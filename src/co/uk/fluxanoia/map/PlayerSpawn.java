package co.uk.fluxanoia.map;

import co.uk.fluxanoia.entity.Entity.EntityIndex;

// The PlayerSpawn class, represents a player spawn location
public class PlayerSpawn extends Cell {

	// Structure of player spawns in files
	// ...ENTITY
	
	// The spawning entity index
	private EntityIndex entityIndex;
	
	// Constructs a player spawn location
	public PlayerSpawn(int x, int y) {
		// Construct the cell
		super(x, y, -1, -1, CellType.PLAYER_SPAWN);
		this.entityIndex = null;
	}
	
	// Returns a summary of this trigger
	public String[] getInfo() {
		String e = "NULL";
		if (entityIndex != null) e = entityIndex.getID();
		return new String[] {"Entity: " + e };
	}

	// Takes a string and translates it into specific player spawn information
	public void parseData(String s) {
		entityIndex = EntityIndex.getIndex(s);
	}

	// Returns the data about this player spawn in a string
	public String getData() {
		String e = "";
		if (entityIndex != null) e = entityIndex.getID();
		return e;
	}
	
	// Returns the entity index
	public EntityIndex getEntityIndex() {
		return entityIndex;
	}
	
}
