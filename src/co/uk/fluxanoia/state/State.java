package co.uk.fluxanoia.state;

import java.awt.Graphics2D;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.DrawableLayer;

// The abstract State class, is the basis for all states
public abstract class State extends DrawableLayer {

	// The enum containing all the states and some special cases
	public enum StateType {
		CLOSE(-2), 			// A special case, closes the game
		LAST_STATE(-1),		// A special case, goes to the last state
		
		LOADING_STATE(0),
		MENU_STATE(1),
		LEVEL_STATE(2);

		// Initialises the values with an ID
		private final int ID;
		StateType(int id) {
			this.ID = id;
		}
		
		// Returns the ID of the value
		public int getID() {
			return ID;
		}
	};
	
	// The listener, used to take input
	private Display display;
	// The state manager, to change the state
	private StateManager stateManager;
	
	// Constructs a State
	public State(StateManager stateManager, Display display) {
		// Assigns the variables
		this.display = display;
		this.stateManager = stateManager;
	}
	
	// For waking the component
	public abstract void wake();
	// For sleeping the component
	public abstract void sleep();
	// For updating the component
	public abstract void update();
	// For drawing the component
	public abstract void draw(Graphics2D g);
	
	// Returns the display
	public Display getDisplay() {
		return this.display;
	}
	
	// Returns the state manager
	public StateManager getStateManager() {
		return this.stateManager;
	}
	
}
