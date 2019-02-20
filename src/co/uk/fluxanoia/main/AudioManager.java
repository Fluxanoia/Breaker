package co.uk.fluxanoia.main;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.MediaPlayer;

// The AudioManager class, manages all SFX and music
public class AudioManager {

    // The strings representing key paths
    public static final String MENU_MUSIC         = "res\\music\\hiding-your-reality-by-kevin-macleod.mp3";
    public static final String BUTTON_HOVER_ENTER = "res\\sfx\\interface\\hover enter.mp3";
    public static final String BUTTON_HOVER_EXIT  = "res\\sfx\\interface\\hover exit.mp3";
    public static final String BUTTON_SELECT      = "res\\sfx\\interface\\select.mp3";

    @SuppressWarnings("unused")
    // The FXPanel to initialise thed FX runtime
    private final JFXPanel fxPanel;

    // The resource manager
    private ResourceManager resourceManager;
    // The currently playing music
    private MediaPlayer music;
    // The music volume
    private double volume;
    // The tracks to be loaded
    private String[] load_paths = new String[] {
            MENU_MUSIC,
            BUTTON_HOVER_ENTER,
            BUTTON_HOVER_EXIT,
            BUTTON_SELECT
    };
    // Whether everything to be loaded has been loaded
    private boolean loaded;

    // Initialises the audio manager
    public AudioManager(ResourceManager resourceManager) {
        // Assigns values
        this.resourceManager = resourceManager;
        // Initialises values
        fxPanel = new JFXPanel();
        volume = 0.5;
        music = null;
        loaded = false;
        // Loads tracks
        load();
    }

    // Updates the audio manager
    public void update() {
        if (loaded) {
        } else {
            for (String p : load_paths) {
                if (resourceManager.getMP3(p).getStatus() == MediaPlayer.Status.UNKNOWN) {
                    return;
                }
            }
            loaded = true;
        }
    }

    // Loads the tracks to be loaded
    private void load() {
        for (String p : load_paths) {
            resourceManager.getMP3(p);
        }
    }

    // Plays a new music track
    public void playMusic(String path) {
        if (music != null) {
            music.stop();
        }
        music = resourceManager.getMP3(path);
        music.setVolume(volume);
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.stop();
        music.play();
    }

    // Plays a sound effect
    public void playSFX(String path) {
        MediaPlayer sfx = resourceManager.getMP3(path);
        sfx.stop();
        sfx.setCycleCount(1);
        sfx.play();
    }

    // Returns whether the audio manager has loaded all it needs yet
    public boolean isLoaded() { return loaded; }

}
