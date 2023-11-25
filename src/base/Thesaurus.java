package base;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Thesaurus {
    private static final String APIKey = "8d9232c5-ec85-49a3-a070-3b63dbc55fc8";

    private static String[] parseResponse(String response, String type) {
        if (response.equals("[]")) {
            return null;
        }
        if (!response.contains("syns") || !response.contains("ants")) {
            if (type.equals("ant")) {
                return null;
            } else {
                String syns = response.substring(response.indexOf("[") + 1, response.indexOf("]") - 1);
                String[] syn = syns.split(",");
                for (int i = 0; i < syn.length; i++) {
                    syn[i] = syn[i].substring(1, syn[i].length() - 1);
                }
                return syn;
            }
        }
        if ((response.indexOf("syns") + 8 > response.indexOf("ants") - 4) && Objects.equals(type, "syn")) {
            return null;
        }
        if ((response.indexOf("ants") + 8 > response.indexOf("offensive") - 4) && Objects.equals(type, "ant")) {
            return null;
        }
        if (type.equals("syn")) {
            String syns = response.substring(response.indexOf("syns") + 8, response.indexOf("ants") - 4);
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
            String ants = response.substring(response.indexOf("ants") + 8, response.indexOf("offensive") - 4);
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

    public static String[] theSaurus(String word, String type) throws URISyntaxException, IOException {
        String urlScript = "https://dictionaryapi.com/api/v3/references/thesaurus/json/"
                + URLEncoder.encode(word, StandardCharsets.UTF_8)
                + "?key=" + APIKey;
        HttpURLConnection con = (HttpURLConnection) new URI(urlScript).toURL().openConnection();
        String response = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
        return parseResponse(response, type);
    }
}
