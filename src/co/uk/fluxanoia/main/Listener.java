package co.uk.fluxanoia.main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

// The Listener, records and processes input
public class Listener implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {

	// The last recorded mouse x and y position
	private int mouse_x;
	private int mouse_y;
	// Whether the mouse has moved since the last check
	private boolean mouseMoved;
	// The amount that the mouse wheel has scrolled
	private double scrollAmount;
	// The hash map containing whether the mouse is held or not
	private HashMap<Integer, Boolean> heldMouseButtons;
	// The hash map containing whether the mouse has been pressed or not
	private HashMap<Integer, Boolean> pressedMouseButtons;
	// The hash map containing whether the mouse has been released or not
	private HashMap<Integer, Boolean> releasedMouseButtons;
	
	// The string of typed character
	private String inputString;
	// The hash map containing whether the key is held or not
	private HashMap<Integer, Boolean> heldKeyButtons;
	// The hash map containing whether the key has been pressed or not
	private HashMap<Integer, Boolean> pressedKeyButtons;
	// The hash map containing whether the key has been released or not
	private HashMap<Integer, Boolean> releasedKeyButtons;
	
	// The scale factor of the mouse position
	private double mouse_x_sf;
	private double mouse_y_sf;	
	
	// Constructs a Listener
	public Listener() {
		// Initialises the variables with default values
		this.mouse_x = 0;
		this.mouse_y = 0;
		this.scrollAmount = 0;
		this.mouseMoved = false;
		this.heldMouseButtons = new HashMap<Integer, Boolean>();
		this.pressedMouseButtons = new HashMap<Integer, Boolean>();
		this.releasedMouseButtons = new HashMap<Integer, Boolean>();
		
		this.inputString = "";
		this.heldKeyButtons = new HashMap<Integer, Boolean>();
		this.pressedKeyButtons = new HashMap<Integer, Boolean>();
		this.releasedKeyButtons = new HashMap<Integer, Boolean>();
		
		this.mouse_x_sf = ((double) Main.DRAW_WIDTH) / ((double) Main.WINDOW_WIDTH);
		this.mouse_y_sf = ((double) Main.DRAW_HEIGHT) / ((double) Main.WINDOW_HEIGHT);
	}
	
	// ------------------------ LISTENER FUNCTIONS
	
	// Updates the hash maps
	public void update() {
		// Reset all the pressed and released buttons
		for (Integer i : pressedMouseButtons.keySet()) {
			pressedMouseButtons.replace(i, false);
		}
		for (Integer i : releasedMouseButtons.keySet()) {
			releasedMouseButtons.replace(i, false);
		}
		for (Integer i : pressedKeyButtons.keySet()) {
			pressedKeyButtons.replace(i, false);
		}
		for (Integer i : releasedKeyButtons.keySet()) {
			releasedKeyButtons.replace(i, false);
		}
	}
	
	// Gets the mouse position as a point
	public Point getMousePosition() {
		return new Point(getMouseX(), getMouseY());
	}
	
	// Empties the input string
	public void flushInputString() {
		this.inputString = "";
	}
	
	// Resets the mouse wheel rotation that has accumulated
	public void resetWheelRotation() {
		scrollAmount = 0;
	}
	
	// Returns the mouse x value
	public int getMouseX() {
		return (int) (((double) this.mouse_x) * mouse_x_sf);
	}
		
	// Returns the mouse y value
	public int getMouseY() {
		return (int) (((double) this.mouse_y) * mouse_y_sf);
	}
	
	// Returns the raw mouse x value
	public int getRawMouseX() {
		return this.mouse_x;
	}
		
	// Returns the mouse y value
	public int getRawMouseY() {
		return this.mouse_y;
	}
	
	// Returns the mouse scroll value
	public double getScrollAmount() {
		return this.scrollAmount;
	}
	
	// Drops the mouse moved value
	public boolean dropMouseMoved() {
		if (mouseMoved) {
			mouseMoved = false;
			return true;
		}
		return false;
	}
	
	// Drops the mouse scroll value
	public double dropScrollAmount() {
		double scroll = getScrollAmount();
		this.resetWheelRotation();
		return scroll;
	}
	
	// Returns whether a mouse is pressed or not
	public boolean isMousePressed(Integer k) {
		if (k == null) return false;
		// If the key isn't in the map, return false, otherwise check
		if (!pressedMouseButtons.containsKey(k)) return false;
		return pressedMouseButtons.get(k);
	}
	
	// Returns whether a mouse is held or not
	public boolean isMouseHeld(Integer k) {
		if (k == null) return false;
		// If the key isn't in the map, return false, otherwise check
		if (!heldMouseButtons.containsKey(k)) return false;
		return heldMouseButtons.get(k);
	}
	
	// Returns whether a mouse is released or not
	public boolean isMouseReleased(Integer k) {
		if (k == null) return false;
		// If the key isn't in the map, return false, otherwise check
		if (!releasedMouseButtons.containsKey(k)) return false;
		return releasedMouseButtons.get(k);
	}
	
	// Returns the input string
	public String getInputString() {
		return this.inputString;
	}
	
	// Returns whether a key is pressed or not
	public boolean isKeyPressed(Integer k) {
		if (k == null) return false;
		// If the key isn't in the map, return false, otherwise check
		if (!pressedKeyButtons.containsKey(k)) return false;
		return pressedKeyButtons.get(k);
	}
	
	// Returns whether a key is held or not
	public boolean isKeyHeld(Integer k) {
		if (k == null) return false;
		// If the key isn't in the map, return false, otherwise check
		if (!heldKeyButtons.containsKey(k)) return false;
		return heldKeyButtons.get(k);
	}
	
	// Returns whether a key is released or not
	public boolean isKeyReleased(Integer k) {
		if (k == null) return false;
		// If the key isn't in the map, return false, otherwise check
		if (!releasedKeyButtons.containsKey(k)) return false;
		return releasedKeyButtons.get(k);
	}
	
	// ------------------------ KEY EVENTS

	// Called when a key is pressed
	public void keyPressed(KeyEvent e) {
		if (heldKeyButtons.containsKey(e.getKeyCode()) && 
				heldKeyButtons.get(e.getKeyCode()) == true) return;
		pressedKeyButtons.put(e.getKeyCode(), true);
		heldKeyButtons.put(e.getKeyCode(), true);
	}

	// Called when a key is released
	public void keyReleased(KeyEvent e) {
		releasedKeyButtons.put(e.getKeyCode(), true);
		heldKeyButtons.put(e.getKeyCode(), false);
	}

	// Called when a key that can be printed is pressed
	public void keyTyped(KeyEvent e) {
		this.inputString += e.getKeyChar();
	}
	
	// ------------------------ MOUSE EVENTS
	
	// Called when a mouse button is clicked
	public void mouseClicked(MouseEvent e) {
	}
	
	// Called when the mouse enters the component
	public void mouseEntered(MouseEvent e) {
	}

	// Called when the mouse exits the component
	public void mouseExited(MouseEvent e) {
	}

	// Called when a mouse button is pressed
	public void mousePressed(MouseEvent e) {
		// Updates the hash maps
		if (heldMouseButtons.containsKey(e.getButton()) && 
				heldMouseButtons.get(e.getButton()) == true) return;
		pressedMouseButtons.put(e.getButton(), true);
		heldMouseButtons.put(e.getButton(), true);
	}

	// Called when a mouse button is released
	public void mouseReleased(MouseEvent e) {
		// Updates the hash maps
		heldMouseButtons.put(e.getButton(), false);
		releasedMouseButtons.put(e.getButton(), true);
	}
	
	// Called when the mouse moves with a mouse button held
	public void mouseDragged(MouseEvent e) {
		// Updates the mouse position
		this.mouse_x = e.getX();
		this.mouse_y = e.getY();
		this.mouseMoved = true;
	}

	// Called when the mouse moves
	public void mouseMoved(MouseEvent e) {
		// Updates the mouse position
		this.mouse_x = e.getX();
		this.mouse_y = e.getY();
		this.mouseMoved = true;
	}

	// Called when the mouse wheel moves
	public void mouseWheelMoved(MouseWheelEvent e) {
		// If it's a unit scroll (not PGUP, PGDN)
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			// Updates the scrolled value
			this.scrollAmount += e.getPreciseWheelRotation();
		}
	}
			
}
