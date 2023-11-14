package base;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.MainController;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Objects;
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
    public static String[] theSaurus(String word, String type) throws URISyntaxException, IOException {
        if (word.contains(" ")) {
            word = word.replace(" ", "%20");
        }
        String urlScript = "https://dictionaryapi.com/api/v3/references/thesaurus/json/" + word +"?key=8d9232c5-ec85-49a3-a070-3b63dbc55fc8";
        HttpURLConnection con = (HttpURLConnection) new URI(urlScript).toURL().openConnection();
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
                if (syn[i].contains("[")) {
                    syn[i] = syn[i].replace("[", "");
                }
                if (syn[i].contains("]")) {
                    syn[i] = syn[i].replace("]", "");
                }
                syn[i] = syn[i].substring(1, syn[i].length() - 1);
            }
            return syn;
        }
        if (type.equals("ant")) {
            String ants = data.substring(data.indexOf("ants") + 8, data.indexOf("offensive") - 4);
            String[] ant = ants.split(",");

            for (int i = 0; i < ant.length; i++) {
                if (ant[i].contains("[")) {
                    ant[i] = ant[i].replace("[", "");
                }
                if (ant[i].contains("]")) {
                    ant[i] = ant[i].replace("]", "");
                }
                ant[i] = ant[i].substring(1, ant[i].length() - 1);
            }
            return ant;
        }
        return null;
    }
    public static double[] graphData(String requestString) {
        if (requestString.contains("(") || requestString.contains(")")) {
            requestString = requestString.substring(0, requestString.indexOf("(") - 1);
        }
        String urlScript = "https://books.google.com/ngrams/json?content="
                + requestString
                + "&year_start=1800&year_end=2022&case_insensitive=on&corpus=26&smoothing=2";
        URL url;
        try {
            url = new URI(urlScript).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        con.setRequestProperty("accept", "application/json");
        StringBuilder response = new StringBuilder();
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            return null;
        }
        double[] wordData;
        try {
            response.append(in.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (response.toString().equals("[]")) {
            return new double[0];
        }
        String responseString = response.toString();
        String[] temp = responseString
                .substring(responseString.indexOf("timeseries") + 14, responseString.indexOf("]"))
                .split(", ");
        wordData = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            wordData[i] = Double.parseDouble(temp[i]);
        }
        return wordData;
    }
}