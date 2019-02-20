package co.uk.fluxanoia.entity;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import co.uk.fluxanoia.control.Controller.InputType;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.map.Terrain;
import co.uk.fluxanoia.map.Tile;
import co.uk.fluxanoia.map.Tile.TileType;

// The GravityEntity class, adds gravity based collision to the entity
public abstract class GravityEntity extends Entity {

	// The amplifier on the acceleration when reversing
	private double X_REVERSE_MOD;
	// The terminal x velocities on ground and in air
	private double TERMINAL_RUN;
	private double TERMINAL_DRIFT;
	// The terminal y velocity
	private double TERMINAL_FALL;
	// The jumping power
	private double JUMP_POWER;
	// The x and y acceleration values
	private double ACCEL_X, ACCEL_Y;
	// The dampening on x movement when a button isn't pressed
	private double FRICTION_X;
	// The default amount of jumps
	private int JUMPS;

	// Whether the entity is near the edge
	private boolean teeter;
	// Whether the entity is turning or not
	private boolean turning;
	// Whether the entity is naturally slowing down
	private boolean frictioned;
	// Whether the entity is grounded or not
	private boolean grounded;
	// Whether the entity has jumped since the key was pressed
	private boolean jumped;
	// The amount of jumps remaining
	private int jumps;

	// Constructs a GravityEntity
	public GravityEntity(Display display, Terrain terrain, int x, int y, int w,
			int h, double x_rev, double acc_x, double acc_y, double ter_r, double ter_d, double ter_f,
			double j_pow, double fri_x, int jumps) {
		super(display, terrain, x, y, w, h);
		// Assign values
		X_REVERSE_MOD = x_rev;
		TERMINAL_RUN = ter_r;
		TERMINAL_DRIFT = ter_d;
		TERMINAL_FALL = ter_f;
		JUMP_POWER = j_pow;
		FRICTION_X = fri_x;
		ACCEL_X = acc_x;
		ACCEL_Y = acc_y;
		JUMPS = jumps;
		// Initialise values
		this.x_vel = this.y_vel = 0;
		this.grounded = false;
		this.jumped = false;
		this.turning = false;
		this.frictioned = false;
		this.teeter = false;
		this.jumps = 0;
	}

	// Updates and draws the gravity entity
	public abstract void update();
	public abstract void draw(Graphics2D g);

	// Updates the collision of the gravity entity
	public boolean updateCollision(ArrayList<Tile> tiles) {
		
		// VELOCITIES
		
		// If a single left/right key is held
		if (this.getController().isHeld(InputType.LEFT)
				^ this.getController().isHeld(InputType.RIGHT)) {
			// Get the sign for the direction of the acceleration
			double accel_mod = 1;
			this.frictioned = false;
			if (this.getController().isHeld(InputType.LEFT)) accel_mod *= -1;
			// If the acceleration is reversing, modify it
			if (Math.signum(accel_mod) != Math.signum(x_vel)) {
				accel_mod *= X_REVERSE_MOD;
				turning = true;
			} else {
				turning = false;
			}
			// Add to the velocity
			x_vel += ACCEL_X * accel_mod;
			// Cap the velocity
			double cap = (grounded) ? TERMINAL_RUN : TERMINAL_DRIFT;
			if (Math.abs(x_vel) > cap) {
				x_vel = cap * Math.signum(x_vel);
			}
		} else {
			// If there's no lateral button presses, decrease the speed
			this.frictioned = true;
			x_vel *= FRICTION_X;
			if (Math.abs(x_vel) < 0.1) x_vel = 0;
		}
		// If the up key is pressed and it has been released
		if (this.getController().isHeld(InputType.UP) && !jumped) {
			// If there are jumps remaining
			if (jumps > 0) {
				// Let the player jump
				jumped = true;
				grounded = false;
				y_vel = -JUMP_POWER;
				jumps--;
			}
		}
		// Update jumped if the up key is released
		if (!this.getController().isHeld(InputType.UP)) jumped = false;
		// Update the y_vel and cap it
		y_vel += ACCEL_Y;
		if (y_vel > TERMINAL_FALL) y_vel = TERMINAL_FALL;
		
		// COLLISIONS
		
		// Get the current and next boxes
		double new_x = x + x_vel;
		double new_y = y + y_vel;
		Rectangle curr_box = this.getHitbox();
		Rectangle next_box = this.getSpecificHitbox(new_x, new_y);
		// Get the top left and bottom right grid references
		int min_x, min_y, max_x, max_y;
		Rectangle encap = Drawable.encapsulate(new Rectangle[] {
				curr_box, next_box
		});
		min_x = (int) Math.floor(encap.getX() / (double) Terrain.GRID_SIZE);
		min_y = (int) Math.floor(encap.getY() / (double) Terrain.GRID_SIZE); 
		max_x = (int) Math.ceil(encap.getMaxX() / (double) Terrain.GRID_SIZE); 
		max_y = (int) Math.ceil(encap.getMaxY() / (double) Terrain.GRID_SIZE);
		// Get the tiles in the vicinity
		ArrayList<Tile> subTiles = new ArrayList<>();
		for (Tile t : tiles) {
			if (t.getType() == TileType.NO_COLLIDE) continue;
			if (t.getCellX() < min_x || t.getCellX() > max_x) continue;
			if (t.getCellY() < min_y || t.getCellY() > max_y) continue;
			subTiles.add(t);
		}
		// Get the points surronding the next hitbox
		Point[] top = getTopPoints(next_box);
		Point[] bottom = getBottomPoints(next_box);
		Point[] left = getLeftPoints(next_box);
		Point[] right = getRightPoints(next_box);
		// Iterate through the tiles
		Rectangle tileBounds;
		boolean hasGrounded = false;
		for (Tile t : subTiles) {
			// If the tile intersects with the box
			tileBounds = t.getBounds();
			if (next_box.intersects(tileBounds)) {
				// If moving horizontally...
				if (x_vel != 0) {
					// Check if moving right and there's a point collision...
					if (containsAny(tileBounds, right) && x_vel > 0) {
						// If there's no tile where we are about to move...
						if (!isTile(subTiles, t.getCellX() - 1, t.getCellY())) {
							new_x = tileBounds.getX() - Math.floor(next_box.getBounds().getWidth() / 2);
							x_vel = 0;
						}
					}
					// Check if moving left and there's a point collision...
					if (containsAny(tileBounds, left) && x_vel < 0) {
						// If there's no tile where we are about to move...
						if (!isTile(subTiles, t.getCellX() + 1, t.getCellY())) {
							new_x = tileBounds.getMaxX() + Math.floor(next_box.getBounds().getWidth() / 2);
							x_vel = 0;
						}
					}
				}
				// If moving vertically...
				if (y_vel != 0) {
					// Check if moving down and there's a point collision...
					if (containsAny(tileBounds, bottom) && y_vel > 0) {
						// If there's no tile where we are about to move...
						if (!isTile(subTiles, t.getCellX(), t.getCellY() - 1)) {
							new_y = tileBounds.getY() + 1 - (next_box.getBounds().getHeight() / 2);
							y_vel = 0;
							hasGrounded = true;
							jumps = JUMPS;
						}
					}
					// Check if moving up and there's a point collision...
					if (containsAny(tileBounds, top) && y_vel < 0) {
						// If there's no tile where we are about to move...
						if (!isTile(subTiles, t.getCellX(), t.getCellY() + 1)) {
							new_y = tileBounds.getMaxY() + Math.floor(next_box.getBounds().getHeight() / 2);
							y_vel = 0;
						}
					}
				}
			}
		}
		// Update the grounded value
		grounded = hasGrounded;
		// Set the new x and y values
		boolean moved = (x != new_x || y != new_y);
		x = new_x;
		y = new_y;
		return moved;
		
		/*
		// Hold the pending x and y values
		double new_x = x + x_vel;
		double new_y = y + y_vel;
		Rectangle next_box = this.getHitbox();
		next_box.setLocation((int) (next_box.getX() + x_vel),
				(int) (next_box.getY() + y_vel));
		// Get the points surrounding the player
		Point[] top = getTopPoints(next_box);
		Point[] bottom = getBottomPoints(next_box);
		Point[] left = getLeftPoints(next_box);
		Point[] right = getRightPoints(next_box);
		// Iterate through all the platforms
		Rectangle tileBounds;
		// Tracking the contained bottom points
		boolean[] contained = new boolean[bottom.length];
		for (int i = 0; i < contained.length; i++) contained[i] = false;
		for (Tile t : tiles) {
			// If it's not collidable, continue
			if (t.getType() == TileType.NO_COLLIDE) continue;
			// Get the bounds of the platform
			tileBounds = t.getBounds();
			// If the platform intersects with the box
			if (next_box.intersects(tileBounds)) {
				// Prepare the sides according to the type of platform
				ArrayList<Integer> containing = containing(tileBounds, bottom);
				for (Integer i : containing) contained[i] = true;
				if (containsAny(tileBounds, bottom) && y_vel >= 0) {
					new_y = tileBounds.getY() + 1
							- Math.floor(next_box.getBounds().getHeight() / 2);
					y_vel = 0;
					grounded = true;
					jumps = JUMPS;
				}
				if (containsAny(tileBounds, top) && y_vel <= 0) {
					new_y = tileBounds.getMaxY()
							+ next_box.getBounds().getHeight() / 2;
					y_vel = 0;
				}
				if (containsAny(tileBounds, left) && x_vel <= 0) {
					new_x = tileBounds.getMaxX()
							+ Math.floor(next_box.getBounds().getWidth() / 2);
					x_vel = 0;
				}
				if (containsAny(tileBounds, right) && x_vel >= 0) {
					new_x = tileBounds.getX() + 1
							- Math.floor(next_box.getBounds().getWidth() / 2);
					x_vel = 0;
					break;
				}
			}
		}
		// Check teeter
		int count = 0;
		for (int i = 0; i < contained.length; i++) if (contained[i]) count++;
		teeter = count != 0 && count != contained.length;
		grounded = !(count == 0);
		// Set the new x and y values
		boolean moved = (x != new_x || y != new_y);
		x = new_x;
		y = new_y;
		return moved;
		*/
	}
	
	// Returns whether there's a tile at the position
	private boolean isTile(ArrayList<Tile> tiles, int x, int y) {
		for (Tile t : tiles) if (x == t.getCellX() && y == t.getCellY()) return true;
		return false;
	}

	// Returns whether any of the points are in the rectangle
	private boolean containsAny(Rectangle r, Point[] ps) {
		for (int i = 0; i < ps.length; i++)
			if (r.contains(ps[i])) return true;
		return false;
	}
	
	// Returns the top two points of the player
	private Point[] getTopPoints(Rectangle r) {
		return new Point[] { new Point((int) r.getX() + 5, (int) r.getY()),
				new Point((int) r.getMaxX() - 5, (int) r.getY()),
				new Point((int) (r.getCenterX() - (r.getWidth() / 4)),
						(int) r.getY()),
				new Point((int) (r.getCenterX() + (r.getWidth() / 4)),
						(int) r.getY()) };
	}

	// Returns the bottom two points of the player
	private Point[] getBottomPoints(Rectangle r) {
		return new Point[] { new Point((int) r.getX() + 5, (int) r.getMaxY()),
				new Point((int) r.getMaxX() - 5, (int) r.getMaxY()),
				new Point((int) (r.getCenterX() - (r.getWidth() / 4)),
						(int) r.getMaxY()),
				new Point((int) (r.getCenterX() + (r.getWidth() / 4)),
						(int) r.getMaxY()) };
	}

	// Returns the left two points of the player
	private Point[] getLeftPoints(Rectangle r) {
		return new Point[] { new Point((int) r.getX(), (int) r.getCenterY()),
				new Point((int) r.getX(),
						(int) (r.getCenterY() - r.getHeight() / 4)),
				new Point((int) r.getX(),
						(int) (r.getCenterY() + r.getHeight() / 4)) };
	}

	// Returns the right two points of the player
	private Point[] getRightPoints(Rectangle r) {
		return new Point[] { new Point((int) r.getMaxX(), (int) r.getCenterY()),
				new Point((int) r.getMaxX(),
						(int) (r.getCenterY() - r.getHeight() / 4)),
				new Point((int) r.getMaxX(),
						(int) (r.getCenterY() + r.getHeight() / 4)) };
	}

	// Returns whether the entity is grounded or not
	public boolean isGrounded() {
		return grounded;
	}
	
	// Returns whether the entity is near an edge or not
	public boolean isTeetering() {
		return teeter;
	}
	
	// Returns the entity x and y velocities
	public double getVelocityX() {
		return x_vel;
	}
	public double getVelocityY() {
		return y_vel;
	}
	
	// Returns whether the entity is attempting to change direction
	public boolean isTurning() {
		return turning;
	}
	
	// Returns whether the entity is naturally slowing
	public boolean isFrictioned() {
		return frictioned;
	}
	
}
