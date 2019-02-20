package co.uk.fluxanoia.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.sun.glass.events.KeyEvent;

import co.uk.fluxanoia.graphics.Display;
import co.uk.fluxanoia.main.GameMode;
import co.uk.fluxanoia.main.Main;
import co.uk.fluxanoia.main.ResourceManager;
import co.uk.fluxanoia.map.Cell.CellType;
import co.uk.fluxanoia.util.TextQuery;
import co.uk.fluxanoia.util.Tween;
import co.uk.fluxanoia.util.Tween.TweenType;

public class MapTool extends GameMode {

	// The amount scroll values are reduced before adding to the zoom level
	private static final double ZOOM_SCALE = 10;
	// The farthest out/in you can zoom
	private static final double ZOOM_OUT_LIMIT = 0.1;
	private static final double ZOOM_IN_LIMIT = 5;

	// The text size
	private static final int FONT_SIZE = 12;
	// The text buffer size
	private static final int TEXT_BUFFER = 3;
	// The help text
	private static final String[] HELP = { 
			"Camera movement:",
			"    WASD        - move the camera",
			"    SCROLL      - zoom in and out",
			"Cell management:",
			"    LEFT CLICK  - places a new cell",
			"    LEFT SHIFT  - deletes a cell",
			"    Q           - switches the cell type",
			"    E           - sets the cell data of cells to be placed",
			"    SPACE       - places a new cell (can overwrite)",
			"File management:",
			"    F (file)    - writes the current map to a file",
			"    R (read)    - reads a map in from a file"};

	// The text querying class
	private TextQuery dataQuery, saveQuery, readQuery;
	// The current tile type index
	private int cellType;
	// The current data
	private String heldData;

	// The amount of zoom
	private double zoom;
	// The hovered cell
	private int hovered_x, hovered_y;
	// Whether help should be displayed or not
	private boolean display_help;
	// The opacity of the notifying message
	private Tween notifyTween;
	// The notifying message
	private String notifyMessage;

	// The tools font
	private Font font;
	// Whether the tool should redraw or not
	private boolean redraw;
	// The display for the app
	private Display display;
	// Whether the map tool is closed or not
	private boolean closed;
	// The tileset and empty texture
	private BufferedImage tileset, empty;
	// The array of cells and cells awaiting adding
	private ArrayList<Cell> cells;

	// Constructs a MapTool
	public MapTool(Display display) {
		super(display);
		// Initialise values
		this.zoom = 1;
		this.cellType = 0;
		this.heldData = "";
		this.redraw = true;
		this.closed = false;
		this.display_help = false;
		this.font = new Font("Consolas", Font.PLAIN, FONT_SIZE);
		dataQuery = new TextQuery("Enter the cell data:");
		saveQuery = new TextQuery("Enter the file name (w/o ending):");
		readQuery = new TextQuery("Enter the file name (w/o ending):");
		this.notifyMessage = "";
		this.notifyTween = new Tween(0);
		hovered_x = hovered_y = 0;
		// Assign values
		this.display = display;
		// Set the tool up for map creation
		this.cells = new ArrayList<>();
		this.empty = display.getResourceManager().getImage("res\\game\\empty.png");
		this.tileset = display.getResourceManager().getImage("res\\game\\tileset.png");
	}

	// Updates the map tool
	public void update() {
		// Check for keyboard/mouse input
		checkInput();
		// Check if the mouse has moved
		redraw |= display.getListener().dropMouseMoved();
		// Check the text query
		// Data
		if (dataQuery.dropPressed() || dataQuery.dropCancelled()) {
			dataQuery.hide();
			heldData = dataQuery.getText();
		}
		if (dataQuery.dropCancelled()) dataQuery.hide();
		// Saving
		if (saveQuery.dropPressed()) {
			if (saveToFile()) {
				notify("Saved to " + saveQuery.getText() + ".level");
			}
			saveQuery.clear();
			saveQuery.hide();
		}
		if (saveQuery.dropCancelled()) {
			saveQuery.clear();
			saveQuery.hide();
		}
		// Reading
		if (readQuery.dropPressed()) {
			ArrayList<Cell> input = new ArrayList<>();
			redraw |= readFile(input, display, null, "res\\stages\\" + readQuery.getText() + ".level");
			if (input != null) {
				cells = input;
				notify("Read the file " + readQuery.getText() + ".level");
			}
			readQuery.clear();
			readQuery.hide();
		}
		if (readQuery.dropCancelled()) {
			readQuery.clear();
			readQuery.hide();
		}
		// Update the notifying tween
		notifyTween.update();
		redraw |= notifyTween.dropMoved();
	}

	// Draws the map tool
	public void draw(Graphics2D g1) {
		// Create the image to draw to
		BufferedImage drawImage = new BufferedImage(
				(int) Math.ceil(display.getCamera().getWidth()),
				(int) Math.ceil(display.getCamera().getHeight()),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) drawImage.getGraphics();
		// Draw a plain background
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, drawImage.getWidth(), drawImage.getHeight());
		// Some necessary variables
		int tx, ty;
		Rectangle r = display.getCamera().getBounds();
		// For all the cells in cells...
		for (Cell c : new ArrayList<Cell>(cells)) {
			// Get the positions releative to the camera position
			tx = (int) (c.getX() - display.getCamera().getX());
			ty = (int) (c.getY() - display.getCamera().getY());
			// If it's off the camera, don't draw
			if (tx > drawImage.getWidth()  || tx + Terrain.GRID_SIZE < 0) continue;
			if (ty > drawImage.getHeight() || ty + Terrain.GRID_SIZE < 0) continue;
			// If available for drawing...
			switch (c.getCellType()) {
			// If it's a tile...
			case TILE:
				// Split the data for tileset info
				((Tile) c).draw(g, r, ResourceManager.getTile(tileset, empty, c.getTextureX(), c.getTextureY(), Terrain.GRID_SIZE));
				break;
			case TRIGGER:
				// Fill the trigger in
				g.setColor(Color.RED);
				g.fillRect(tx, ty, Terrain.GRID_SIZE, Terrain.GRID_SIZE);
				break;
			case PLAYER_SPAWN:
				// Fill the spawn in
				g.setColor(Color.BLUE);
				g.fillRect(tx, ty, Terrain.GRID_SIZE, Terrain.GRID_SIZE);
				break;
			}
		}
		// Draw over the hovered cell
		g.setColor(new Color(255, 255, 255, 128));
		g.fillRect(
				(int) (hovered_x * Terrain.GRID_SIZE - display.getCamera().getX()), 
				(int) (hovered_y * Terrain.GRID_SIZE - display.getCamera().getY()),
				Terrain.GRID_SIZE, Terrain.GRID_SIZE);
		// Draw the image
		g1.drawImage(drawImage, 0, 0, Main.DRAW_WIDTH, Main.DRAW_HEIGHT, null);
		g.dispose();
		// Finish overlap text
		g1.setColor(Color.BLACK);
		g1.setFont(font);
		// Draw the x and y values
		g1.drawString("X: " + hovered_x, 
				(int) (display.getListener().getMouseX() + 10 + TEXT_BUFFER),
				(int) (display.getListener().getMouseY() + FONT_SIZE));
		g1.drawString("Y: " + hovered_y, 
				(int) (display.getListener().getMouseX() + 10 + TEXT_BUFFER),
				(int) (display.getListener().getMouseY() + FONT_SIZE * 2));
		// Draw the cell info
		Cell hover = getCell(hovered_x, hovered_y);
		if (hover != null) {
			// Draw the cell data
			g1.drawString("Type: " + hover.getCellType().getName(),
					(int) (display.getListener().getMouseX() + 10 + TEXT_BUFFER),
					(int) (display.getListener().getMouseY() + FONT_SIZE * 3));
			String[] info = hover.getInfo();
			for (int i = 0; i < info.length; i++) g1.drawString(info[i], 
					(int) (display.getListener().getMouseX() + 10 + TEXT_BUFFER),
					(int) (display.getListener().getMouseY() + FONT_SIZE * (4 + i)));
		}
		// Draw the data and type
		g1.drawString("Type: " + CellType.values()[cellType].getName(), TEXT_BUFFER, FONT_SIZE);
		g1.drawString("Data: " + heldData, TEXT_BUFFER, FONT_SIZE * 2);
		// Draw help text
		if (display_help) {
			for (int i = 0; i < HELP.length; i++) {
				g1.drawString(HELP[HELP.length - (i + 1)], 
						TEXT_BUFFER, 
						Main.DRAW_HEIGHT - i * FONT_SIZE - TEXT_BUFFER);
			}
		} else {
			g1.drawString("Press H to display help", TEXT_BUFFER, Main.DRAW_HEIGHT - TEXT_BUFFER);
		}
		// Draw the notification
		if (notifyTween.value() != 0) {
			g1.setColor(new Color(0, 0, 0, (int) notifyTween.value()));
			g1.drawString(notifyMessage, Main.DRAW_WIDTH - (TEXT_BUFFER + g1.getFontMetrics().stringWidth(notifyMessage)),
					FONT_SIZE);
		}
	}

	// Checks for user input
	private void checkInput() {
		// Manage mouse scrolling
		double prior_zoom = zoom;
		zoom -= this.display.getListener().dropScrollAmount() / ZOOM_SCALE;
		if (zoom < ZOOM_OUT_LIMIT)
			zoom = ZOOM_OUT_LIMIT;
		if (zoom > ZOOM_IN_LIMIT)
			zoom = ZOOM_IN_LIMIT;
		if (prior_zoom != zoom) {
			// Move the camera according to the zoom
			display.getCamera().getTweenWidth().move(TweenType.EASE_OUT, ((double) Main.DRAW_WIDTH) / zoom, 5, 0);
			display.getCamera().getTweenHeight().move(TweenType.EASE_OUT, ((double) Main.DRAW_HEIGHT) / zoom, 5, 0);
		}
		// Manage the mouse position
		hovered_x = (int) ((((double) this.display.getListener().getMouseX() / zoom) + display.getCamera().getX())
				/ (double) Terrain.GRID_SIZE);
		hovered_y = (int) ((((double) this.display.getListener().getMouseY() / zoom) + display.getCamera().getY())
				/ (double) Terrain.GRID_SIZE);
		// Manage mouse presses
		if (this.display.getListener().isMouseHeld(MouseEvent.BUTTON1)) {
			redraw |= addCell(cells, display, null, hovered_x, hovered_y, CellType.values()[cellType], heldData);
		}
		// Manages key presses
		// Displaying help
		boolean d_h = this.display_help;
		this.display_help = this.display.getListener().isKeyHeld(KeyEvent.VK_H);
		redraw |= (d_h != this.display_help);
		// Moving the camera (left, right, up, down)
		if (this.display.getListener().isKeyHeld(KeyEvent.VK_A)) {
			double dest = display.getCamera().getTweenX().getDestination() - 5;
			if (dest < 0)
				dest = 0;
			display.getCamera().getTweenX().move(TweenType.EASE_OUT, dest, 5, 0);
		}
		if (this.display.getListener().isKeyHeld(KeyEvent.VK_D)) {
			display.getCamera().getTweenX().move(TweenType.EASE_OUT, display.getCamera().getTweenX().getDestination() + 5, 5, 0);
		}
		if (this.display.getListener().isKeyHeld(KeyEvent.VK_W)) {
			double dest = display.getCamera().getTweenY().getDestination() - 5;
			if (dest < 0)
				dest = 0;
			display.getCamera().getTweenY().move(TweenType.EASE_OUT, dest, 5, 0);
		}
		if (this.display.getListener().isKeyHeld(KeyEvent.VK_S)) {
			display.getCamera().getTweenY().move(TweenType.EASE_OUT, display.getCamera().getTweenY().getDestination() + 5, 5, 0);
		}
		// Deleting cells
		if (this.display.getListener().isKeyHeld(KeyEvent.VK_SHIFT)) {
			Cell c = getCell(hovered_x, hovered_y);
			if (c != null) cells.remove(c);
			redraw = true;
		}
		// Setting type
		if (this.display.getListener().isKeyReleased(KeyEvent.VK_Q)) {
			cellType++;
			cellType %= CellType.values().length;
			redraw = true;
		}
		// Setting data
		if (this.display.getListener().isKeyReleased(KeyEvent.VK_E)) {
			dataQuery.clear();
			dataQuery.show();
		}
		// Editting data/type
		if (this.display.getListener().isKeyHeld(KeyEvent.VK_SPACE)) {
			Cell c = getCell(hovered_x, hovered_y);
			if (c != null) cells.remove(c);
			addCell(cells, display, null, hovered_x, hovered_y, CellType.values()[cellType], heldData);
			redraw = true;
		}
		// Write to file
		if (this.display.getListener().isKeyReleased(KeyEvent.VK_F)) {
			saveQuery.show();
		}
		// Read a file
		if (this.display.getListener().isKeyReleased(KeyEvent.VK_R)) {
			readQuery.show();
		}
	}

	// Saves the cells to a file
	public boolean saveToFile() {
		// If there's no file name, stop
		if (saveQuery.getText().equals(""))
			return false;
		// Initialise some variables
		String s = "";
		// For all the cells in cells...
		for (Cell c : new ArrayList<Cell>(cells)) {
			s += c.getCellData() + " ";
		}
		// Create the array to be written
		ArrayList<String> ss = new ArrayList<>();
		ss.add(s);
		// Write to file
		return ResourceManager.overwriteFile("res\\stages\\" + saveQuery.getText() + ".level", ss);
	}

	// Reads in a level file and converts it to an array of cells
	public static boolean readFile(ArrayList<Cell> cells, Display display, Terrain terrain, String path) {
		// Variables to hold the cell data
		int x, y;
		String data;
		CellType type;
		// Variables to hold the input data
		String[] cell, ids;
		// The input data
		ArrayList<String> input = ResourceManager.readFile(path);
		// If there's no input, return null
		if (input == null) return false;
		// We should now clear the input array
		cells.clear();
		// For all the strings in the input
		for (String s : input) {
			// Split by spaces
			cell = s.split(" ");
			// For each cell
			for (int i = 0; i < cell.length; i++) {
				// Break down the cell
				ids = cell[i].split("_");
				// If there's insufficient arguments, skip
				if (ids.length < 3) continue;
				// Parse values
				type = CellType.parse(ids[0]);
				x = Integer.valueOf(ids[1]);
				y = Integer.valueOf(ids[2]);
				// If there's invalid values, skip
				if (type == null) continue;
				// Load the remaining data into a string
				data = "";
				if (ids.length > 3) {
					data = ids[3];
					for (int j = 4; j < ids.length; j++) data += "_" + ids[j];
				}
				// Add the cell
				addCell(cells, display, terrain, x, y, type, data);
			}
		}
		return true;
	}

	// Adds a new cell
	private static boolean addCell(ArrayList<Cell> cells, Display display, Terrain terrain, int x, int y, CellType type, String heldData) {
		if (getCell(cells, x, y) != null) return false;
		switch (type) {
		case TILE:
			Tile t = new Tile(x, y, -1, -1, null);
			t.parseData(heldData);
			cells.add(t);
			break;
		case TRIGGER:
			Trigger tr = new Trigger(display, terrain, x, y, null, null);
			tr.parseData(heldData);
			cells.add(tr);
			break;
		case PLAYER_SPAWN:
			PlayerSpawn ps = new PlayerSpawn(x, y);
			ps.parseData(heldData);
			cells.add(ps);
			break;
		}
		return true;
	}
	
	// Returns the cell with position x, y
	private Cell getCell(int x, int y) {
		return getCell(cells, x, y);
	}
	// Returns the cell with position x, y in a given cell array
	private static Cell getCell(ArrayList<Cell> cells, int x, int y) {
		for (Cell c : cells) {
			if (c.getCellX() == x && c.getCellY() == y)
				return c;
		}
		return null;
	}
	
	// Sets up a new notifying message
	private void notify(String notify) {
		notifyTween.set(255);
		notifyTween.move(TweenType.LINEAR, 0, 60, 120);
		notifyMessage = notify;
		redraw = true;
	}

	// Gets the clip bounds of the active layer
	public Rectangle dropClipBounds() {
		if (this.redraw) {
			this.redraw = false;
			return Display.drawBounds();
		}
		return new Rectangle(0, 0, 0, 0);
	}

	// Returns whether the map tool is closed or not
	public boolean isClosed() {
		return closed;
	}

}
