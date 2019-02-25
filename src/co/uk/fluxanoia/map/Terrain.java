package co.uk.fluxanoia.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.sun.glass.events.KeyEvent;

import co.uk.fluxanoia.control.Controller;
import co.uk.fluxanoia.control.Controller.InputType;
import co.uk.fluxanoia.control.PlayerController;
import co.uk.fluxanoia.entity.Entity;
import co.uk.fluxanoia.entity.Entity.EntityIndex;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.graphics.Drawable;
import co.uk.fluxanoia.graphics.GridBackground;
import co.uk.fluxanoia.main.ErrorHandler;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.main.ResourceManager;
import co.uk.fluxanoia.map.Trigger.CameraMovement;
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
	private Entity player;
	// The entities barring the player
	private ArrayList<Entity> entities;

	// The camera
	private Camera camera;
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
	public Terrain(Display display, Camera camera, GridBackground gbg) {
		// Check for null values
		ErrorHandler.checkNull(display, "A Terrain was given a null display.");
		ErrorHandler.checkNull(camera, "A Terrain was given a null camera.");
		ErrorHandler.checkNull(gbg, "A Terrain was given a null grid background.");
		// Assign values
		this.gridBackground = gbg;
		this.camera = camera;
		this.display = display;
		// Initialise values
		this.tiles = new ArrayList<>();
		this.triggers = new ArrayList<>();
		this.entities = new ArrayList<>();
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
		camera.setPosition(0, 0, Main.DRAW_WIDTH, Main.DRAW_HEIGHT);
		this.cameraMode = CameraMovement.FOLLOW;
		// Set up the player
		player = EntityIndex.getEntity(playerSpawn.getEntityIndex(), display, this,
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
		// Update the player
		player.update();
		this.pushClipBounds(player.dropClipBounds());
		// Update entities
		Rectangle cb;
		for (Entity e : new ArrayList<Entity>(entities)) {
			e.update();
			cb = e.dropClipBounds();
			if (cb.intersects(Display.drawBounds())) this.pushClipBounds(cb);
		}
		// Update the camera
		double x, y;
		switch (cameraMode) {
		case FOLLOW:
			x = player.getHitbox().getCenterX() - camera.getWidth() / 2;
			y = player.getHitbox().getCenterY() - camera.getHeight() / 2;
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			if (camera.getTweenX().getDestination() != x) {
				camera.getTweenX().move(TweenType.EASE_OUT, x, 5, 0);
			}
			if (camera.getTweenY().getDestination() != y) {
				camera.getTweenY().move(TweenType.EASE_OUT, y, 5, 0);
			}
			break;
		case SCROLL:
			camera.getTweenX().set(camera.getX() + cameraScroll.getX());
			camera.getTweenY().set(camera.getY() + cameraScroll.getY());
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
		Rectangle r = camera.getBounds();
		for (Tile t : new ArrayList<Tile>(tiles)) {
			t.draw(g, r, ResourceManager.getTile(tileset, empty,
					t.getTextureX(), t.getTextureY(), Terrain.GRID_SIZE));
		}
		if (player != null) player.draw(g);
		for (Entity e : new ArrayList<Entity>(entities)) {
			e.draw(g);
		}
	}
	
	// Pushes a background change
	public void pushBackgroundChange(Color c, double f_x, double f_y, double b_x, double b_y,
			double opacity, int duration) {
		ErrorHandler.checkNull(c, "A Terrain was given a null colour.");
		this.gridBackground.setColour(c, duration);
		if (opacity != -1) this.gridBackground.getOpacity().push(TweenType.EASE_IN, opacity, duration, 0);
		this.gridBackground.setGridDirections(f_x, f_y, b_x, b_y);
	}
	
	// Pushes a camera change
	public void pushCameraMode(CameraMovement cameraMode, Point2D scroll) {
		ErrorHandler.checkNull(cameraMode, "A Terrain was given a null camera mode.");
		ErrorHandler.checkNull(scroll, "A Terrain was given a null scroll vector.");
		this.cameraMode = cameraMode;
		this.cameraScroll = scroll;
	}
	
	// Adds an entity
	public void addEntity(EntityIndex ei, Controller c, int x, int y) {
		ErrorHandler.checkNull(ei, "A Terrain was given a null entity index.");
		Entity e = EntityIndex.getEntity(ei, display, this, x, y);
		if (e == null) return;
		e.setController(c);
		entities.add(e);
	}

	// Returns the camera
	public Camera getCamera() {
		return camera;
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
