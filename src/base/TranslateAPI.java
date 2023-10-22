package base;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;

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

    public static void speakAudio(String text, String languageOutput) throws IOException, URISyntaxException {
        String urlString = "http://translate.google.com/translate_tts?" + "?ie=UTF-8" + //encoding
                "&q=" + URLEncoder.encode(text, StandardCharsets.UTF_8) + //query encoded
                "&tl=" + TranslateAPI.langMap.get(languageOutput) + //language used
                "&client=tw-ob";
        URL url = new URI(urlString).toURL();
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestMethod("GET");
        urlConn.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream audioSrc = urlConn.getInputStream();
        DataInputStream read = new DataInputStream(audioSrc);
        OutputStream outstream = new FileOutputStream("src/resources/speech.mp3");
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
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] lang = line.split(" ");
            langMap.put(lang[0], lang[1]);
        }
        fileReader.close();
    }
    public static String[] theSaurus(String word, String type) throws URISyntaxException, IOException {
        if (word.contains(" ")) {
            word = word.replace(" ", "_");
        }
        String urlScript = "https://dictionaryapi.com/api/v3/references/thesaurus/json/" + word +"?key=8d9232c5-ec85-49a3-a070-3b63dbc55fc8";
        URL url = new URI(urlScript).toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("accept", "application/json");
        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String data = response.toString();
        if (data.equals("[]")) {
            return null;
        }
        if (!data.contains("syns") || !data.contains("ants")) {
            if (type.equals("ant")) {
                return null;
            }
            else {
                String syns = data.substring(data.indexOf("[") + 1, data.indexOf("]") - 1);
                String[] syn = syns.split(",");
                for (int i = 0; i < syn.length; i++) {
                    syn[i] = syn[i].substring(1, syn[i].length() - 1);
                }
                return syn;
            }
        }
        if ((data.indexOf("syns") + 8 > data.indexOf("ants") - 4) && Objects.equals(type, "syn")) {
            return null;
        }
        if ((data.indexOf("ants") + 8 > data.indexOf("offensive") - 4) && Objects.equals(type, "ant")) {
            return null;
        }
        if (type.equals("syn")) {
            String syns = data.substring(data.indexOf("syns") + 8, data.indexOf("ants") - 4);
            String[] syn = syns.split(",");
            for (int i = 0; i < syn.length; i++) {
                syn[i] = syn[i].substring(1, syn[i].length() - 1);
            }
            return syn;
        }
        if (type.equals("ant")) {
            String ants = data.substring(data.indexOf("ants") + 8, data.indexOf("offensive") - 4);
            String[] ant = ants.split(",");

            for (int i = 0; i < ant.length; i++) {
                ant[i] = ant[i].substring(1, ant[i].length() - 1);
            }
            return ant;
        }
        return null;
    }
}