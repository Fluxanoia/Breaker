package co.uk.fluxanoia.main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

// The resource manager, helps the loading of resources be minimal
public class ResourceManager {

	// The separator between keys and values in files
	private static final char KEY_SEP = ':';

	// Commonly used colours
	public static final Color COLOUR_RED = new Color(255, 60, 95);
	public static final Color COLOUR_PINK = new Color(255, 70, 220);
	public static final Color COLOUR_BLUE = new Color(70, 220, 255);
	public static final Color COLOUR_YELLOW = new Color(245, 240, 65);
	public static final Color COLOUR_GREEN = new Color(70, 240, 65);

	// The hash map containing textures
	private HashMap<String, BufferedImage> images;
	// The hash map containing mp3 files
	private HashMap<String, MediaPlayer> tracks;

	// Constructs the ResourceManager
	public ResourceManager() {
		// Initialises values
		images = new HashMap<>();
		tracks = new HashMap<>();
	}

	// ------------------------------------- MUSIC TRACKS

	// Returns the mp3 file at the path, loading it if not available
	public MediaPlayer getMP3(String path) {
		if (tracks.get(path) == null) {
			Media m = new Media(new File(path).toURI().toString());
			tracks.put(path, new MediaPlayer(m));
		}
		return tracks.get(path);
	}

	// ------------------------------------- IMAGES

	// Returns the image at the path, loading it if not available
	public BufferedImage getImage(String path) {
		// Checks if the path is already in the map
		if (images.get(path) == null) {
			// Load the image if it's not here
			try {
				// Load the image from a stream
				images.put(path, ImageIO.read(new File(path)));
			} catch (Exception e) {
				// If an error occurs, print the error and close
				System.err.println("Image could not be loaded at: " + path);
				System.err.println("Stack trace:");
				e.printStackTrace();
				System.exit(1);
			}
		}
		// Return the image at the path
		return images.get(path);
	}

	// Returns either the subimage from the tileset or the empty texture
	public static BufferedImage getTile(BufferedImage tileset,
			BufferedImage empty, int x, int y, int size) {
		return ResourceManager.getTile(tileset, empty, x, y, size, size);
	}

	public static BufferedImage getTile(BufferedImage tileset,
			BufferedImage empty, int x, int y, int width, int height) {
		try {
			return tileset.getSubimage(x * width, y * height, width, height);
		} catch (Exception e) {
			return empty;
		}
	}

	// ------------------------------------- FILES

	// Reads in a file in lines to an array
	public static ArrayList<String> readFile(String path) {
		// Get the jar path and attach the ending
		File file = new File(getPath() + path);
		// Prepare the array
		ArrayList<String> strings = new ArrayList<String>();
		// Scan the lines
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				strings.add(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		// Return
		return strings;
	}

	// Read the value after the first key in a file
	public static String readValue(String path, String key) {
		// Get the jar path and attach the ending
		File file = new File(getPath() + path);
		// Prepare a split array
		String[] split;
		// Scan the lines
		try {
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) {
				split = sc.nextLine().split(ResourceManager.KEY_SEP + " ");
				if (split[0].equals(key)) {
					sc.close();
					return split[1];
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Edits the values at the first key in a file, returning true for success
	public static boolean editValue(String path, String key, String value) {
		// Prepare variables for tracking
		int line = 0;
		String[] split = null;
		// Get the file to be written to and
		ArrayList<String> strings = readFile(path);
		// Iterate through the strings looking for the key
		for (String s : strings) {
			split = s.split(KEY_SEP + " ");
			if (split[0].equals(key)) break;
			line++;
		}
		// If not found, return
		if (line >= strings.size()) return false;
		strings.set(line, split[0] + KEY_SEP + " " + value);
		// Write to file
		overwriteFile(path, strings);
		// Return true for success
		return true;
	}

	// Copies a file
	public static boolean copyFile(String src, String dest) {
		// Read in the origin file
		ArrayList<String> strings = readFile(src);
		// Overwrite the file to change
		return overwriteFile(dest, strings);
	}

	// Overwrites a file
	public static boolean overwriteFile(String path,
			ArrayList<String> strings) {
		// Get the file
		File file = new File(getPath() + path);
		// Write to the file
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			for (String s : strings) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Creates a file, returning true if created
	public static boolean createFile(String path, ArrayList<String> strings) {
		// Create the file referencing the location
		File file = new File(getPath() + path);
		// If it doesn't exist, overwrite, otherwise return
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.out.println(
						"File at: " + path + ", could not be created.");
				return false;
			}
		}
		return overwriteFile(path, strings);
	}

	// Gets the path of the file
	public static String getPath() {
		try {
			return new File(ResourceManager.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath())
							.getParentFile().getPath()
					+ "\\";
		} catch (URISyntaxException e) {
			return "null";
		}
	}

}
