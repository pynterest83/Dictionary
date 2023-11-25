package base;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.MainController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class TranslateAPI {
    private static final Pattern pattern = Pattern.compile("(?<=((?<!\\[)\\[\\\"))(.*?)(?=\\\",\\\")");
    private static final String PATH = "src/resources/Spelling.txt";
    public static LinkedHashMap<String, String> langMap = new LinkedHashMap<>();
    public static String googleTranslate(String langFrom, String langTo, String text) throws IOException, URISyntaxException {
        String urlScript = "https://translate.googleapis.com/translate_a/single?client=gtx&"
                + "sl=" + langFrom
                + "&tl=" + langTo
                + "&dt=t&dt=t&q=" + URLEncoder.encode(text,StandardCharsets.UTF_8);
        HttpURLConnection con = (HttpURLConnection) new URI(urlScript).toURL().openConnection();
        String response = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
        StringBuilder translated = new StringBuilder();
        translated.append(response, response.indexOf("\"")+1, response.indexOf("\"",response.indexOf("\"")+1));
        String[] match = pattern.matcher(response).results().map(MatchResult::group).toArray(String[]::new);
        for (String s:match) translated.append(s).append("\n");
        return StringEscapeUtils.unescapeJava(translated.toString());
    }
    public static void speakAudio(String text, String languageOutput) {
        String urlString = "http://translate.google.com/translate_tts?" + "?ie=UTF-8" + //encoding
                "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) + //query encoded
                "&tl=" + TranslateAPI.langMap.get(languageOutput) + //language used
                "&client=tw-ob";
        Media voice = new Media(urlString);
        MediaPlayer mediaPlayer = new MediaPlayer(voice);
        mediaPlayer.setVolume(MainController.SpeakVolume);
        mediaPlayer.play();
    }
    public static void addDefault() throws IOException {
        File infile = new File(PATH);
        FileReader fileReader=  new FileReader(infile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] lang = line.split("\\|");
            langMap.put(lang[0], lang[1]);
        }
        fileReader.close();
    }
}