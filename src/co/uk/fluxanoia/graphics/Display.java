package co.uk.fluxanoia.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import co.uk.fluxanoia.main.AudioManager;
import co.uk.fluxanoia.main.ErrorHandler;
import co.uk.fluxanoia.main.GameMode;
import co.uk.fluxanoia.main.GameMode.Mode;
import co.uk.fluxanoia.main.Listener;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.main.ResourceManager;

// The display component, handles the panel (extended) and drawing
public class Display extends JPanel {
	
	// The wait between initialisation and mode selection
	private static final long MODE_WAIT = 2000;
	
	// The time when the display was initialised
	private long init_time;
	// Whether the initialisation stage is over or not
	private boolean initialised;
	// The load image
	private Background loading_bg;
	
	// The game window
	private JFrame window;
	// The input listener
	private Listener listener;
	// The resource manager
	private ResourceManager resourceManager;
	// The audio manager
	private AudioManager audioManager;
	// The state manager for the game
	private GameMode mode;
	// Whether the display is running or not
	private boolean running;
	// The drawing image
	private BufferedImage drawImage;
	
	// Constructs the Display object
	public Display() {
		// Initialises the game panel
		super();
		// Assigns the values
		// Initialises the running value
		this.running = true;
		// Initialises the ResourceManager
		this.resourceManager = new ResourceManager();
		// Initialises the AudioManager
		this.audioManager = new AudioManager(resourceManager);
		// Sets up the game panel
		Dimension window = new Dimension(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
		this.setPreferredSize(window);
		this.setVisible(true);
		// Initialises the window
		this.window = new JFrame(Main.GAME_TITLE);
		// Initialises the Listener
		this.listener = new Listener();
		// Sets up the game window
		this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.window.setResizable(false);
		this.window.add(this);
		this.window.pack();
		centreWindow(this.window);
		this.window.setVisible(true);
		// Initialises the mode
		this.mode = null;
		// Adds the listener to the panel and window
		this.window.addKeyListener(this.listener);
		this.addMouseListener(this.listener);
		this.addMouseMotionListener(this.listener);
		this.addMouseWheelListener(this.listener);
		// Sets up the background
		loading_bg = new Background(resourceManager.getImage("res\\menu\\load.png"));
		// Sets the initialisation time
		this.initialised = false;
		this.init_time = System.currentTimeMillis();
	}
	
	// Updates the Display
	public void update() {
		if (!initialised) {
			if (System.currentTimeMillis() - init_time >= MODE_WAIT) {
				this.mode = Mode.getGameMode(GameMode.DEFAULT_MODE, this);
			} else {
				this.mode = Mode.getGameMode(listener, this);
			}
			this.initialised = (mode != null);
			return;
		}
		// Updates the state manager and checks if it's closed
		this.mode.update();
		if (this.mode.isClosed()) {
			running = false;
			this.window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
			return;
		}
		// Updates the audio manager
		this.audioManager.update();
		// Updates the listener
		this.listener.update();
	}
	
	// Calls a draw
	public void draw() {
		this.repaint(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
	}
	
	// Draws the loading screen
	public void drawLoading(Graphics2D g) {
		// Draw the loading screen
		if (loading_bg != null) loading_bg.draw(g);
	}
	
	// Draws the components
	public void paint(Graphics g1) {
		// Call the super
		super.paint(g1);
		// Create the draw image
		if (drawImage == null) {
			drawImage = new BufferedImage(Main.DRAW_WIDTH, Main.DRAW_HEIGHT,
					BufferedImage.TYPE_INT_ARGB);
		}
		// Create a G2D object
		Graphics2D g = (Graphics2D) drawImage.getGraphics();
		// If there's a mode
		if (initialised) {
			// Set the clip bounds
			g.setClip(mode.dropClipBounds());
			// Draws the state manager
			this.mode.draw(g);
		} else {
			// Draw the loading screen
			drawLoading(g);
		}
		// Dispose of the graphics instance
		g.dispose();
		// Set the antialias and interpolation options
		((Graphics2D) g1).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		((Graphics2D) g1).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		// Print to the panel
		((Graphics2D) g1).drawImage(drawImage, 0, 0,
				Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT, Color.BLACK, null);
	}
	
	// Returns the Listener
	public Listener getListener() {
		return this.listener;
	}

	// Returns the AudioManager
	public AudioManager getAudioManager() { return audioManager; }

	// Returns the ResourceManager
	public ResourceManager getResourceManager() {
		return resourceManager;
	}
	
	// Returns whether the display is running or not
	public boolean isRunning() {
		return running;
	}
	
	// Centres the window
	public static void centreWindow(JFrame frame) {
		ErrorHandler.checkNull(frame, "The Display was given a null frame.");
		// Get the toolkit and then the screen dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// Get the required x, y positions and set the location
		int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
		int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
		frame.setLocation(x, y);
	}
	
	// Returns the window bounds
	public static Rectangle drawBounds() {
		return new Rectangle(0, 0, Main.DRAW_WIDTH, Main.DRAW_HEIGHT);
	}
	
	// Gets the area of a Rectangle
	public static double area(Rectangle r) {
		return r.getWidth() * r.getHeight();
	}
	
}
