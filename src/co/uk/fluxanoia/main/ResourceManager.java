package co.uk.fluxanoia.main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
		Media m = null;
		if (tracks.get(path) == null) {
			try {
				m = new Media(new File(path).toURI().toString());
			} catch (Exception e) {
				System.err.println("The media at path: " + path + ", could not be read.");
				e.printStackTrace();
				System.exit(1);
			}
			ErrorHandler.checkNull(m, "The media at path: " + path + ", could not be read.");
			tracks.put(path, new MediaPlayer(m));
		}
		return tracks.get(path);
	}

	// ------------------------------------- IMAGES

	// Returns the image at the path, loading it if not available
	public BufferedImage getImage(String path) {
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
		// Checks if the path is already in the map
		if (images.get(path) == null) {
			// Load the image if it's not here
			BufferedImage image = null;
			try {
				// Load the image from a stream
				image = ImageIO.read(new File(path));
			} catch (Exception e) {
				// If an error occurs, print the error and close
				System.err.println("The image at path: " + path + ", could not be read.");
				e.printStackTrace();
				System.exit(1);
			}
			ErrorHandler.checkNull(image, "The image at path: " + path + ", could not be read.");
			images.put(path, image);
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
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
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
		} catch (Exception e) {
			System.err.println("The file at path: " + path + ", could not be read.");
			e.printStackTrace();
			System.exit(1);
		}
		// Return
		return strings;
	}

	// Read the value after the first key in a file
	public static String readValue(String path, String key) {
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
		ErrorHandler.checkNull(key, "The ResourceManager was given a null key.");
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
		} catch (Exception e) {
			System.err.println("The value at key: " + key + ", in the file at path: " 
		        + path + ", could not be read.");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	// Edits the values at the first key in a file, returning true for success
	public static boolean editValue(String path, String key, String value) {
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
		ErrorHandler.checkNull(key, "The ResourceManager was given a null key.");
		ErrorHandler.checkNull(value, "The ResourceManager was given a null value.");
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
	public static void copyFile(String src, String dest) {
		ErrorHandler.checkNull(src, "The ResourceManager was given a null source path.");
		ErrorHandler.checkNull(dest, "The ResourceManager was given a null destination path.");
		// Read in the origin file
		ArrayList<String> strings = readFile(src);
		// Overwrite the file to change
		overwriteFile(dest, strings);
	}

	// Overwrites a file
	public static void overwriteFile(String path,
			ArrayList<String> strings) {
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
		ErrorHandler.checkNull(strings, "The ResourceManager was given a null arraylist of strings.");
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
		} catch (Exception e) {
			System.err.println("The file at path: " + path + ", could not be overwritten.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	// Creates a file, returning true if created
	public static void createFile(String path, ArrayList<String> strings) {
		ErrorHandler.checkNull(path, "The ResourceManager was given a null path (String).");
		ErrorHandler.checkNull(strings, "The ResourceManager was given a null arraylist of strings.");
		// Create the file referencing the location
		File file = new File(getPath() + path);
		// If it doesn't exist, overwrite, otherwise return
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				System.err.println("The file at path: " + path + ", could not be created.");
				e.printStackTrace();
				System.exit(1);
			}
		}
		overwriteFile(path, strings);
	}

	// Gets the path of the file
	public static String getPath() {
		try {
			return new File(ResourceManager.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath())
							.getParentFile().getPath()
					+ "\\";
		} catch (Exception e) {
			System.err.println("The running directory of the program could not be found.");
			e.printStackTrace();
			System.exit(1);
		}
		return "null path (String)";
	}

}
