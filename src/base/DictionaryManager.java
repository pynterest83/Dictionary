package base;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.SearchController;

import java.lang.reflect.Array;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class DictionaryManager extends Dictionary {
    private static final String PATH = "src/resources/dictionaries.txt";
    public static void insertFromCommandline() {
        String word_target, word_explain;
        System.out.print("Enter number of words to add: ");
        Scanner intInput = new Scanner(System.in);
        Scanner strInput = new Scanner(System.in);
        int nums = intInput.nextInt();
        while (nums-- > 0) {
            System.out.print("Enter word: ");
            word_target = strInput.nextLine();
            System.out.print("Enter definition of " + word_target + ":");
            word_explain = strInput.nextLine();
            Word newWord = new Word(word_target, word_explain);
            if (curDict.contains(newWord)) continue;
            curDict.add(newWord);
        }
        System.out.println("Added " + nums + " words.");
        Collections.sort(curDict);
    }


    public static void defaultFile() {
        try {
            File infile = new File(PATH);
            FileReader fileReader= new FileReader(infile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("   ");
                Word newWord = new Word(words[0], words[1]);
                curDict.add(newWord);
            }
            Collections.sort(curDict);
            System.out.println("Data Loaded" + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Word> importFromFile(String path){
        ArrayList<Word> repeated = new ArrayList<Word>();
        try {
            File infile = new File(path);
            FileReader fileReader= new FileReader(infile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("   ");
                Word newWord = new Word(words[0], words[1]);
                if (DictionaryManager.dictionaryLookup(words[0]) != "Word not found.") {
                    repeated.add(newWord);
                    continue;
                }
                curDict.add(newWord);
            }
            System.out.println("Data Loaded" + "\n");
            Collections.sort(curDict);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return repeated;
    }

    public static void exportToFile() {
        try {
            File outfile = new File(PATH);
            FileWriter fileWriter = new FileWriter(outfile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < curDict.size(); i++) {
                bufferedWriter.write(curDict.get(i).getWordTarget() + "   " + curDict.get(i).getWordExplain() + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            System.out.println("Data Exported Successfully\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteWord() {
        Scanner strInput = new Scanner(System.in);
        System.out.println("Enter word you want to delete: ");
        String del = strInput.nextLine();
        Word tmp = new Word();
        tmp.setWordTarget(del);
        int position = Collections.binarySearch(curDict,tmp);
        if (position < 0) {
            System.out.println("Word not found.");
            return;
        }
        curDict.remove(position);
        System.out.println("Word " + del + " deleted.");
    }

    public static boolean addWord(String word_target, String word_explain) throws Exception {
        Word newWord = new Word(word_target, word_explain);
        if (DictionaryManager.dictionaryLookup(word_target) != "Word not found.") {
            return false;
        }
        else {
            curDict.add(newWord);
            Collections.sort(curDict);
        }
        return true;
    }

    public static void modifyWord() {
        Scanner strInput = new Scanner(System.in);
        System.out.println("Enter word you want to modify: ");
        String search = strInput.nextLine();
        Word mod = new Word();
        mod.setWordTarget(search);
        int position = Collections.binarySearch(curDict, mod);
        if (position < 0) {
            System.out.println("Word not found.");
            return;
        }
        String word_target, word_explain;
        System.out.println("Enter new word: ");
        word_target = strInput.nextLine();
        System.out.println("Enter new definition: ");
        word_explain = strInput.nextLine();
        mod.setWordTarget(word_target);
        mod.setWordExplain(word_explain);
        curDict.set(position, mod);
        System.out.println("Successfully modified.");
        Collections.sort(curDict);
    }
    public static String dictionaryLookup(String search) throws Exception {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(curDict,dummy);
        if (position < 0) return "Word not found.";
        else {
            return curDict.get(position).getWordExplain();
        }
    }
    public static String[] dictionarySearcher(String search) {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(curDict,dummy);
        if (position<0) position = -(position+1);
        ArrayList<String> suggestions = new ArrayList<String>();
        while (curDict.get(position).getWordTarget().toLowerCase().indexOf(search.toLowerCase())==0) {
            suggestions.add(curDict.get(position).getWordTarget());
            position++;
            if (position>=curDict.size()) break;
        }
        String[] suggest_array = new String[suggestions.size()];
        suggest_array = suggestions.toArray(suggest_array);
        return suggest_array;
    }

    public static int binSearch(String search) {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        return Collections.binarySearch(curDict, dummy);
    }

}