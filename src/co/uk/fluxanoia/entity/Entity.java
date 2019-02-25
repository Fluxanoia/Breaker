package co.uk.fluxanoia.entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import co.uk.fluxanoia.control.Controller;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.main.ErrorHandler;
import co.uk.fluxanoia.map.Terrain;

// The Entity class, gives a basis for all entities
public abstract class Entity extends Drawable {
	
	// Indexing of all the entities
	public enum EntityIndex {
		PROTAGONIST("protagonist");

		private String id;
		EntityIndex(String id) {
			this.id = id;
		}
		public String getID() { return id; }
		
		// Returns the EntityIndex associated with the id
		public static EntityIndex getIndex(String s) {
			ErrorHandler.checkNull(s, "The EntityIndex enum was given a null input string.");
			EntityIndex[] eis = EntityIndex.values();
			for (int i = 0; i < eis.length; i++) {
				if (s.equals(eis[i].getID())) return eis[i];
			}
			return null;
		}
		// Returns the entity associated with the index
		public static Entity getEntity(String id, Display display, Terrain terrain, int x, int y) {
			return getEntity(getIndex(id), display, terrain, x, y);
		}
		public static Entity getEntity(EntityIndex ei, Display display, Terrain terrain, int x, int y) {
			ErrorHandler.checkNull(ei, "The EntityIndex enum was given a null entity index.");
			switch (ei) {
			case PROTAGONIST:
				return new Protagonist(display, terrain, x, y);
			}
			return null;
		}
	}
	// The entity index of the entity
	private EntityIndex entityIndex;
	
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
	public Entity(EntityIndex entityIndex, Display display, Terrain terrain, int x, int y, int w, int h) {
		// Check for null inputs
		ErrorHandler.checkNull(entityIndex, "An Entity was given a null EntityIndex.");
		ErrorHandler.checkNull(display, "An Entity was given a null Display.");
		ErrorHandler.checkNull(terrain, "An Entity was given a null Terrain");
		// Initialise values
		this.x_vel = this.y_vel = 0;
		// Assign values
		this.entityIndex = entityIndex;
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
		if (controller != null) controller.update();
		return moved;
	}
	
	// Updates the clip bounds of the entity
	public void updateClip() {
		Rectangle b = animator.getBounds(this.getHitbox());
		if (b == null) b = this.getHitbox();
		b = new Rectangle((int) b.getX() - 5,
				(int) b.getY() - 5,
				(int) b.getWidth() + 10,
				(int) b.getHeight() + 10);
		b.translate((int) -terrain.getCamera().getX(), (int) -terrain.getCamera().getY());
		this.pushClipBounds(b);
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
	
	// Returns the entity index
	public EntityIndex getEntityIndex() { return entityIndex; }
	// Returns the controller
	public Controller getController() { return controller; }
	// Returns the display
	protected Display getDisplay() { return display; }
	// Returns the terrain
	protected Terrain getTerrain() { return terrain; }
	// Returns the animator
	protected Animator getAnimator() { return animator; }
	
}
