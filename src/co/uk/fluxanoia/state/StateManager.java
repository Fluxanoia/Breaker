package co.uk.fluxanoia.state;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.graphics.GridBackground;
import co.uk.fluxanoia.main.GameMode;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.map.Camera;
import co.uk.fluxanoia.state.State.StateType;

// The StateManager, manages the state of the game
public class StateManager extends GameMode {

	// The list containing all the states
	private HashMap<Integer, State> states;
	// The current state
	private StateType currentState;
	// The last state
	private StateType lastState;
	// Whether the StateManager is closed or not
	private boolean closed = false;
	// The passed ID
	private int pass_ID;
	// The grid background
	private GridBackground gridBackground;
	// The camera
	private Camera camera;
	
	// Constructs the StateManager object
	public StateManager(Display display) {
		super(display);
		// Initialise values
		pass_ID = -1;
		gridBackground = new GridBackground(0, 0, 0, 0);
		camera = new Camera(0, 0, Main.DRAW_WIDTH, Main.DRAW_HEIGHT);
		// Initialise the states
		states = new HashMap<>();
		states.put(StateType.LOADING_STATE.getID(), new LoadingState(this, display));
		states.put(StateType.MENU_STATE.getID(), new MenuState(this, display));
		states.put(StateType.LEVEL_STATE.getID(), new LevelState(this, display));
		// Sets the initial state value
		lastState = StateType.CLOSE;
		currentState = StateType.LOADING_STATE;
		// Wake the first state
		this.wakeState();
	}
	
	// Wakes the current state
	private void wakeState() {
		// If the StateManager is closed, return
		if (closed) return;
		// Wake the state
		states.get(currentState.getID()).wake();
	}
	
	// Sleeps the current state
	private void sleepState() {
		// If the StateManager is closed, return
		if (closed) return;
		// Sleep the state
		states.get(currentState.getID()).sleep();
	}
	
	// Updates the current state
	private void updateState() {
		// If the StateManager is closed, return
		if (closed) return;
		// Update the state
		states.get(currentState.getID()).update();
		states.get(currentState.getID()).updateLayer();
	}
	
	// Changes the state of the StateManager
	public void changeState(StateType st) {
		// If the StateManager is closed, return
		if (closed) return;
		// If expected to close, close
		if (st == StateType.CLOSE) { 
			closed = true;
			return;
		}
		// If expected to go to the last state, return
		if (st == StateType.LAST_STATE) st = lastState;
		// Sleep the old state
		sleepState();
		// Switch the states
		lastState = currentState;
		currentState = st;
		// Wake the state
		wakeState();
	}
	
	// Updates the component
	public void update() {
		// If the StateManager is closed, return
		if (closed) return;
		// Update the background
		this.camera.update();
		gridBackground.update();
		// Update the state
		updateState();
	}
	
	// Draws the active layer
	public void draw(Graphics2D g) {
		if (closed) return;
		if (camera == null) return;
		gridBackground.draw(g);
		states.get(currentState.getID()).draw(g);
	}

	// Gets the clip bounds of the active layer
	public Rectangle dropClipBounds() {
		if (camera.dropMoved()) return Display.drawBounds();
		return Drawable.encapsulate(new Rectangle[] {
				gridBackground.dropClipBounds(),
				states.get(currentState.getID()).dropClipBounds()
				});
	}
	
	// Sets the passed ID
	public void setPassID(int pid) {
		pass_ID = pid;
	}

	// Returns the camera
	public Camera getCamera() {
		return camera;
	}
	
	// Returns the grid background
	public GridBackground getGridBG() {
		return gridBackground;
	}

	// Returns the passed ID
	public int getPassID() {
		return pass_ID;
	}
	
	// Returns whether the StateManager is closed or not
	public boolean isClosed() {
		return closed;
	}
	
}
