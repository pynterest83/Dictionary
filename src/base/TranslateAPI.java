package base;

import javafx.scene.control.MenuItem;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TranslateAPI {
    private static final String PATH = "src/resources/Spelling.txt";
    public static LinkedHashMap<String, String> langMap = new LinkedHashMap<>();

    public static String googleTranslate(String langFrom, String langTo, String text) throws IOException {
        String urlScript = "https://script.google.com/macros/s/AKfycbyniMmCcI4V-qH7DhHozrZgu-CE4boXZihvviN4fRKZXkhApuwdw4YDqKNJBbhfNpfZdQ/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlScript);
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

