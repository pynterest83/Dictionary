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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateAPI {
    private static final Pattern specialCharacters = Pattern.compile("[\\<\\(\\[\\{\\\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]");
    private static final String generalPattern = "(?<=((?<!\\[)\\[\"))(.*?)(?=\",\"";
    private static final String PATH = "src/resources/Spelling.txt";
    public static LinkedHashMap<String, String> langMap = new LinkedHashMap<>();
    public static String googleTranslate(String langFrom, String langTo, String text, boolean isParagraph) throws IOException, URISyntaxException {
        text = text.trim();
        if (isParagraph) text = text.replace("\n","");
        String urlScript = "https://translate.googleapis.com/translate_a/single?client=gtx&"
                + "sl=" + langFrom
                + "&tl=" + langTo
                + "&dt=t&dt=t&q=" + URLEncoder.encode(text,StandardCharsets.UTF_8);
        HttpURLConnection con = (HttpURLConnection) new URI(urlScript).toURL().openConnection();
        String response = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
        response = StringEscapeUtils.unescapeJava(response);
        return (isParagraph ? paragraphParser(text, response) : nonParagraphParser(text, response));
    }
    private static String nonParagraphParser(String text, String response) {
        response = response.replace("\n","\\n");
        StringBuilder translated = new StringBuilder();
        String[] lines =  text.split("\n");
        int begin = response.indexOf("\"") + 1;
        int end = response.indexOf("\",\"" + lines[0].split("\\.|\\?|!;")[0]);
        try {
            translated.append(response, begin, end);
            response = response.substring(end + 1);
        } catch(Exception e) {
            return "";
        }
        for (String line:lines) {
            String[] sentences = line.split("\\.|\\?|!;");
            for (int i = 0; i < sentences.length; i++) {
                sentences[i] = escapeAll(sentences[i].trim());
                Pattern pattern = Pattern.compile(generalPattern + sentences[i] + ")");
                Matcher match = pattern.matcher(response);
                match.find();
                try {
                    translated.append(match.group());
                    response = response.substring(match.end()+1);
                } catch(Exception ignored) {}
            }
        }
        return StringEscapeUtils.unescapeJava(translated.toString()).replace("\",\"","");
    }
    private static String paragraphParser(String text, String response) {
        StringBuilder translated = new StringBuilder();
        String[] lines = text.split("\\.|\\?|!;");
        int begin = response.indexOf("\"") + 1;
        int end = response.indexOf("\",\"" + lines[0]);
        try {
            translated.append(response, begin, end);
        } catch(Exception e) {
            return "";
        }
        for (int i = 1; i < lines.length; i++) {
            lines[i] = escapeAll(lines[i].trim());
            Pattern pattern = Pattern.compile(generalPattern + lines[i] + ")");
            Matcher match = pattern.matcher(response);
            match.find();
            try {
                translated.append(match.group());
                response = response.substring(match.end()+1);
            } catch(Exception ignored) {}
        }
        return StringEscapeUtils.unescapeJava(translated.toString()).replace("\",\"","");
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
    private static String escapeAll(String input) {
        Matcher matcher = specialCharacters.matcher(input);
        return matcher.replaceAll("\\\\$0");
    }
}