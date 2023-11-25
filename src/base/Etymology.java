package base;

import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Etymology {
    private static String buildLink(String word) {
        return "https://en.wiktionary.org/w/api.php?action=query&explaintext=&prop=extracts&titles="
            + URLEncoder.encode(word, StandardCharsets.UTF_8)
            + "&format=json";
    }
    private static String parseResponse(String response, String language) {
        language = "== " + language + " ==";
        response = StringEscapeUtils.unescapeJava(response);
        response = response.replaceAll("\n","\\\\n");
        StringBuilder etymology = new StringBuilder();
        if (!response.contains("Etymology")) return "Not available.";
        response = response.substring(response.indexOf(language) + language.length());
        Matcher match = Pattern.compile("(?<!=)==(?!=)").matcher(response);
        if (match.find()) {
            response = response.substring(0, match.start());
        }
        int index = response.indexOf("=== Etymology");
        while (index != -1) {
            etymology.append(response, response.indexOf("===", index + 13) + 3, response.indexOf("===", response.indexOf("===", index + 13) + 3)).append("\n");
            index = response.indexOf("=== Etymology", index + 13);
        }
        return StringEscapeUtils.unescapeJava(etymology.toString());
    }
    public static String etymologyLookup(String word, String language) throws IOException, URISyntaxException {
        String urlScript = buildLink(word);
        HttpURLConnection con = (HttpURLConnection) new URI(urlScript).toURL().openConnection();
        String response = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return parseResponse(response,language);
    }
}
