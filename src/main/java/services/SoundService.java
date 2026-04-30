package services;

import javafx.scene.media.AudioClip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundService {

    private static final String SOUND_PATH = "/com/example/java_project/sounds/";
    private static final Map<String, AudioClip> soundCache = new HashMap<>();

    public static void playPop() {
        playSound("pop.mp3");
    }

    public static void playTick() {
        playSound("tick.mp3");
    }

    public static void playAlarm() {
        playSound("alarm.mp3");
    }

    public static void playSuccess() {
        playSound("success.mp3");
    }

    private static void playSound(String fileName) {
        try {
            if (soundCache.containsKey(fileName)) {
                soundCache.get(fileName).play();
                return;
            }

            URL resource = SoundService.class.getResource(SOUND_PATH + fileName);
            if (resource != null) {
                try {
                    AudioClip clip = new AudioClip(resource.toExternalForm());
                    soundCache.put(fileName, clip);
                    clip.play();
                } catch (Exception e) {
                    System.err.println("Error loading sound " + fileName + ": " + e.getMessage());
                }
            } else {
                System.err.println("Sound not found: " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
