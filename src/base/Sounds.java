package base;

import javafx.scene.media.AudioClip;

import java.nio.file.Paths;
import java.util.List;

public class Sounds {
    public static List<AudioClip> correctsound;
    public static AudioClip wrongsound;
    public static AudioClip countdownsound;
    public static void LoadSounds() {
        correctsound = List.of(new AudioClip(Paths.get("src/sounds/correct1.mp3").toUri().toString())
                , new AudioClip(Paths.get("src/sounds/correct2.mp3").toUri().toString()));
        wrongsound = new AudioClip(Paths.get("src/sounds/wrong.mp3").toUri().toString());
        countdownsound = new AudioClip(Paths.get("src/sounds/countdown.mp3").toUri().toString());
    }
}
