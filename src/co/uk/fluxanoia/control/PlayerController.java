package co.uk.fluxanoia.control;

import java.util.HashMap;

import co.uk.fluxanoia.main.Listener;

// The PlayerController class, takes user input and converts it to the game's interface
public class PlayerController extends Controller {
	
	// The key maps of the controller
	private HashMap<InputType, Integer> keymap;
	private HashMap<InputType, Integer> mousemap;
	
	// The inputs of the controller
	private HashMap<InputType, Boolean> pressedInput;
	private HashMap<InputType, Boolean> heldInput;
	private HashMap<InputType, Boolean> releasedInput;
	
	// Constructs a player controller
	public PlayerController(Listener listener) {
		// Construct the controller
		super(listener);
		// Initialise values
		this.keymap = new HashMap<>();
		this.mousemap = new HashMap<>();
		this.pressedInput = new HashMap<>();
		this.heldInput = new HashMap<>();
		this.releasedInput = new HashMap<>();
		// Fill the hash maps
		InputType[] ivs = InputType.values();
		for (int i = 0; i < ivs.length; i++) {
			pressedInput.put(ivs[i], false);
			heldInput.put(ivs[i], false);
			releasedInput.put(ivs[i], false);
		}
	}

	// Updates the player controller
	public void update() {
		boolean pressed, held, released;
		InputType[] ivs = InputType.values();
		for (int i = 0; i < ivs.length; i++) {
			// For each input type, check the key/mouse value associated with it
			pressed = false;
			held = false;
			released = false;
			pressed |= this.getListener().isKeyPressed(keymap.get(ivs[i]));
			pressed |= this.getListener().isMousePressed(mousemap.get(ivs[i]));
			held |= this.getListener().isKeyHeld(keymap.get(ivs[i]));
			held |= this.getListener().isMouseHeld(mousemap.get(ivs[i]));
			released |= this.getListener().isKeyReleased(keymap.get(ivs[i]));
			released |= this.getListener().isMouseReleased(mousemap.get(ivs[i]));
			pressedInput.replace(ivs[i], pressed);
			heldInput.replace(ivs[i], held);
			releasedInput.replace(ivs[i], released);
		}
	}
	
	// Sets maps
	public void setKeyMap(InputType it, int k) {
		keymap.put(it, k);
	}
	public void setMouseMap(InputType it, int k) {
		mousemap.put(it, k);
	}
	
	// Returns data on control
	public boolean isPressed(InputType it) {
		return pressedInput.get(it);
	}
	public boolean isHeld(InputType it) {
		return heldInput.get(it);
	}
	public boolean isReleased(InputType it) {
		return releasedInput.get(it);
	}
	
}
