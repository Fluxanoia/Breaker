package co.uk.fluxanoia.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

// The abstract Drawable class, to allow classes
// to be drawn by the Display
public abstract class Drawable {

	// The area that should be redrawn for the component and the last drawn
	private Rectangle oldClipBounds, clipBounds;
	
	// Constructs a Drawable
	public Drawable() {
		// Assigns values
		this.setClipBounds(new Rectangle(0, 0, 0, 0));
	}
	
	// For drawing the component
	public abstract void draw(Graphics2D g);
	
	// Updates the new clip bounds and pushes the old one
	public void pushClipBounds(Rectangle clip) {
		if (Display.area(clipBounds) == 0) {
			this.clipBounds = clip;
		} else {
			if (Display.area(oldClipBounds) == 0) {
				this.oldClipBounds = this.clipBounds;
			} else {
				this.oldClipBounds = encapsulate(new Rectangle[] { oldClipBounds, clipBounds });
			}
			this.clipBounds = clip;
		}
	}
	
	// Sets the clip bounds to a single rectangle
	public void setClipBounds(Rectangle clipBounds) {
		this.clipBounds = clipBounds;
		this.oldClipBounds = clipBounds;
	}
	
	// Returns the current clip bounds
	public Rectangle dropClipBounds() {
		if (Display.area(clipBounds) == 0) {
			Rectangle clip = this.oldClipBounds;
			this.oldClipBounds = new Rectangle(0, 0, 0, 0);
			return clip;
		}
		if (Display.area(oldClipBounds) == 0) {
			Rectangle clip = this.clipBounds;
			this.oldClipBounds = this.clipBounds;
			this.clipBounds = new Rectangle(0, 0, 0, 0);
			return clip;
		}
		Rectangle clip = encapsulate(new Rectangle[] { clipBounds, oldClipBounds });
		this.oldClipBounds = this.clipBounds;
		this.clipBounds = new Rectangle(0, 0, 0, 0);
		return clip;
	}
	
	// Returns the rectangle containing both the input rectangles
	public static Rectangle encapsulate(Rectangle[] rs) {
		if (rs.length == 0) return new Rectangle(0, 0, 0, 0);
		int x = (int) rs[0].getX(), 
			y = (int) rs[0].getY(), 
			end_x = (int) rs[0].getMaxX(), 
			end_y = (int) rs[0].getMaxY();
		for (int i = 1; i < rs.length; i++) {
			if (x > rs[i].getX()) x = (int) rs[i].getX();
			if (y > rs[i].getY()) y = (int) rs[i].getY();
			if (end_x < rs[i].getMaxX()) end_x = (int) rs[i].getMaxX();
			if (end_y < rs[i].getMaxY()) end_y = (int) rs[i].getMaxY(); 
		}
		return new Rectangle(x, y, end_x - x, end_y - y);
	}
	
	// The same as the previous method but for ArrayLists
	public static Rectangle encapsulate(ArrayList<Rectangle> rs) {
		Rectangle[] rss = new Rectangle[rs.size()];
		for (int i = 0; i < rss.length; i++) rss[i] = rs.get(i);
		return encapsulate(rss);
	}

}
