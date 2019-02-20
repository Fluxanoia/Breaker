package co.uk.fluxanoia.main;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.main.GameMode.Mode;

// Contains the entry point and is the main controller for the game
public class Main {
	
	// The title of the game window
	public static final String GAME_TITLE      = "Breaker";
	// The width and height of the draw image and the game window
	public static final int DRAW_WIDTH         = 640;
	public static final int DRAW_HEIGHT        = 360;
	public static final int WINDOW_WIDTH       = 1280;
	public static final int WINDOW_HEIGHT      = 720;
	// The updates per second that the game runs at
	private static final int UPDATES_PER_SECOND = 60;
	// Whether the game loads into the map tool or the game
	public static final Mode MODE = Mode.GAME;
	
	// The display controller for the game
	private Display display;
	// Whether the game should be running or not
	private boolean running = false;
	
	// The entry point for the game
	public static void main(String[] args) {
		// Creates the Main object with the command line variables
		new Main();
	}
	
	// Constructs the Main object
	private Main() {
		// Initialise variables
		init();
		// Run the main loop
		run();
	}
	
	// Initialises variable
	private void init() {
		// Creates a Display object
		this.display = new Display();
		// Tells the game to run
		running = true;
	}
	
	// The main game loop
	private void run() {
		// The time in nanoseconds between updates
		int delta_time = (int) (Math.pow(10, 9) / UPDATES_PER_SECOND);
		// The time when the last update occurred in nanoseconds
		long last_update = System.nanoTime();
		// The main game loop
		while (running) {
			// While the time since the last update has exceeded the delta, update
			while (System.nanoTime() - last_update >= delta_time) {
				last_update += delta_time;
				this.update();
			}
			// Draw when possible
			this.display.draw();
		}
	}
	
	// Update the game components
	private void update() {
		this.display.update();
		this.running = display.isRunning();
	}
	
}
