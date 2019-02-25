package co.uk.fluxanoia.map;

import co.uk.fluxanoia.main.ErrorHandler;
import co.uk.fluxanoia.util.Tween;

// The cell class, representing a part of the map
public abstract class Cell {
	
	// Structure of cells in files
	// TYPE_X_Y_...
	
	// The types of cells
	public enum CellType {
		TRIGGER("tri", "Trigger"),
		TILE("til", "Tile"),
		PLAYER_SPAWN("psw", "Player Spawn");
		
		private String id, name;
		CellType(String id, String name) { 
			this.id = id;
			this.name = name;
		}
		public String getID() { return id; }
		public String getName() { return name; }
		
		public static CellType parse(String s) {
			for (CellType ct : CellType.values()) {
				if (ct.getID().equals(s)) return ct;
			}
			return null;
		}
	}
	
	// The type of this cell
	private CellType type;
	// The x and y values of the cell
	private Tween x, y;
	// The cell x and y values
	private int cellx, celly;
	// The x and y values of the cells texture
	private int textureX, textureY;
	
	// Constructs a Cell
	public Cell(int cellx, int celly, int textureX, int textureY, CellType type) {
		// Check for null values
		ErrorHandler.checkNull(type, "A Cell was given a null type.");
		// Assign values
		this.type = type;
		this.cellx = cellx;
		this.celly = celly;
		this.x = new Tween(cellx * Terrain.GRID_SIZE);
		this.y = new Tween(celly * Terrain.GRID_SIZE);
		this.textureX = textureX;
		this.textureY = textureY;
	}
	
	// Allows the cell to return specific information
	public abstract String[] getInfo();
	// Allows the cell to parse specific information
	public abstract void parseData(String s);
	// Reduces the extra information of a specific cell into a string
	public abstract String getData();
	
	// Reduces the cell into a string detailing its structure
	public String getCellData() {
		String data = type.getID() + "_" + this.getCellX() + "_" + this.getCellY();
		if (!getData().equals("")) {
			data += "_" + getData();
		}
		return data;
	}
	
	// Sets the texture x and y values
	public void setTextureX(int textureX) { this.textureX = textureX; }
	public void setTextureY(int textureY) { this.textureY = textureY; }
	
	// Returns the x and y tweens
	public Tween getTweenX() { return x; }
	public Tween getTweenY() { return y; }
	// Returns the x and y values
	public int getX() { return (int) this.x.value(); }
	public int getY() { return (int) this.y.value(); }
	// Returns the x and y cell values
	public int getCellX() { return (int) this.cellx; }
	public int getCellY() { return (int) this.celly; }
	// Returns the texture x and y values
	public int getTextureX() { return textureX; }
	public int getTextureY() { return textureY; }
	// Returns the type of cell
	public CellType getCellType() { return this.type; }

}
