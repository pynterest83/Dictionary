package base;

import com.voicerss.tts.AudioFormat;
import com.voicerss.tts.VoiceParameters;
import com.voicerss.tts.VoiceProvider;
import javafx.scene.media.AudioClip;
import com.voicerss.tts.Languages;
import com.voicerss.tts.AudioCodec;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.FileOutputStream;
import java.nio.file.Paths;

public class VoiceRSS {
    private static final String API_KEY = "4aceebbbab2946d5b434b3bb810bd2eb";
    private static final String AUDIO_PATH = "src/resources/audio.wav";
    public static void speakWord(String word) throws Exception {
        VoiceProvider tts = new VoiceProvider(API_KEY);
        VoiceParameters params = new VoiceParameters(word, Languages.English_UnitedStates);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
        byte[] voice = tts.speech(params);
        FileOutputStream fos = new FileOutputStream(AUDIO_PATH);
        fos.write(voice, 0, voice.length);
        fos.flush();
        fos.close();
    }
    public static void speak(String word) throws Exception {
        speakWord(word);
        Media voice = new Media(Paths.get("src/resources/audio.wav").toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(voice);
        mediaPlayer.play();
    }
}
