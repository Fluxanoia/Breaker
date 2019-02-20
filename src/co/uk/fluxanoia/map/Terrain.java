package co.uk.fluxanoia.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.sun.glass.events.KeyEvent;

import co.uk.fluxanoia.control.Controller.InputType;
import co.uk.fluxanoia.control.PlayerController;
import co.uk.fluxanoia.entity.Protagonist;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.graphics.GridBackground;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.main.ResourceManager;
import co.uk.fluxanoia.map.Trigger.CameraMovement;
import co.uk.fluxanoia.map.Trigger.MusicTransition;
import co.uk.fluxanoia.util.Tween.TweenType;
import javafx.geometry.Point2D;

// The Terrain class, implements all game logic
public class Terrain extends Drawable {

	// The size of the grid cells
	public static final int GRID_SIZE = 32;

	// The list of tiles
	private ArrayList<Tile> tiles;
	// The list of triggers
	private ArrayList<Trigger> triggers;
	// The player spawn
	private PlayerSpawn playerSpawn;

	// The player
	private Protagonist player;

	// The display of the game
	private Display display;
	// The background for the game
	private GridBackground gridBackground;
	// The camera's mode and scroll speed
	private Point2D cameraScroll;
	private CameraMovement cameraMode;
	// The game's tileset and the empty texture
	private BufferedImage tileset, empty;

	// Constructs a GameGrid
	public Terrain(Display display, GridBackground gbg) {
		// Assign values
		this.gridBackground = gbg;
		this.display = display;
		// Initialise values
		this.tiles = new ArrayList<>();
		this.triggers = new ArrayList<>();
		this.playerSpawn = null;
		this.player = null;
		this.empty = display.getResourceManager()
				.getImage("res\\game\\empty.png");
		this.tileset = display.getResourceManager()
				.getImage("res\\game\\tileset.png");
	}

	// Loads a level onto the grid
	public void loadLevel(int i) {
		// Clear the current arrays
		tiles.clear();
		triggers.clear();
		playerSpawn = null;
		// Load in cells
		ArrayList<Cell> cells = new ArrayList<>();
		MapTool.readFile(cells, display, this, "res\\stages\\level" + i + ".level");
		for (Cell c : cells) {
			if (c instanceof Tile) tiles.add((Tile) c);
			if (c instanceof Trigger) triggers.add((Trigger) c);
			if (c instanceof PlayerSpawn) playerSpawn = (PlayerSpawn) c;
		}
		// Set up the camera
		display.getCamera().setPosition(0, 0, Main.DRAW_WIDTH, Main.DRAW_HEIGHT);
		this.cameraMode = CameraMovement.FOLLOW;
		// Set up the player
		player = new Protagonist(display, this,
				playerSpawn.getX() + Terrain.GRID_SIZE / 2,
				playerSpawn.getY() + Terrain.GRID_SIZE / 2);
		PlayerController c = new PlayerController(display.getListener());
		c.setKeyMap(InputType.UP, KeyEvent.VK_W);
		c.setKeyMap(InputType.DOWN, KeyEvent.VK_S);
		c.setKeyMap(InputType.LEFT, KeyEvent.VK_A);
		c.setKeyMap(InputType.RIGHT, KeyEvent.VK_D);
		player.setController(c);
		// Allow redraw
		this.pushClipBounds(Display.drawBounds());
	}

	// Updates the grid
	public void update() {
		player.update();
		this.pushClipBounds(player.dropClipBounds());
		double x, y;
		switch (cameraMode) {
		case FOLLOW:
			x = player.getHitbox().getCenterX() - display.getCamera().getWidth() / 2;
			y = player.getHitbox().getCenterY() - display.getCamera().getHeight() / 2;
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			if (display.getCamera().getTweenX().getDestination() != x) {
				display.getCamera().getTweenX().move(TweenType.EASE_OUT, x, 5, 0);
			}
			if (display.getCamera().getTweenY().getDestination() != y) {
				display.getCamera().getTweenY().move(TweenType.EASE_OUT, y, 5, 0);
			}
			break;
		case SCROLL:
			display.getCamera().getTweenX().set(display.getCamera().getX() + cameraScroll.getX());
			display.getCamera().getTweenY().set(display.getCamera().getY() + cameraScroll.getY());
			break;
		case STATIC:
			break;
		}
		// Update and activate triggers
		for (Trigger t : triggers) {
			t.update();
			t.activate();
		}
	}

	// Draws the grid
	public void draw(Graphics2D g) {
		Rectangle r = display.getCamera().getBounds();
		for (Tile t : new ArrayList<Tile>(tiles)) {
			t.draw(g, r, ResourceManager.getTile(tileset, empty,
					t.getTextureX(), t.getTextureY(), Terrain.GRID_SIZE));
		}
		if (player != null) player.draw(g);
	}
	
	// Pushes a background change
	public void pushBackgroundChange(Color c, double f_x, double f_y, double b_x, double b_y,
			double opacity, int duration) {
		this.gridBackground.setColour(c, duration);
		if (opacity != -1) this.gridBackground.getOpacity().push(TweenType.EASE_IN, opacity, duration, 0);
		this.gridBackground.setGridDirections(f_x, f_y, b_x, b_y);
	}
	
	// Pushes a camera change
	public void pushCameraMode(CameraMovement cameraMode, Point2D scroll) {
		this.cameraMode = cameraMode;
		this.cameraScroll = scroll;
	}
	
	// Pushes a music change
	public void pushMusic(MusicTransition musicTransition, int duration, String path) {
		
	}

	// Returns the tiles
	public ArrayList<Tile> getTiles() {
		return tiles;
	}
	
	// Returns the player
	public Rectangle getPlayerHitbox() {
		return player.getHitbox();
	}

}