package co.uk.fluxanoia.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import co.uk.fluxanoia.control.Controller;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.map.Terrain;

// The Entity class, gives a basis for all entities
public abstract class Entity extends Drawable {
	
	// Indexing of all the entities
	public enum EntityIndex {
	}
	
	// The display for the game
	private Display display;
	// The terrain of the game
	private Terrain terrain;
	// The animator of the entity
	private Animator animator;
	// The controller of the entity
	private Controller controller;
	
	// The x and y positions of the entity
	protected double x, y;
	protected double w, h;
	// The velocities of the entity
	protected double x_vel, y_vel;
	
	// Constructs an entity
	public Entity(Display display, Terrain terrain, int x, int y, int w, int h) {
		// Initialise values
		this.x_vel = this.y_vel = 0;
		// Assign values
		this.display = display;
		this.terrain = terrain;
		this.animator = new Animator();
		this.controller = null;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	// Updates the entity
	public abstract void update();
	// Draws the entity
	public abstract void draw(Graphics2D g);

	// Updates the tween and returns whether they have moved or not
	public boolean updateSuper() {
		boolean moved = false;
		moved |= animator.update();
		moved |= animator.dropMoved();
		controller.update();
		return moved;
	}
	
	// Returns the bounds for the entity
	public Rectangle getHitbox() {
		return getSpecificHitbox(x, y);
	}
	public Rectangle getSpecificHitbox(double x, double y) {
		return new Rectangle(
				(int) (x - w / 2),
				(int) (y - h / 2),
				(int) w,
				(int) h
				);
	}
	
	// Sets the controller
	public void setController(Controller c) {
		this.controller = c;
	}
	
	// Returns the display
	public Display getDisplay() { return display; }
	// Returns the terrain
	public Terrain getTerrain() { return terrain; }
	// Returns the animator
	public Animator getAnimator() { return animator; }
	// Returns the controller
	public Controller getController() { return controller; }
	
}
