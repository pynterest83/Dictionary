package base;

import javafx.geometry.Pos;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Wordle {
    static Random random = new Random();
    private static ArrayList<String> PossibleWords = new ArrayList<String>();
    public static void LoadWordleList() throws IOException {
        File infile = new File("src/resources/WordleList.txt");
        FileReader fileReader= new FileReader(infile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            PossibleWords.add(line);
        }
    }
    public static String GetRandomWord() {
        return PossibleWords.get(random.nextInt(PossibleWords.size())).toUpperCase();
    }
}