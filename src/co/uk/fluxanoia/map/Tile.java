package co.uk.fluxanoia.map;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

// The Tile class, represents a small section of the game's terrain
public class Tile extends Cell {

	// Structure of tiles in files
	// ...TYPE_TX_TY
	
	// The types of tiles
	public enum TileType {
		COLLIDE("col", "Collision Enabled"), 
		NO_COLLIDE("ncl", "Collision Disabled");
		private String id, name;
		TileType(String id, String name) { 
			this.id = id;
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static TileType parse(String s) {
			for (TileType tt : TileType.values()) {
				if (tt.getID().equals(s)) return tt;
			}
			return null;
		}
	}

	// The type of the tile
	private TileType type;
	
	// Constructs a Tile
	public Tile(int x, int y, int textureX, int textureY, TileType type) {
		// Construct the cell
		super(x, y, textureX, textureY, CellType.TILE);
		// Assign values
		this.type = type;
	}
	
	// Draws the tile
	public void draw(Graphics2D g, Rectangle r, BufferedImage image) {
		// If the tile is out of bounds, return
		if (!(r.intersects(this.getBounds()))) return;
		// Draw the tile
		g.drawImage(image,
				(int) (this.getX() - r.getX()),
				(int) (this.getY() - r.getY()),
				null);
	}

	// Takes a string and translates it into specific tile information
	public void parseData(String s) {
		String[] split = s.split("_");
		if (split.length > 0) {
			TileType tt = TileType.parse(split[0]);
			if (tt != null) type = tt;
		}
		if (split.length > 1) {
			this.setTextureX(Integer.valueOf(split[1]));
		}
		if (split.length > 2) {
			this.setTextureY(Integer.valueOf(split[2]));
		}
	}

	// Returns the data about this tile in a string
	public String getData() {
		if (type == null) return "";
		return type.getID() + "_" + this.getTextureX() + "_" + this.getTextureY();
	}
	
	// Returns a summary of this tile
	public String[] getInfo() {
		if (type == null) return new String[] {"No type"};
		return new String[] {"TX: " + this.getTextureX(), "TY: " + this.getTextureY(), type.getName()};
	}
	
	// Returns the bounds of the tile
	public Rectangle getBounds() {
		return new Rectangle(this.getX(), this.getY(),
				Terrain.GRID_SIZE, Terrain.GRID_SIZE);
	}
	
	// Returns the tile's type
	public TileType getType() { return type; }
	
}
