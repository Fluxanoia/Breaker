package co.uk.fluxanoia.state;

import java.awt.Graphics2D;

import co.uk.fluxanoia.graphics.Background;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.main.AudioManager;
import co.uk.fluxanoia.map.Trigger.MusicTransition;
import co.uk.fluxanoia.util.Tween.TweenType;

// The LoadingState, used as an opening for the game
public class LoadingState extends State {

	// Images for the background
	private Background fore;
	// Whether audio was loaded on the last update
	private boolean wasLoaded;

	// Constructs a LoadingState
	public LoadingState(StateManager stateManager, Display display) {
		// Constructs a State
		super(stateManager, display);
		// Initialises values
		wasLoaded = false;
		// Initialises the backgrounds
		fore = new Background(
				display.getResourceManager().getImage("res\\menu\\background_fore.png"), 
				MenuState.FORE_OUT_SCALE);
		// Add the backgrounds to the layer
		this.addComponent(Layer.FG, fore);
	}

	// Wakes the state
	public void wake() {
		// Set the background color
		// Move the foreground in
		fore.moveScale(TweenType.EASE_OUT, 1, 60, 0);
	}

	// Sleeps the state
	public void sleep() {
		// Reset the foreground
		fore.setScale(1.75);
	}

	// Updates the states
	public void update() {
		if (!this.getDisplay().getAudioManager().isLoaded()) {
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
			this.getDisplay().drawLoading(g);
		}
	}

}
