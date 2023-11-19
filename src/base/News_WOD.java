package base;

import com.google.gson.JsonObject;
import com.jzhangdeveloper.newsapi.models.*;
import com.jzhangdeveloper.newsapi.net.NewsAPIClient;
import com.jzhangdeveloper.newsapi.net.NewsAPIResponse;
import com.jzhangdeveloper.newsapi.params.EverythingParams;
import com.jzhangdeveloper.newsapi.params.TopHeadlinesParams;
import com.jzhangdeveloper.newsapi.net.NewsAPI;
import org.apache.http.client.utils.DateUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class News_WOD {
    static NewsAPIClient client = NewsAPI.newClientBuilder().setApiKey("7760cd05eb014505b291331d1498d6d1").build();
    public static String definition = "";
    public static String todayDate = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
    public static String tenDaysAgo = DateUtils.formatDate(new Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000), "yyyy-MM-dd");
    static Map<String,String> wordOfTheDayParams = EverythingParams.newBuilder().setPageSize(1).setLanguage("en").setSearchQuery("english").setFrom(tenDaysAgo).setTo(todayDate).setDomains("merriam-webster.com").build();
    static Map<String, String> everythingParams = EverythingParams.newBuilder().setPageSize(100).setLanguage("en").setSearchQuery("english").build();
    public static List<Article> articles = new ArrayList<>();
    public static List<Article> wordOfTheDayArticles = new ArrayList<>();
    public static void getNews() throws IOException, InterruptedException {
        NewsAPIResponse response = client.getEverything(everythingParams);
        Everything topHeadlines = response.getBody();
        articles = topHeadlines.getArticles();
    }
    public static void getWordOfTheDay() throws IOException, InterruptedException {
        NewsAPIResponse response = client.getEverything(wordOfTheDayParams);
        Everything wordOfTheDay = response.getBody();
        wordOfTheDayArticles = wordOfTheDay.getArticles();
    }
    public static void getDefinition() throws URISyntaxException, IOException {
        String urlScript = "https://dictionaryapi.com/api/v3/references/collegiate/json/" + wordOfTheDayArticles.get(0).getTitle() +"?key=9d49cbbb-42e7-4a2d-a0f4-a22986fe8c1f";
        HttpURLConnection con = (HttpURLConnection) new URI(urlScript).toURL().openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("accept", "application/json");
        StringBuilder response = new StringBuilder();
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String data = response.toString();
        if (data.equals("[]")) {
            return;
        }
        definition = data.substring(data.indexOf("shortdef") + 11, data.indexOf("]", data.indexOf("shortdef")));
        String[] definitions = definition.split(",");
        definition = "";
        for (int i = 0; i < definitions.length; i++) {
            if (definitions[i].charAt(definitions[i].length() - 1) != '"') {
                definition += definitions[i] + ", ";
            }
            else definition += definitions[i] + "\n";
        }
    }
}