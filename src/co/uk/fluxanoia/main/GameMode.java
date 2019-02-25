package co.uk.fluxanoia.main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.map.MapTool;
import co.uk.fluxanoia.state.StateManager;

// The abstract class that allows different game modes
public abstract class GameMode {

	// The enum representing the different modes
	public static final Mode DEFAULT_MODE = Mode.GAME;
	public enum Mode { 
		MAP_EDIT(KeyEvent.VK_M), 
		GAME(KeyEvent.VK_ENTER);
		
		private int key;
		Mode(int key) {
			this.key = key;
		}
		public int getKey() { return key; }
		
		public static GameMode getGameMode(Listener l, Display display) {
			ErrorHandler.checkNull(l, "The Mode enum was given a null listener.");
			Mode[] modes = Mode.values();
			for (int i = 0; i < modes.length; i++) {
				if (l.isKeyPressed(modes[i].getKey())) return getGameMode(modes[i], display);
			}
			return null;
		}
		public static GameMode getGameMode(Mode m, Display display) {
			ErrorHandler.checkNull(m, "The Mode enum was given a null mode.");
			switch (m) {
			case GAME:
				return new StateManager(display);
			case MAP_EDIT:
				return new MapTool(display);
			}
			return null;
		}
	};
	
	// The display for the game
	private Display display;
	
	// Constructs a game mode
	public GameMode(Display display) {
		ErrorHandler.checkNull(display, "A GameMode was given a null display.");
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
