package base;

import java.util.*;

public class DictionaryManager extends Dictionary {
    public static void insertFromCommandline() {
        String word_target, word_explain;
        Scanner intInput = new Scanner(System.in);
        Scanner strInput = new Scanner(System.in);
        int nums = intInput.nextInt();
        while (nums-- > 0) {
            word_target = strInput.nextLine();
            word_explain = strInput.nextLine();
            Word newWord = new Word(word_target, word_explain);
            curDict.add(newWord);
        }
        Collections.sort(curDict);
    }
}
