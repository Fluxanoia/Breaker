package co.uk.fluxanoia.graphics;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

// The DrawableLayer class, allows for independent state layering
public abstract class DrawableLayer extends Drawable {

	// The Layer enum corresponding to the 3 main layer arrays
	public enum Layer { BG, MD, FG };
	
	// The list of drawable components for the:
	// background, midground and, foreground
	private ArrayList<Drawable> bg_components;
	private ArrayList<Drawable> md_components;
	private ArrayList<Drawable> fg_components;
	
	// Constructs the DrawableLayer
	public DrawableLayer() {
		// Construct the Drawable
		super();
		// Initialises values
		this.bg_components = new ArrayList<Drawable>();
		this.md_components = new ArrayList<Drawable>();
		this.fg_components = new ArrayList<Drawable>();
	}

	// Draws the layer itself
	public abstract void draw(Graphics2D g);
	
	// Updates the drawable layer
	public void updateLayer() {
		// Create the list that will hold all of the clip bounds
		ArrayList<Rectangle> bounds = new ArrayList<Rectangle>();
		// Name a variable to temporarily hold the bounds
		Rectangle r;
		// Compile the lists into one ordered array
		ArrayList<Drawable>[] arrs = new ArrayList[3];
		arrs[0] = bg_components;
		arrs[1] = md_components;
		arrs[2] = fg_components;
		// Iterate through all the components, taking the necessary bounds
		for (int i = 0; i < 3; i++) for (Drawable d : arrs[i]) {
			r = d.dropClipBounds();
			// If the bounds are not empty
			if (Display.area(r) == 0) continue;
			bounds.add(r);
		}
		// Get the final bounds
		r = encapsulate(bounds);
		// Push the final bounds if they are not empty
		if (Display.area(r) != 0) {
			this.pushClipBounds(r);
		}
	}
	
	// Adds a component to the desired layer
	public void addComponent(Layer l, Drawable d) {
		// Go through the layer possibilities and add the component
		// to the corresponding layer
		switch (l) {
		case BG:
			bg_components.add(d);
			break;
		case MD:
			md_components.add(d);
			break;
		case FG:
			fg_components.add(d);
			break;
		}
	}
	
	// Draws the components of the layer
	public void drawComponents(Graphics2D g) {
		// Draw all the components in order
		for (Drawable d : bg_components) d.draw(g);
		for (Drawable d : md_components) d.draw(g);
		for (Drawable d : fg_components) d.draw(g);
	}
	
}
