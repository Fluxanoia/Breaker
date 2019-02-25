package co.uk.fluxanoia.state;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Random;

import co.uk.fluxanoia.graphics.Background;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.FloatingImage;
import co.uk.fluxanoia.graphics.GridBackground;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.main.ResourceManager;
import co.uk.fluxanoia.util.ButtonManager;
import co.uk.fluxanoia.util.Tween;
import co.uk.fluxanoia.util.Tween.TweenType;

//The MenuState, used as an opening for the game
public class MenuState extends State {

	// The scale of the pushed out foreground
	public static final double FORE_OUT_SCALE = 1.75;

	// The states of the menu
	private enum Phase {
		MAIN   (0, ResourceManager.COLOUR_PINK),
		LEVEL  (1, ResourceManager.COLOUR_BLUE),
		OPTIONS(2, ResourceManager.COLOUR_GREEN),
		HELP   (3, ResourceManager.COLOUR_YELLOW);

		private int ID;
		private Color c;
		public int getID() { return ID; }
		public Color getColour() { return c; }
		Phase(int ID, Color c) {
			this.ID = ID;
			this.c = c;
		}
	};
	// The state of the menu
	private Phase phase;

	// The list of button names
	private final String[] BUTTON_NAMES = new String[] { "Play", "Options", "Help", "Quit" };
	// The amount of level buttons displayed on the width and height
	private final int NO_LEVEL_BUTTONS_W = 10;
	private final int NO_LEVEL_BUTTONS_H = 10;

	// The button manager for the menu
	private ButtonManager[] buttonManagers;
	// The background of the menu
	private Background fore;
	// The title
	private FloatingImage title;

	// The state that will be changed to when the transition ends
	private StateType pending;
	private int pending_wait;
	
	// Constructs a MenuState
	public MenuState(StateManager stateManager, Display display) {
		// Constructs a State
		super(stateManager, display);
		// Initialises values
		this.phase = null;
		this.pending = null;
		this.pending_wait = 0;
		// Creates the button managers
		Phase[] p_values = Phase.values();
		this.buttonManagers = new ButtonManager[p_values.length];
		for (int i = 0; i < p_values.length; i++) {
			this.buttonManagers[p_values[i].getID()] = new ButtonManager(display);
			// Add the button manager to the component list
			this.addComponent(Layer.MD, buttonManagers[i]);
		}
		// Adds the buttons
		this.addButtons();
		// Initialises values
		fore = new Background(
				display.getResourceManager().getImage("res\\menu\\background_fore.png"));
		title = new FloatingImage(
				display.getResourceManager().getImage("res\\sprites\\title.png"),
				-Main.DRAW_WIDTH / 2, 100, 1, 1, 0);
		// Add the backgrounds to the layer
		this.addComponent(Layer.MD, title);
		this.addComponent(Layer.FG, fore);
	}

	// Wakes the state
	public void wake() {
		exitAllPhases();
		phase = null;
		GridBackground gbg = this.getStateManager().getGridBG();
		gbg.setColour(Color.BLACK, 1);
		gbg.getOpacity().set(0);

		switchPhase(Phase.MAIN, 0, 40);
		fore.setScale(1);
		gbg.getOpacity().move(TweenType.EASE_OUT, 1, 60, 0);
	}

	// Sleeps the state
	public void sleep() {
		pending = null;
	}

	// Updates the state
	public void update() {
		// If pending a change of state
		if (pending_wait > 0) {
			// Decrement the wait
			pending_wait--;
			if (pending_wait == 0) {
				// If the wait is over, change state
				this.getStateManager().changeState(pending);
			}
		}
		// Update the foreground
		fore.update();
		// Update the title
		title.update();
		// Update the button manager
		for (int i = 0; i < buttonManagers.length; i++) buttonManagers[i].update();
		// Take and iterate through the button queue of the button manager
		int[] queue = buttonManagers[phase.getID()].dropQueue();
		for (int i = 0; i < queue.length; i++) button_activation(queue[i]);
	}

	// Draws the state
	public void draw(Graphics2D g) {
		// Draw the components of this layer
		this.drawComponents(g);
	}

	// Reacts to a button press
	private void button_activation(int i) {
		// If waiting to close, return
		if (pending_wait > 0) return;
		// React to button presses
		switch (phase) {
			case MAIN:
				pressOnMain(i);
				break;
			case LEVEL:
				pressOnLevel(i);
				break;
			case OPTIONS:
				pressOnOptions(i);
				break;
			case HELP:
				pressOnHelp(i);
				break;
			default: return;
		}
		// If the pending state has changed, move the
		if (pending != null) {
			// Set the timer
			this.pending_wait = 60;
			exitPhase(phase, 60);
		}
	}

	// Reacts to a MAIN button press
	private void pressOnMain(int i) {
		switch (i) {
			case 0:
				switchPhase(Phase.LEVEL, 30, 40);
				break;
			case 1:
				switchPhase(Phase.OPTIONS, 30, 40);
				break;
			case 2:
				switchPhase(Phase.HELP, 30, 40);
				break;
			case 3:
				this.getStateManager().changeState(StateType.CLOSE);
				break;
		}
	}

	// Reacts to a LEVEL button press
	private void pressOnLevel(int i) {
		switch (i) {
			case 0:
				switchPhase(Phase.MAIN, 30, 40);
				break;
			default:
				getStateManager().setPassID(i);
				exitPhase(phase, 60);
				this.getStateManager().getGridBG().getOpacity()
					.move(TweenType.EASE_OUT, 0, 60, 0);
				pending = StateType.LEVEL_STATE;
				pending_wait = 60;
				break;
		}
	}

	// Reacts to a OPTIONS button press
	private void pressOnOptions(int i) {
		switch (i) {
			default:
				switchPhase(Phase.MAIN, 30, 40);
				break;
		}
	}

	// Reacts to a HELP button press
	private void pressOnHelp(int i) {
		switch (i) {
			default:
				switchPhase(Phase.MAIN, 30, 40);
				break;
		}
	}

	// Moves the phase in
	private void enterPhase(Phase p, int duration) {
		if (p == null) return;
		// The tweens to be altered
		Tween[] tweens;
		int[] holds;
		// The starting value to be moved from
		int start;
		// The grid background
		GridBackground gbg = this.getStateManager().getGridBG();
		// Clear the tweens of the manager
		tweens = buttonManagers[p.getID()].getXTweens();
		for (int i = 0; i < tweens.length; i++) {
			tweens[i].set(0);
		}
		tweens = buttonManagers[p.getID()].getYTweens();
		for (int i = 0; i < tweens.length; i++) {
			tweens[i].set(0);
		}
		// Go to the case of the current phase
		switch (p) {
			case MAIN:
				fore.pushScale(TweenType.EASE_OUT, 1, 20, 0);
				title.getTweenRotation().set(-30 + (new Random().nextDouble()) * 60);
				title.getTweenRotation().move(TweenType.ELASTIC,
						0, 
						130, 0);
				title.getTweenY().set((new Random().nextDouble()) * 200);
				title.getTweenY().move(TweenType.ELASTIC,
						100, 
						120, 0);
				title.getTweenX().move(TweenType.ELASTIC,
						Main.DRAW_WIDTH / 2, 
						145, 0);
				tweens = buttonManagers[p.getID()].getXTweens();
				start = -3 * (Main.DRAW_WIDTH / 4);
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = (holds.length - i + 1) * 4;
				gbg.setGridDirections(0.2, 0.3, -0.25, -0.15);
				break;
			case LEVEL:
				tweens = buttonManagers[p.getID()].getYTweens();
				start = Main.DRAW_HEIGHT;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = (int) Math.ceil(i / NO_LEVEL_BUTTONS_W);
				gbg.setGridDirections(-0.4, -0.6, -0.5, -0.3);
				break;
			case HELP:
				tweens = buttonManagers[p.getID()].getXTweens();
				start = Main.DRAW_HEIGHT;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = 0;
				gbg.setGridDirections(0.2, 0.3, 0.5, 0.1);
				break;
			case OPTIONS:
				tweens = buttonManagers[p.getID()].getXTweens();
				start = Main.DRAW_HEIGHT;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = 0;
				gbg.setGridDirections(0.3, -0.7, 0.5, 0.6);
				break;

				default:
					return;
		}
		// Sets the grid background colour
		gbg.setColour(p.getColour(), duration);
		// Iterates through the tweens
		for (int i = 0; i < tweens.length; i++) {
			// Sets them to the starting position
			tweens[i].set(start);
			// Moves it to its normal position
			tweens[i].move(TweenType.EASE_OUT, 0, duration, holds[i]);
		}
	}

	// Moves the phase out
	private void exitPhase(Phase p, int duration) {
		if (p == null) return;
		// The tweens to be altered
		Tween[] tweens;
		int[] holds;
		// The value to be moved to
		int end;
		// Go to the case of the current phase
		switch (p) {
			case MAIN:
				fore.pushScale(TweenType.EASE_OUT, FORE_OUT_SCALE, 35, 0);
				title.getTweenX().move(TweenType.EASE_OUT,
						-Main.DRAW_WIDTH / 2, 
						15, 0);
				tweens = buttonManagers[p.getID()].getYTweens();
				end = -Main.DRAW_HEIGHT;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = i;
				break;
			case LEVEL:
				tweens = buttonManagers[p.getID()].getYTweens();
				end = -Main.DRAW_HEIGHT;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = (int) Math.floor(i / NO_LEVEL_BUTTONS_W);
				break;
			case HELP:
				tweens = buttonManagers[p.getID()].getXTweens();
				end = -Main.DRAW_WIDTH;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = 0;
				break;
			case OPTIONS:
				tweens = buttonManagers[p.getID()].getXTweens();
				end = -Main.DRAW_WIDTH;
				holds = new int[tweens.length];
				for (int i = 0; i < holds.length; i++) holds[i] = 0;
				break;

			default:
				return;
		}

		// Iterates through the tweens
		for (int i = 0; i < tweens.length; i++) {
			// Moves it to the next position
			tweens[i].move(TweenType.EASE_OUT, end, duration, holds[i]);
		}
	}

	// Switches the phase
	private void switchPhase(Phase p, int exit, int enter) {
		exitPhase(phase, exit);
		phase = p;
		enterPhase(phase, enter);
	}

	// Exits all phases
	private void exitAllPhases() {
		Phase[] p_values = Phase.values();
		for (int i = 0; i < p_values.length; i++) {
			exitPhase(p_values[i], 0);
		}
	}

	// Adds the necessary buttons to the button manager
	private void addButtons() {
		// Adds the necessary buttons
		Dimension dim;
		// MAIN
		dim = new Dimension(50, 20);
		for (int i = 0; i < BUTTON_NAMES.length; i++) {
			buttonManagers[Phase.MAIN.getID()].addButton(i,
					dim,
					BUTTON_NAMES[i],
					ButtonManager.CENTRE_VALUE,
					200 + i * 30);
		}
		// LEVEL
		dim = new Dimension(40, 20);
		int x_buffer = (Main.DRAW_WIDTH - NO_LEVEL_BUTTONS_W * (int) dim.getWidth()) / (NO_LEVEL_BUTTONS_W + 1);
		int y_buffer = (Main.DRAW_HEIGHT - NO_LEVEL_BUTTONS_H * (int) dim.getHeight()) / (NO_LEVEL_BUTTONS_H + 1);
		for (int y = 0; y < NO_LEVEL_BUTTONS_H; y++) {
			for (int x = 0; x < NO_LEVEL_BUTTONS_W; x++) {
				if (x == y && y == 0) {
					buttonManagers[Phase.LEVEL.getID()].addButton(0,
							dim,
							"Back",
							x_buffer,
							y_buffer);
					continue;
				}
				buttonManagers[Phase.LEVEL.getID()].addButton(y * NO_LEVEL_BUTTONS_W + x,
						dim,
						(y * NO_LEVEL_BUTTONS_W + x) + "",
						x_buffer + x * ((int) dim.getWidth() + x_buffer),
						y_buffer + y * ((int) dim.getHeight() + y_buffer));
			}
		}
		// OPTIONS
		dim = new Dimension(500, 200);
		buttonManagers[Phase.OPTIONS.getID()].addButton(0,
				dim,
				"Back",
				ButtonManager.CENTRE_VALUE,
				ButtonManager.CENTRE_VALUE);
		// HELP
		dim = new Dimension(500, 200);
		buttonManagers[Phase.HELP.getID()].addButton(0,
				dim,
				"Back",
				ButtonManager.CENTRE_VALUE,
				ButtonManager.CENTRE_VALUE);
	}
	
}
