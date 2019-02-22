package co.uk.fluxanoia.control;

import co.uk.fluxanoia.main.Listener;

// The abstract controller class, allows entities to be AI or player controlled
public abstract class Controller {

	// The enum containing all types of controller
	public enum ControllerType {
		PLAYER("plr");
		
		private String id;
		ControllerType(String id) {
			this.id = id;
		}
		public String getID() { return id; }
		
		// Returns the EntityIndex associated with the id
		public static ControllerType getType(String id) {
			ControllerType[] cts = ControllerType.values();
			for (int i = 0; i < cts.length; i++) {
				if (id.equals(cts[i].getID())) return cts[i];
			}
			return null;
		}
		// Returns the entity associated with the index
		public static Controller getController(String id, Listener listener) {
			return getController(getType(id), listener);
		}
		public static Controller getController(ControllerType ct, Listener listener) {
			if (ct == null) return null;
			switch (ct) {
			case PLAYER:
				return new PlayerController(listener);
			}
			return null;
		}
	}
	
	// The enum containing all input types
	public enum InputType {
		UP, DOWN, LEFT, RIGHT, ATTACK;
	}
	
	// The listener for the game
	private Listener listener;
	// The type of controller
	private ControllerType type;
	
	// Constructs a controller
	public Controller(ControllerType type, Listener listener) {
		this.type = type;
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
	
	// Returns the type
	public ControllerType getType() {
		return type;
	}
	
}
