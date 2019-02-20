package co.uk.fluxanoia.map;

import java.awt.Rectangle;

import co.uk.fluxanoia.util.Tween;

// The camera class, represents what is currently being viewed on the terrain
public class Camera {

	// The camera's position
	private Tween x, y;
	// The camera's dimension
	private Tween width, height;
	// Whether the camera has moved since it was last checked
	private boolean moved;
	
	// Constructs a Camera
	public Camera(double x, double y, double width, double height) {
		// Initialise values
		this.x = new Tween(0);
		this.y = new Tween(0);
		this.width = new Tween(0);
		this.height = new Tween(0);
		setPosition(x, y, width, height);
	}
	
	// Sets the camera's position and size
	public void setPosition(double x, double y, double width, double height) {
		this.x.set(x);
		this.y.set(y);
		this.width.set(width);
		this.height.set(height);
		moved = true;
	}
	
	// Updates the values
	public void update() {
		this.x.update();
		this.y.update();
		this.width.update();
		this.height.update();
		moved |= this.x.dropMoved() ||
				 this.y.dropMoved() ||
				 this.width.dropMoved() ||
				 this.height.dropMoved();
	}
	
	// Returns the camera's bounds
	public Rectangle getBounds() {
		return new Rectangle(
				(int) x.value(), 
				(int) y.value(), 
				(int) width.value(), 
				(int) height.value());
	}
	
	// Drops the moved value
	public boolean dropMoved() {
		if (moved) {
			moved = false;
			return true;
		}
		return false;
	}
	
	// Returns the camera's position
	public double getX() { return x.value(); }
	public double getY() { return y.value(); }
	// Returns the camera's position tweens
	public Tween getTweenX() { return x; }
	public Tween getTweenY() { return y; }
	// Returns the camera's dimensions
	public double getWidth() { return width.value(); }
	public double getHeight() { return height.value(); }
	// Returns the camera's dimension tweens
	public Tween getTweenWidth() { return width; }
	public Tween getTweenHeight() { return height; }
	
}
