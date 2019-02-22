package co.uk.fluxanoia.state;

import java.awt.Graphics2D;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.map.Terrain;

// The LevelState, where the user will play the levels
public class LevelState extends State {

    // The game terrain
    private Terrain terr;

    // Constructs the LevelState
    public LevelState(StateManager stateManager, Display display) {
        // Constructs the State
        super(stateManager, display);
        // Initialise values
        terr = new Terrain(display, this.getStateManager().getCamera(), this.getStateManager().getGridBG());
        // Add components to the layer
        this.addComponent(Layer.FG, terr);
    }

    // Wakes the state
    public void wake() {
    	this.getDisplay().getAudioManager().stopMusic();
        terr.loadLevel(getStateManager().getPassID());
    }

    // Sleeps the state
    public void sleep() {
    }

    // Updates the state
    public void update() {
        terr.update();
    }

    // Draws the state
    public void draw(Graphics2D g) {
        this.drawComponents(g);
    }

}
