package base;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

public class TranslateAPI {
    private static final String PATH = "src/resources/Spelling.txt";
    public static LinkedHashMap<String, String> langMap = new LinkedHashMap<>();

    public static String googleTranslate(String langFrom, String langTo, String text) throws IOException, URISyntaxException {
        String urlScript = "https://script.google.com/macros/s/AKfycbyniMmCcI4V-qH7DhHozrZgu-CE4boXZihvviN4fRKZXkhApuwdw4YDqKNJBbhfNpfZdQ/exec" +
                "?q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URI(urlScript).toURL();
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
    private static String generate_TTS_URL(String language, String text) {
        return "http://translate.google.com/translate_tts?" + "?ie=UTF-8" + //encoding
                "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) + //query encoded
                "&tl=" + TranslateAPI.langMap.get(language) + //language used
                "&client=tw-ob";
    }
    public static void speakAudio(String text, String languageOutput) throws IOException, URISyntaxException {
        String urlString = generate_TTS_URL(languageOutput, text);
        URL url = new URI(urlString).toURL();
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("GET");
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream audioSrc = urlConn.getInputStream();
        DataInputStream read = new DataInputStream(audioSrc);
        OutputStream outstream = new FileOutputStream(new File("src/resources/speech.mp3"));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = read.read(buffer)) > 0) {
            outstream.write(buffer, 0, len);
        }
        outstream.close();
        Media voice = new Media(Paths.get("src/resources/speech.mp3").toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(voice);
        mediaPlayer.play();
    }
    public static void addDefault() throws IOException {
        File infile = new File(PATH);
        FileReader fileReader=  new FileReader(infile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            String[] lang = line.split(" ");
            langMap.put(lang[0], lang[1]);
        }
        fileReader.close();
    }
}

