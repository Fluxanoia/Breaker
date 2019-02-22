package co.uk.fluxanoia.state;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import co.uk.fluxanoia.graphics.Background;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.main.AudioManager;
import co.uk.fluxanoia.map.Trigger.MusicTransition;
import co.uk.fluxanoia.util.Tween.TweenType;

// The LoadingState, used as an opening for the game
public class LoadingState extends State {

	// Images for the background
	private Background fore;
	private Background load;
	// The loading icon
	private BufferedImage load_ico;
	// The updates that have occurred so far
	private int ticks;
	// Whether audio was loaded on the last update
	private boolean wasLoaded;

	// Constructs a LoadingState
	public LoadingState(StateManager stateManager, Display display) {
		// Constructs a State
		super(stateManager, display);
		// Initialises values
		ticks = 0;
		wasLoaded = false;
		// Initialises the backgrounds
		fore = new Background(display, "res\\menu\\background_fore.png", 1.75, true);
		load = new Background(display, "res\\menu\\load.png", 1, true);
		// Load the load icon
		load_ico = display.getResourceManager().getImage("res\\icons\\load.png");
		// Add the backgrounds to the layer
		this.addComponent(Layer.FG, fore);
	}

	// Wakes the state
	public void wake() {
		// Set the background color
		// Move the foreground in
		fore.moveRelativeScale(TweenType.EASE_OUT, 1, 60, 0);
	}

	// Sleeps the state
	public void sleep() {
		// Reset the foreground
		fore.setRelativeScale(1.75);
		// Reset the updates
		ticks = 0;
	}

	// Updates the states
	public void update() {
		if (!this.getDisplay().getAudioManager().isLoaded()) {
			ticks++;
			this.pushClipBounds(Display.drawBounds());
			return;
		}
		if (!wasLoaded) {
			this.getDisplay().getAudioManager().changeMusic(MusicTransition.SET, 0, AudioManager.MENU_MUSIC);
			wasLoaded = true;
		}
		this.fore.update();
		if (fore.hasArrived()) {
			this.getStateManager().changeState(StateType.MENU_STATE);
		}
	}

	// Draws the state
	public void draw(Graphics2D g) {
		if (this.getDisplay().getAudioManager().isLoaded()) {
			this.drawComponents(g);
		} else {
			// Draw the loading screen
			load.draw(g);
			// Draw the load icon
			AffineTransform at = new AffineTransform();
			at.translate(load_ico.getWidth() / 2 + 5, load_ico.getHeight() / 2 + 5);
			at.rotate(Math.toRadians(ticks * 6));
			g.setTransform(at);
			g.drawImage(load_ico, null, -load_ico.getWidth() / 2, -load_ico.getHeight() / 2);
			g.setTransform(new AffineTransform());
		}
	}

}
