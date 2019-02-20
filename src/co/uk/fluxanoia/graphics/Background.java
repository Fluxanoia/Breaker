package co.uk.fluxanoia.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.util.Tween;
import co.uk.fluxanoia.util.Tween.TweenType;

// The Background class, is a general, scaled, window-sized image
public class Background extends Drawable {

	// The image for the background
	private BufferedImage image;
	
	// The values for the scale and translation for the background
	private Tween scale;
	
	// Constructs the background with varying degrees of specificity
	public Background(Display display, String path, double scale, boolean relative) {
		// Construct the Drawable
		super();
		init(display, path, scale, relative);
	}
	public Background(Display display, String path, double scale) {
		this(display, path, scale, false);
	}
	public Background(Display display, String path) {
		this(display, path, 1, false);
	}
	
	// Initialises the backgrounds values
	private void init(Display display, String path, double scale, boolean relative) {
		this.image = display.getResourceManager().getImage(path);
		this.scale = new Tween(scale);
		if (relative) this.setRelativeScale(scale);
	}

	// Updates the background
	public void update() {
		scale.update();
		if (scale.dropMoved()) this.pushClipBounds(getClipBounds());
	}
	
	// Draws the background
	public void draw(Graphics2D g) {
		g.drawImage(
				image, 
				(int) getX(), 
				(int) getY(), 
				(int) (image.getWidth() * scale.value()),
				(int) (image.getHeight() * scale.value()), 
				null
		);
	}
	
	// Gets the clip bounds of the background
	private Rectangle getClipBounds() {
		return new Rectangle(
				(int) getX(), 
				(int) getY(), 
				(int) (image.getWidth() * scale.value()),
				(int) (image.getHeight() * scale.value())
		);
	}
	
	// Sets both scales
	public void setScale(double s) {
		this.scale.set(s);
	}
	
	// Moves both scales
	public void moveScale(TweenType tweenType, double end, int duration, int hold) {
		this.scale.move(tweenType, end, duration, hold);
	}
	
	// Pushes both scales
	public void pushScale(TweenType tweenType, double end, int duration, int hold) {
		this.scale.push(tweenType, end, duration, hold);
	}
	
	// Sets both scales
	public void setRelativeScale(double s) {
		s *= ((double) Main.DRAW_WIDTH / (double) image.getWidth());
		this.setScale(s);
	}
	
	// Moves both scales
	public void moveRelativeScale(TweenType tweenType, double end, int duration, int hold) {
		end *= ((double) Main.DRAW_WIDTH / (double) image.getWidth());
		this.moveScale(tweenType, end, duration, hold);
	}
	
	// Pushes both scales
	public void pushRelativeScale(TweenType tweenType, double end, int duration, int hold) {
		end *= ((double) Main.DRAW_WIDTH / (double) image.getWidth());
		this.pushScale(tweenType, end, duration, hold);
	}
	
	// Whether both scales have arrived
	public boolean hasArrived() {
		return scale.hasArrived();
	}

	// Returns the x value of the background
	private double getX() {
		return (Main.DRAW_WIDTH - image.getWidth() * scale.value()) / 2; 
	}
	
	// Returns the y value of the background
	private double getY() {
		return (Main.DRAW_HEIGHT - image.getHeight() * scale.value()) / 2; 
	}
	
}
