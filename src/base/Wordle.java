package base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Wordle {
    static Random random = new Random();
    private static final ArrayList<String> PossibleWords = new ArrayList<>();
    public static void LoadWordleList() throws IOException {
        FileReader fileReader= new FileReader("src/resources/WordleList.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            PossibleWords.add(line);
        }
    }
    public static String GetRandomWord() {
        return PossibleWords.get(random.nextInt(PossibleWords.size())).toUpperCase();
    }
}