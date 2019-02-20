package co.uk.fluxanoia.map;

import java.awt.Color;
import java.awt.Rectangle;

import co.uk.fluxanoia.graphics.Display;
import javafx.geometry.Point2D;

// The Trigger class, represents an action in the game world
public class Trigger extends Cell{

	// Structure of triggers in files
	// ...TYPE_EFFECT_DATA
	
	// Structure of data in files
	// - Music
		// TRANSITION_DURATION_PATH
	// - Background
		// R_G_B_FX_FY_BX_BY_OPACITY_DURATION
	// - Entity
		// TO BE ADDED
	// - Camera
		// MOVEMENT_VX_VY
	
	// The types of triggers
	public enum TriggerType {
		ONSCREEN("ons", "Onscreen"),       // Triggered when it enters the camera's view
		OFFSCREEN("off", "Offscreen"),     // Triggered when it enters and then leaves the camera's view
		PLAYER_X("pxp", "Player X"),       // Triggered when the player's x value is the same as the trigger
		PLAYER_Y("pyp", "Player Y"),       // Triggered when the player's y value is the same as the trigger
		PLAYER_XY("pxy", "Player X & Y");  // Triggered when the player is on the trigger
		private String id, name;
		TriggerType(String id, String name) { 
			this.id = id; 
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static TriggerType parse(String s) {
			for (TriggerType tt : TriggerType.values()) {
				if (tt.getID().equals(s)) return tt;
			}
			return null;
		}
	}
	
	// The effects of triggers
	public enum TriggerEffect {
		MUSIC("mus", "Music"), 
		BACKGROUND("bkg", "Background"), 
		ENTITY("ent", "Entity"), 
		CAMERA("cam", "Camera");
		private String id, name;
		TriggerEffect(String id, String name) { 
			this.id = id;
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static TriggerEffect parse(String s) {
			for (TriggerEffect te : TriggerEffect.values()) {
				if (te.getID().equals(s)) return te;
			}
			return null;
		}
	}
	
	// The type of music change
	public enum MusicTransition {
		SET("set", "Set"), 
		FADE("fad", "Fade"), 
		STOP("stp", "Stop");
		private String id, name;
		MusicTransition(String id, String name) { 
			this.id = id;
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static MusicTransition parse(String s) {
			for (MusicTransition mt : MusicTransition.values()) {
				if (mt.getID().equals(s)) return mt;
			}
			return null;
		}
	}

	// The types of camera movement
	public enum CameraMovement {
		SCROLL("scr", "Scrolling"), 
		STATIC("stc", "Static"), 
		FOLLOW("fol", "Following");
		private String id, name;
		CameraMovement(String id, String name) { 
			this.id = id; 
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static CameraMovement parse(String s) {
			for (CameraMovement cm : CameraMovement.values()) {
				if (cm.getID().equals(s)) return cm;
			}
			return null;
		}
	};
	
	// The type of the tile
	private TriggerType type;
	// The effect of the trigger
	private TriggerEffect effect;
	
	// The following may or may not be used depending on the trigger
	// The type of music transition
	private MusicTransition musicTransition;
	// The duration of the transition
	private int transitionDuration;
	// The path of the music
	private String musicPath;
	// The type of camera movement
	private CameraMovement cameraMovement;
	// The vector for the camera scroll
	private Point2D cameraScroll;
	// The colours the background changes to
	private Color gridColour;
	// The vectors for the background grid speeds
	private Point2D gridBackVector, gridForeVector;
	// The opacity of the grid background
	private double gridOpacity;
	// The duration of the transitions of grid backgrounds
	private int gridDuration;
	
	// The game display
	private Display display;
	// The game terrain
	private Terrain terrain;
	// Whether the trigger has been activated or not
	private boolean activated;
	// Whether the trigger is ready to be activated
	private boolean ready;
	// Whether the trigger has been onscreen at some point
	private boolean onscreen;
	
	// Constructs a Tile
	public Trigger(Display display, Terrain terrain, int x, int y, TriggerType type, TriggerEffect effect) {
		// Construct the cell
		super(x, y, -1, -1, CellType.TRIGGER);
		// Assign values
		this.display = display;
		this.terrain = terrain;
		this.type = type;
		this.effect = effect;
		// Initialise values
		this.onscreen = false;
		this.ready = false;
		this.activated = false;
		this.musicTransition = null;
		this.transitionDuration = -1;
		this.musicPath = null;
		this.cameraMovement = null;
		this.cameraScroll = null;
		this.gridColour = null;
		this.gridBackVector = null;
		this.gridForeVector = null;
		this.gridDuration = 1;
		this.gridOpacity = -1;
	}
	
	// Updates the trigger
	public void update() {
		// Check the type
		Rectangle player, bounds = this.getBounds();
		if (!onscreen && bounds.intersects(display.getCamera().getBounds())) {
			onscreen = true;
		}
		switch (type) {
		case OFFSCREEN:
			if (!bounds.intersects(display.getCamera().getBounds()) && onscreen) {
				ready = true;
			}
			break;
		case ONSCREEN:
			ready = onscreen;
			break;
		case PLAYER_X:
			player = terrain.getPlayerHitbox();
			if (bounds.getX() < player.getCenterX() && bounds.getMaxX() > player.getCenterX()) {
				ready = true;
			}
			break;
		case PLAYER_XY:
			player = terrain.getPlayerHitbox();
			if (bounds.getY() < player.getCenterY() && bounds.getMaxY() > player.getCenterY()) {
				if (bounds.getX() < player.getCenterX() && bounds.getMaxX() > player.getCenterX()) {
					ready = true;
				}
			}
			break;
		case PLAYER_Y:
			player = terrain.getPlayerHitbox();
			if (bounds.getY() < player.getCenterY() && bounds.getMaxY() > player.getCenterY()) {
				ready = true;
			}
			break;
		}
	}
	
	// Returns whether the trigger has been activated or not
	public boolean activate() {
		if (ready && !activated) {
			activated = true;
			switch(effect) {
			case BACKGROUND:
				terrain.pushBackgroundChange(gridColour,
						gridForeVector.getX(),
						gridForeVector.getY(),
						gridBackVector.getX(),
						gridBackVector.getY(),
						gridOpacity,
						gridDuration);
				break;
			case CAMERA:
				terrain.pushCameraMode(cameraMovement, cameraScroll);
				break;
			case ENTITY:
				// TO BE ADDED
				break;
			case MUSIC:
				terrain.pushMusic(musicTransition, transitionDuration, musicPath);
				break;
			}
			return true;
		}
		return false;
	}
	
	// Takes a string and translates it into specific trigger information
	public void parseData(String s) {
		String[] split = s.split("_");
		if (split.length > 0) {
			TriggerType tt = TriggerType.parse(split[0]);
			if (tt != null) type = tt;
		}
		if (split.length > 1) {
			TriggerEffect te = TriggerEffect.parse(split[1]);
			if (te != null) effect = te;
		}
		if (effect == null) return;
		switch (effect) {
		case BACKGROUND:
			if (split.length > 8) {
				this.gridColour = new Color(
						Integer.valueOf(split[2]),
						Integer.valueOf(split[3]),
						Integer.valueOf(split[4]));
				this.gridForeVector = new Point2D(
						Double.valueOf(split[5]),
						Double.valueOf(split[6]));
				this.gridBackVector = new Point2D(
						Double.valueOf(split[7]),
						Double.valueOf(split[8]));
			}
			if (split.length > 10) {
				this.gridOpacity = Double.valueOf(split[9]);
				this.gridDuration = Integer.valueOf(split[10]);
			} else {
				this.gridOpacity = 1;
				this.gridDuration = 10;
			}
			break;
		case CAMERA:
			if (split.length > 2) {
				this.cameraMovement = CameraMovement.parse(split[2]);
			}
			if (split.length > 4) {
				this.cameraScroll = new Point2D(
						Double.valueOf(split[3]),
						Double.valueOf(split[4]));
			}
			break;
		case ENTITY:
			// TO BE ADDED
			break;
		case MUSIC:
			if (split.length > 4) {
				musicTransition = MusicTransition.parse(split[2]);
				transitionDuration = Integer.valueOf(split[3]);
				musicPath = split[4];
			}
			break;
		}
	}

	// Returns a summary of this trigger
	public String[] getInfo() {
		if (type == null || effect == null) return new String[] {"NULL"};
		String info = type.getName() + " trigger_" + effect.getName() + " effect_";
		switch (effect) {
		case BACKGROUND:
			if (gridColour != null) {
				info += "Colour (" + gridColour.getRed() + ", "
					+ gridColour.getGreen() + ", "
					+ gridColour.getBlue() + ")_";
			}
			if (gridForeVector != null) {
				info += "Fore vector (" + gridForeVector.getX() + ", "
						+ gridForeVector.getY() + ")_";
			}
			if (gridBackVector != null) {
				info += "Back vector (" + gridBackVector.getX() + ", "
						+ gridBackVector.getY() + ")_";
			}
			if (gridOpacity != -1) {
				info += "Opacity " + gridOpacity + "_";
			}
			info += "Duration " + gridDuration + "_";
			break;
		case CAMERA:
			if (cameraMovement != null) {
				info += "Movement type: " + cameraMovement.getName() + "_";
				if (cameraScroll != null) {
					info += "Scrolling at (" + cameraScroll.getX() + ", "
							+ cameraScroll.getY() + ")_";
				}
			}
			break;
		case ENTITY:
			break;
		case MUSIC:
			if (musicTransition != null) {
				info += "Transition of " + musicTransition.getName() + "_Lasting "
						+ transitionDuration + "_To " + musicPath;
			}
			break;
		}
		return info.split("_");
	}
	
	// Returns the data about this trigger in a string
	public String getData() {
		String data = type.getID() + "_" + effect.getID();
		switch (effect) {
		case BACKGROUND:
			if (gridColour == null) break;
			data += "_" + gridColour.getRed();
			data += "_" + gridColour.getGreen();
			data += "_" + gridColour.getBlue();
			if (gridForeVector == null) break;
			data += "_" + gridForeVector.getX();
			data += "_" + gridForeVector.getY();
			if (gridBackVector == null) break;
			data += "_" + gridBackVector.getX();
			data += "_" + gridBackVector.getY();
			break;
		case MUSIC:
			if (musicTransition == null) break;
			data += "_" + musicTransition.getID();
			data += "_" + transitionDuration;
			if (musicPath == null) break;
			data += "_" + musicPath;
			break;
		case ENTITY:
			// TO BE ADDED
			break;
		case CAMERA:
			if (cameraMovement == null) break;
			data += "_" + cameraMovement.getID();
			if (cameraScroll == null) break;
			data += "_" + cameraScroll.getX();
			data += "_" + cameraScroll.getY();
			break;
		}
		return data;
	}
	
	// Returns the tile bounds
	public Rectangle getBounds() {
		return new Rectangle(this.getX(), this.getY(), Terrain.GRID_SIZE, Terrain.GRID_SIZE);
	}
	
}
