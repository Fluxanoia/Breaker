package co.uk.fluxanoia.map;

import java.awt.Color;
import java.awt.Rectangle;

import co.uk.fluxanoia.control.Controller.ControllerType;
import co.uk.fluxanoia.entity.Entity.EntityIndex;
import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.main.ErrorHandler;
import javafx.geometry.Point2D;

// The Trigger class, represents an action in the game world
public class Trigger extends Cell{
	
	// The types of triggers
	public enum TriggerType {
		ONSCREEN("ons", "Onscreen"),
		OFFSCREEN("off", "Offscreen"),
		PLAYER_X("pxp", "Player X"),
		PLAYER_Y("pyp", "Player Y"),
		PLAYER_XY("pxy", "Player X & Y");
		private String id, name;
		TriggerType(String id, String name) { 
			this.id = id; 
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static TriggerType parse(String s) {
			ErrorHandler.checkNull(s, "The TriggerType enum was given a null input string.");
			for (TriggerType tt : TriggerType.values()) {
				if (tt.getID().equals(s)) return tt;
			}
			return null;
		}
	}
	
	// The effects of triggers
	public enum TriggerEffect {
		MUSIC("mus", "Music"), 
		SFX("sfx", "Sound Effect"),
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
			ErrorHandler.checkNull(s, "The TriggerEffect enum was given a null input string.");
			for (TriggerEffect te : TriggerEffect.values()) {
				if (te.getID().equals(s)) return te;
			}
			return null;
		}
	}
	
	// The type of music change
	public enum MusicTransition {
		SET("set", "Set"), 
		FADE_IN("fin", "Fade in"),
		FADE_OUT("fou", "Fade out"),
		STOP("stp", "Stop");
		private String id, name;
		MusicTransition(String id, String name) { 
			this.id = id;
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static MusicTransition parse(String s) {
			ErrorHandler.checkNull(s, "The MusicTransition enum was given a null input string.");
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
			ErrorHandler.checkNull(s, "The CameraMovement enum was given a null input string.");
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
	// The index of the spawning entity
	private EntityIndex entityIndex;
	// The type of controller of the entity
	private ControllerType entityController;
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
		// Check for null values
		ErrorHandler.checkNull(display, "A Trigger was given a null display.");
		ErrorHandler.checkNull(terrain, "A Trigger was given a null terrain.");
		// Assign values
		this.display = display;
		this.terrain = terrain;
		this.type = type;
		this.effect = effect;
		// Initialise values
		this.onscreen = false;
		this.ready = false;
		this.activated = false;
		this.entityIndex = null;
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
		if (!onscreen && bounds.intersects(terrain.getCamera().getBounds())) {
			onscreen = true;
		}
		switch (type) {
		case OFFSCREEN:
			if (!bounds.intersects(terrain.getCamera().getBounds()) && onscreen) {
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
				Rectangle bounds = this.getBounds();
				terrain.addEntity(entityIndex, ControllerType.getController(entityController, display.getListener()), 
						(int) bounds.getCenterX(), (int) bounds.getCenterY());
				break;
			case MUSIC:
				display.getAudioManager().changeMusic(musicTransition, transitionDuration, musicPath);
				break;
			case SFX:
				display.getAudioManager().playSFX(musicPath);
				break;
			}
			return true;
		}
		return false;
	}
	
	// Takes a string and translates it into specific trigger information
	public void parseData(String s) {
		ErrorHandler.checkNull(s, "A Trigger was given a null input string.");
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
				int r, g, b;
				try {
					r = Integer.valueOf(split[2]);
					g = Integer.valueOf(split[3]);
					b = Integer.valueOf(split[4]);
				} catch (NumberFormatException e) {
					r = g = b = 0;
				}
				this.gridColour = new Color(r, g, b);
				double vx, vy;
				try {
					vx = Double.valueOf(split[5]);
					vy = Double.valueOf(split[6]);
				} catch (NumberFormatException e) {
					vx = vy = 0;
				}
				this.gridForeVector = new Point2D(vx, vy);
				try {
					vx = Double.valueOf(split[7]);
					vy = Double.valueOf(split[8]);
				} catch (NumberFormatException e) {
					vx = vy = 0;
				}
				this.gridBackVector = new Point2D(vx, vy);
			}
			if (split.length > 10) {
				this.gridOpacity = Double.valueOf(split[9]);
				this.gridDuration = Integer.valueOf(split[10]);
			} else {
				this.gridOpacity = 1;
				this.gridDuration = 1;
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
			if (split.length > 2) entityIndex = EntityIndex.getIndex(split[2]);
			if (split.length > 3) entityController = ControllerType.getType(split[3]);
			break;
		case MUSIC:
			if (split.length > 2) {
				musicTransition = MusicTransition.parse(split[2]);
			}
			if (split.length > 3) {
				try {
					transitionDuration = Integer.valueOf(split[3]);
				} catch (NumberFormatException e) {
					transitionDuration = 0;
				}
				if (transitionDuration < 0) transitionDuration = 0;
			}
			if (split.length > 4) {
				musicPath = split[4];
			}
			break;
		case SFX:
			if (split.length > 2) musicPath = split[2];
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
		case SFX:
			if (musicPath == null) break;
			info += "Playing " + musicPath + "_";
			break;
		case ENTITY:
			if (entityIndex != null) info += "Entity: " + entityIndex.getID() + "_";
			if (entityController != null) info += "Controller: " + entityController.getID() + "_";
			break;
		case MUSIC:
			if (musicTransition != null) {
				info += "Transition of " + musicTransition.getName() + "_Lasting "
						+ transitionDuration + "_To " + musicPath + "_";
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
		case SFX:
			if (musicPath == null) break;
			data += "_" + musicPath;
			break;
		case ENTITY:
			if (entityIndex == null) break;
			data += "_" + entityIndex.getID();
			if (entityController == null) break;
			data += "_" + entityController.getID();
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
