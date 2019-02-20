package co.uk.fluxanoia.map;

// The PlayerSpawn class, represents a player spawn location
public class PlayerSpawn extends Cell {

	// Structure of player spawns in files
	// ...TYPE
	
	// Constructs a player spawn location
	public PlayerSpawn(int x, int y) {
		// Construct the cell
		super(x, y, -1, -1, CellType.PLAYER_SPAWN);
	}
	
	// Returns a summary of this trigger
	public String[] getInfo() {
		return new String[] {""};
	}

	// Takes a string and translates it into specific player spawn information
	public void parseData(String s) {
	}

	// Returns the data about this player spawn in a string
	public String getData() {
		return "";
	}
	
}
