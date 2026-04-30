package services;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundService {

    private static final String SOUND_PATH = "/com/example/java_project/sounds/";

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
            URL resource = SoundService.class.getResource(SOUND_PATH + fileName);
            if (resource != null) {
                AudioClip clip = new AudioClip(resource.toString());
                clip.play();
            } else {
                System.out.println("Sound not found: " + fileName);
            }
        } catch (Exception e) {
            System.err.println("Error playing sound " + fileName + ": " + e.getMessage());
        }
    }
}
