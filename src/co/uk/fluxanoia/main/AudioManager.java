package co.uk.fluxanoia.main;

import co.uk.fluxanoia.map.Trigger.MusicTransition;
import co.uk.fluxanoia.util.Tween;
import co.uk.fluxanoia.util.Tween.TweenType;
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
    private MediaPlayer music, fadeChannel;
    // The music volume
    private Tween musicVolume;
    // The desired volumes
    private double sfxVolume, fullMusicVolume;
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
        sfxVolume = 0.5;
        fullMusicVolume = 0.5;
        musicVolume = new Tween(fullMusicVolume);
        music = null;
        fadeChannel = null;
        loaded = false;
        // Loads tracks
        load();
    }

    // Updates the audio manager
    public void update() {
        if (!loaded) {
            for (String p : load_paths) {
                if (resourceManager.getMP3(p).getStatus() == MediaPlayer.Status.UNKNOWN) return;
            }
            loaded = true;
        }
        musicVolume.update();
        if (musicVolume.dropMoved()) {
        	double vol = musicVolume.value() * fullMusicVolume;
        	if (music != null) music.setVolume(vol);
        	if (fadeChannel != null) fadeChannel.setVolume(fullMusicVolume - vol);
        }
        if (musicVolume.hasArrived() && fadeChannel != null) {
        	fadeChannel.stop();
        	fadeChannel = null;
        }
        if (musicVolume.value() == 0) {
        	this.stopMusic();
        	music = null;
        }
    }

    // Loads the tracks to be loaded
    private void load() {
        for (String p : load_paths) {
            resourceManager.getMP3(p);
        }
    }

    // Plays a new music track
    public void changeMusic(MusicTransition mt, int duration, String path) {
    	switch (mt) {
		case FADE_IN:
			musicVolume.set(0);
			musicVolume.move(TweenType.LINEAR, 1, duration, 0);
			if (fadeChannel != null) fadeChannel.stop();
			fadeChannel = music;
			music = resourceManager.getMP3(path);
	        music.setVolume(musicVolume.value());
	        music.stop();
	        music.setCycleCount(MediaPlayer.INDEFINITE);
	        music.play();
			break;
		case FADE_OUT:
			musicVolume.push(TweenType.LINEAR, 0, duration, 0);
			if (fadeChannel != null) {
				fadeChannel.stop();
				fadeChannel = null;
			}
			break;
		case SET:
			this.stopMusic();
			if (fadeChannel != null) {
				fadeChannel.stop();
				fadeChannel = null;
			}
	        music = resourceManager.getMP3(path);
	        music.setVolume(musicVolume.value());
	        music.stop();
	        music.setCycleCount(MediaPlayer.INDEFINITE);
	        music.play();
			break;
		case STOP:
			this.stopMusic();
			music = null;
			if (fadeChannel != null) {
				fadeChannel.stop();
				fadeChannel = null;
			}
			break;
    	}
    }
    
    // Stops the current music track
    public void stopMusic() {
    	if (music != null) music.stop();
    }

    // Plays a sound effect
    public void playSFX(String path) {
        MediaPlayer sfx = resourceManager.getMP3(path);
        sfx.setVolume(sfxVolume);
        sfx.stop();
        sfx.setCycleCount(1);
        sfx.play();
    }

    // Returns whether the audio manager has loaded all it needs yet
    public boolean isLoaded() { return loaded; }

}
