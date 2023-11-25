package base;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class UsageGraph {
    private static String buildLink(String requestString) {
        if (requestString.contains("(") || requestString.contains(")")) {
            requestString = requestString.substring(0, requestString.indexOf("(") - 1);
        }
        return "https://books.google.com/ngrams/json?content="
                + requestString
                + "&year_start=1800&year_end=2022&case_insensitive=on&corpus=26&smoothing=2";
    }
    public static double[] graphData(String requestString) {
        StringBuilder response = new StringBuilder();
        try {
            HttpURLConnection con = (HttpURLConnection) new URI(buildLink(requestString)).toURL().openConnection();
            response.append(IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
        String responseString = response.toString();
        if (responseString.equals("[]")) {
            return new double[0];
        }
        String[] temp = responseString
                .substring(responseString.indexOf("timeseries") + 14, responseString.indexOf("]"))
                .split(", ");
        double[] wordData = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            wordData[i] = Double.parseDouble(temp[i]);
        }
        return wordData;
    }
}
