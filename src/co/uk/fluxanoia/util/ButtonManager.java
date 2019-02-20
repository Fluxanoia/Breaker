package co.uk.fluxanoia.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.main.AudioManager;
import co.uk.fluxanoia.main.Main;

// The ButtonManager class, manages a collection of buttons
public class ButtonManager extends Drawable {
	
	// The value corresponding to the centre of the axis
	public static final int CENTRE_VALUE = -1024;
	// The default colour of the text
	public static final Color DEFAULT_TEXT_COLOR = new Color(240, 240, 240);
	// The default font of the button
	public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
	
	// The display (for adding buttons)
	private Display display;
	// The list of buttons
	private ArrayList<Button> buttons;
	// The colour of the text of the buttons
	private Color text_color;
	// The font of the buttons
	private Font font;
	// The list of button events
	private ArrayList<Integer> button_queue;
	
	// Constructs a ButtonManager
	public ButtonManager(Display display) {
		// Construct the Drawable
		super();
		// Assigns values
		this.display = display;
		// Initialise values
		this.button_queue = new ArrayList<Integer>();
		this.buttons = new ArrayList<Button>();
		this.font = DEFAULT_FONT;
		this.text_color = DEFAULT_TEXT_COLOR;
	}
	
	// Updates the button manager
	public void update() {
		// Initialises the list that will contain the clip bounds
		ArrayList<Rectangle> bounds = new ArrayList<Rectangle>();
		// Iterate through the buttons
		for (Button b : buttons) {
			// Press the buttons if necessary
			if (display.getListener().isMousePressed(MouseEvent.BUTTON1)) {
				b.pressed(display.getListener().getMouseX(), display.getListener().getMouseY());
			}
			// Notify the button of a mouse release
			if (display.getListener().isMouseReleased(MouseEvent.BUTTON1)) {
				b.released(display.getListener().getMouseX(), display.getListener().getMouseY());
			}
			// Updates the buttons
			b.update(display.getListener().getMousePosition());
			// If the button requires a redraw then get its clip bounds
			if (b.dropUpdated()) bounds.add(b.getBounds());
			if (b.dropActivated()) {
				display.getAudioManager().playSFX(AudioManager.BUTTON_SELECT);
				button_queue.add(b.getID());
			}
			if (b.dropEntered()) display.getAudioManager().playSFX(AudioManager.BUTTON_HOVER_ENTER);
			if (b.dropExited()) display.getAudioManager().playSFX(AudioManager.BUTTON_HOVER_EXIT);
		}
		// Check if the bounds are not empty
		Rectangle r = encapsulate(bounds);
		if (Display.area(r) != 0) {
			// Push the bounds containing the bounds required
			this.pushClipBounds(r);
		}
	}
	
	// Draws the button
	public void draw(Graphics2D g) {
		// Iterate through all the buttons and draw them
		for (Button b : buttons) b.draw(g, font, text_color);
	}
	
	// Adds a new button
	public void addButton(int ID, Dimension dimensions, String text, int x, int y) {
		// Create the button
		if (x == CENTRE_VALUE) x = (int) ((Main.DRAW_WIDTH - dimensions.getWidth()) / 2);
		if (y == CENTRE_VALUE) y = (int) ((Main.DRAW_HEIGHT - dimensions.getHeight()) / 2);
		Button b = new Button(ID, dimensions, text, x, y);
		// Add it to our array
		buttons.add(b);
	}

	// Returns the x tween for all the buttons
	public Tween[] getXTweens() {
		Tween[] tweens = new Tween[buttons.size()];
		for (int i = 0; i < tweens.length; i++) {
			tweens[i] = buttons.get(i).getTweenX();
		}
		return tweens;
	}
	
	// Returns the y tween for all the buttons
	public Tween[] getYTweens() {
		Tween[] tweens = new Tween[buttons.size()];
		for (int i = 0; i < tweens.length; i++) {
			tweens[i] = buttons.get(i).getTweenY();
		}
		return tweens;
	}
	
	// Returns the button queue as a list and clears it
	public int[] dropQueue() {
		int[] q = new int[button_queue.size()];
		for (int i = 0; i < q.length; i++) q[i] = button_queue.get(i);
		button_queue.clear();
		return q;
	}
	
}
