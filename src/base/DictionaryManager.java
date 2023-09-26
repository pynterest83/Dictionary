package base;

import java.util.*;
import java.io.*;

public class DictionaryManager extends Dictionary {
    private static final String PATH = "src/resources/dictionaries.txt";
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
                if (!Objects.equals(DictionaryManager.dictionaryLookup(words[0]), "Word not found.")) {
                    repeated.add(newWord);
                    continue;
                }
                curDict.add(newWord);
            }
            Collections.sort(curDict);
            fileReader.close();
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

    public static void deleteWord(String word_target) {
        Word tmp = new Word();
        tmp.setWordTarget(word_target);
        int position = Collections.binarySearch(curDict,tmp);
        curDict.remove(position);
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

    public static void modifyWord(String word_target, String word_explain) throws Exception {
        Word mod = new Word();
        mod.setWordTarget(word_target);
        int position = Collections.binarySearch(curDict, mod);
        mod.setWordExplain(word_explain);
        curDict.set(position, mod);
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