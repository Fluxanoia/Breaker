package co.uk.fluxanoia.control;

import co.uk.fluxanoia.main.Listener;

// The abstract controller class, allows entities to be AI or player controlled
public abstract class Controller {

	// The enum containing all input types
	public enum InputType {
		UP, DOWN, LEFT, RIGHT, ATTACK;
	}
	
	// The listener for the game
	private Listener listener;
	
	// Constructs a controller
	public Controller(Listener listener) {
		this.listener = listener;
	}
	
	// Updates the controller
	public abstract void update();
	// Gets the data of the controller
	public abstract boolean isPressed(InputType it);
	public abstract boolean isHeld(InputType it);
	public abstract boolean isReleased(InputType it);
	
	// Returns the listener
	protected Listener getListener() {
		return listener;
	}
	
}
