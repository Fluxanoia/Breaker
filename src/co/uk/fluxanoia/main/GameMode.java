package co.uk.fluxanoia.main;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import co.uk.fluxanoia.graphics.Display;

// The abstract class that allows different game modes
public abstract class GameMode {

	// The enum representing the different modes
	public enum Mode { MAP_EDIT, GAME };
	
	// The display for the game
	private Display display;
	
	// Constructs a game mode
	public GameMode(Display display) {
		this.display = display;
	}
	
	// For updating the mode
	public abstract void update();
	// For drawing the mode
	public abstract void draw(Graphics2D g);
	// Gets the clip bounds of the active layer
	public abstract Rectangle dropClipBounds();
	// For checking whether it should be closed or not
	public abstract boolean isClosed();
	
	// Returns the display
	public Display getDisplay() { return this.display; }
	
}
