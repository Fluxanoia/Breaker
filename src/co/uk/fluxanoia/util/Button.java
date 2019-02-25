package co.uk.fluxanoia.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import co.uk.fluxanoia.main.ErrorHandler;

// The Button, an individual button - usually managed by ButtonManager
public class Button {
	
	// The ID of the button
	private int ID;
	// The dimensions of the button
	private Dimension dimensions;
	// The text of the button
	private String text;
	// The x, y values of the button
	private int x, y;
	// The x, y tweens that are additive to the position
	private Tween x_tween, y_tween;
	// Whether the button is being hovered or not
	private boolean hovered;
	// Whether the button has been updated or not
	private boolean updated;
	// Whether the button is currently pressed or not
	private boolean pressed;
	// Whether the button has been activated or not
	private boolean activated;
	// Whether the button has been entered or not
	private boolean entered;
	// Whether the button has been exited or not
	private boolean exited;
	
	// Constructs a Button
	public Button(int ID, Dimension dimensions, String text, int x, int y) {
		// Check for null values
		ErrorHandler.checkNull(dimensions, "A Button was given a null dimension.");
		ErrorHandler.checkNull(text, "A Button was given a null text.");
		// Assign values
		this.ID = ID;
		this.dimensions = dimensions;
		this.text = text;
		this.x = x;
		this.y = y;
		this.x_tween = new Tween(0);
		this.y_tween = new Tween(0);
		// Initialises values
		this.hovered = false;
		this.pressed = false;
		this.activated = false;
		this.entered = false;
		this.exited = false;
	}
	
	// Updates the button (with the mouse position)
	public void update(Point mouse_position) {
		ErrorHandler.checkNull(mouse_position, "A Button was given a null mouse position.");
		// Check if the mouse is over the button
		if (this.getBounds().contains(mouse_position)) {
			if (!hovered) {
				this.entered = true;
				this.hovered = true;
				this.updated = true;
			}
		} else {
			if (hovered) {
				this.exited = true;
				this.hovered = false;
				this.updated = true;
			}
			if (pressed) {
				this.pressed = false;
				this.updated = true;
			}
		}
		// Update the x, y values
		this.x_tween.update();
		this.y_tween.update();
		// Checked whether the x, y values have moved
		this.updated |= this.x_tween.dropMoved();
		this.updated |= this.y_tween.dropMoved();
	}
	
	// Draws the button
	public void draw(Graphics2D g, Font font, Color text_color) {
		ErrorHandler.checkNull(font, "A Button was given a null font.");
		ErrorHandler.checkNull(text_color, "A Button was given a null text colour.");
		if (hovered) {
			g.setColor(new Color(255, 255, 255, 40));
		}
		if (pressed) {
			g.setColor(new Color(255, 255, 255, 120));
		}
		if (hovered || pressed) g.fill(this.getBounds());

		g.setColor(text_color);
		g.setFont(font);
		double text_buffer_x = (dimensions.getWidth() - g.getFontMetrics().stringWidth(text)) / 2;
		double text_buffer_y = dimensions.getHeight() - (g.getFontMetrics().getHeight() / 2);
		g.drawString(
				text, 
				(int) (x + x_tween.value() + text_buffer_x),
				(int) (y + y_tween.value() + text_buffer_y)
		);
	}
	
	// Returns whether the button has updated and sets it to false
	public boolean dropUpdated() {
		if (this.updated) {
			this.updated = false;
			return true;
		}
		return false;
	}
	
	// Notifies the button of a mouse press
	public void pressed(int x, int y) {
		this.pressed = this.getBounds().contains(new Point(x, y));
		this.updated = true;
	}
	
	// Notifies the button of a mouse release
	public void released(int x, int y) {
		if (this.pressed) {
			this.pressed = false;
			this.activated = true;
			this.updated = true;
		}
	}
	
	// Returns whether the button has been activated and sets the value to false
	public boolean dropActivated() {
		if (this.activated) {
			this.activated = false;
			return true;
		}
		return false;
	}

	// Returns whether the button has been entered and sets the value to false
	public boolean dropEntered() {
		if (this.entered) {
			this.entered = false;
			return true;
		}
		return false;
	}

	// Returns whether the button has been exited and sets the value to false
	public boolean dropExited() {
		if (this.exited) {
			this.exited = false;
			return true;
		}
		return false;
	}
	
	// Returns the bounds of the button
	public Rectangle getBounds() {
		return new Rectangle(
				new Point((int) x_tween.value() + x, (int) y_tween.value() + y), dimensions);
	}
	
	// Returns the button ID
	public int getID() {
		return ID;
	}
	
	// Returns the tween corresponding to the x value
	public Tween getTweenX() {
		return x_tween;
	}
	
	// Returns the tween corresponding to the y value
	public Tween getTweenY() {
		return y_tween;
	}

}
