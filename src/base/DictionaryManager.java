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
    public static String dictionaryLookup(String search)
    {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(curDict,dummy);
        if (position < 0) return "Word not found.\n";
        else return curDict.get(position).getWordTarget() + " | " + curDict.get(position).getWordExplain();
    }
    public static String dictionarySearcher(String search)
    {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(curDict,dummy);
        if (position<0) position = -(position+1);
        StringBuilder s = new StringBuilder();
        while (curDict.get(position).getWordTarget().toLowerCase().indexOf(search.toLowerCase())==0) {
            s.append(curDict.get(position).getWordTarget()).append(" ");
            position++;
            if (position>=curDict.size()) break;
        }
        return s.toString();
    }
}
