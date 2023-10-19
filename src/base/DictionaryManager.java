package base;

import java.util.*;
import java.io.*;

public class DictionaryManager extends Dictionary {
    private static final String E_V_PATH = "src/resources/en-vi.txt";
    private static final String V_E_PATH = "src/resources/vi-en.txt";
    private static final String LearningPath = "src/resources/Learning.txt";
    private static final String SymPath = "src/resources/Thesaurus.txt";
    private static final String EN_HisPath = "src/resources/en_history.txt";
    private static final String VI_HisPath = "src/resources/vi_history.txt";
    private static String PATH = null;
    private static String HisPath = null;

    public static void defaultFile() {
        try {
            File infile = new File(E_V_PATH);
            FileReader fileReader= new FileReader(infile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("   ");
                Word newWord = new Word(words[0], words[1]);
                EN_VI_Dict.add(newWord);
            }
            Collections.sort(EN_VI_Dict);

            File infile2 = new File(LearningPath);
            FileReader fileReader2= new FileReader(infile2);
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
            String line2 = null;
            while ((line2 = bufferedReader2.readLine()) != null) {
                String[] learning = line2.split("   ");
                Word newWord = new Word(learning[0], learning[1]);
                learningDict.add(newWord);
            }
            Collections.sort(learningDict);

            File infile3 = new File(SymPath);
            FileReader fileReader3= new FileReader(infile3);
            BufferedReader bufferedReader3 = new BufferedReader(fileReader3);
            String line3 = null;
            while ((line3 = bufferedReader3.readLine()) != null) {
                String[] words = line3.split(",");
                ArrayList<String> synonyms = new ArrayList<String>();
                for (int i = 1; i < words.length; i++) {
                    synonyms.add(words[i]);
                }
                symDict.put(words[0], synonyms);
            }

            File infile4 = new File(EN_HisPath);
            FileReader fileReader4= new FileReader(infile4);
            BufferedReader bufferedReader4 = new BufferedReader(fileReader4);
            String line4 = null;
            while ((line4 = bufferedReader4.readLine()) != null) {
                EN_History.add(line4);
            }

            File infile5 = new File(VI_HisPath);
            FileReader fileReader5= new FileReader(infile5);
            BufferedReader bufferedReader5 = new BufferedReader(fileReader5);
            String line5 = null;
            while ((line5 = bufferedReader5.readLine()) != null) {
                VI_History.add(line5);
            }

            File infile6 = new File(V_E_PATH);
            FileReader fileReader6= new FileReader(infile6);
            BufferedReader bufferedReader6 = new BufferedReader(fileReader6);
            String line6 = null;
            while ((line6 = bufferedReader6.readLine()) != null) {
                String[] words = line6.split("   ");
                Word newWord = new Word(words[0], words[1]);
                VI_EN_Dict.add(newWord);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Word> importFromFile(String path, String dest){
        ArrayList<Word> repeated = new ArrayList<Word>();
        try {
            if (dest.equals("EN_VI")) {
                curDict = EN_VI_Dict;
            }
            else if (dest.equals("VI_EN")) {
                curDict = VI_EN_Dict;
            }
            File infile = new File(path);
            FileReader fileReader= new FileReader(infile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("   ");
                Word newWord = new Word(words[0], words[1]);
                if (!Objects.equals(DictionaryManager.dictionaryLookup(words[0], dest), "Word not found.")) {
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

    public static void exportToFile(String path) {
        try {
            if (path.equals("EN_VI")) {
                PATH = E_V_PATH;
                curDict = EN_VI_Dict;
            }
            else if (path.equals("VI_EN")) {
                PATH = V_E_PATH;
                curDict = VI_EN_Dict;
            }
            File outfile = new File(PATH);
            FileWriter fileWriter = new FileWriter(outfile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < curDict.size(); i++) {
                bufferedWriter.write(curDict.get(i).getWordTarget() + "   " + curDict.get(i).getWordExplain() + "\n");
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteWord(String word_target, String path) {
        if (path.equals("EN_VI")) {
            curDict = EN_VI_Dict;
        }
        else if (path.equals("VI_EN")) {
            curDict = VI_EN_Dict;
        }
        Word tmp = new Word();
        tmp.setWordTarget(word_target);
        int position = Collections.binarySearch(curDict,tmp);
        curDict.remove(position);
    }

    public static boolean addWord(String word_target, String word_explain, String path) throws Exception {
        Word newWord = new Word(word_target, word_explain);
        if (DictionaryManager.dictionaryLookup(word_target, path) != "Word not found.") {
            return false;
        }
        else {
            if (path.equals("EN_VI")) {
                curDict = EN_VI_Dict;
            }
            else if (path.equals("VI_EN")) {
                curDict = VI_EN_Dict;
            }
            curDict.add(newWord);
            Collections.sort(curDict);
        }
        return true;
    }

    public static void modifyWord(String word_target, String word_explain, String path) throws Exception {
        if (path.equals("EN_VI")) {
            curDict = EN_VI_Dict;
        }
        else if (path.equals("VI_EN")) {
            curDict = VI_EN_Dict;
        }
        Word mod = new Word();
        mod.setWordTarget(word_target);
        int position = Collections.binarySearch(curDict, mod);
        mod.setWordExplain(word_explain);
        curDict.set(position, mod);
        Collections.sort(curDict);
    }
    public static String dictionaryLookup(String search, String path) throws Exception {
        if (path.equals("EN_VI")) {
            curDict = EN_VI_Dict;
        }
        else if (path.equals("VI_EN")) {
            curDict = VI_EN_Dict;
        }
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(curDict,dummy);
        if (position < 0) return "Word not found.";
        else {
            return curDict.get(position).getWordExplain();
        }
    }
    public static String[] dictionarySearcher(String search, String path) {
        if (path.equals("EN_VI")) {
            curDict = EN_VI_Dict;
        }
        else if (path.equals("VI_EN")) {
            curDict = VI_EN_Dict;
        }
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


    public static void addLearning(String word, String note, String path) {
        try {
            File outfile = new File(LearningPath);
            FileWriter fileWriter = new FileWriter(outfile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            learningDict.add(new Word(word, dictionaryLookup(word, path) + "<br>Note: " + note));
            Collections.sort(learningDict);
            bufferedWriter.write(word + "   " + dictionaryLookup(word, path) + "<br>Note: " + note + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String learningWordLookup(String search) throws Exception {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(learningDict,dummy);
        if (position < 0) return "Word not found.";
        else {
            return learningDict.get(position).getWordExplain();
        }
    }
    public static String[] learningWordSearcher(String search) {
        Word dummy = new Word();
        dummy.setWordTarget(search);
        int position = Collections.binarySearch(learningDict,dummy);
        if (position<0) position = -(position+1);
        ArrayList<String> suggestions = new ArrayList<String>();
        while (learningDict.get(position).getWordTarget().toLowerCase().indexOf(search.toLowerCase())==0) {
            suggestions.add(learningDict.get(position).getWordTarget());
            position++;
            if (position>=learningDict.size()) break;
        }
        String[] suggest_array = new String[suggestions.size()];
        suggest_array = suggestions.toArray(suggest_array);
        return suggest_array;
    }
    public static void modifyLearningWord(String word_target, String word_explain) throws Exception {
        Word mod = new Word();
        mod.setWordTarget(word_target);
        int position = Collections.binarySearch(learningDict, mod);
        mod.setWordExplain(word_explain);
        learningDict.set(position, mod);
        Collections.sort(learningDict);

        File outfile = new File(LearningPath);
        FileWriter fileWriter = new FileWriter(outfile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < learningDict.size(); i++) {
            bufferedWriter.write(learningDict.get(i).getWordTarget() + "   " + learningDict.get(i).getWordExplain() + "\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }
    public static void removeLearningWord(String word_target) throws IOException {
        Word tmp = new Word();
        tmp.setWordTarget(word_target);
        int position = Collections.binarySearch(learningDict, tmp);
        learningDict.remove(position);

        File outfile = new File(LearningPath);
        FileWriter fileWriter = new FileWriter(outfile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < learningDict.size(); i++) {
            bufferedWriter.write(learningDict.get(i).getWordTarget() + "   " + learningDict.get(i).getWordExplain() + "\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public static void addSynonyms(String word, ArrayList<String> synonyms) {
        try {
            File outfile = new File(SymPath);
            FileWriter fileWriter = new FileWriter(outfile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            symDict.put(word, synonyms);
            bufferedWriter.write(word + ",");
            for (int i = 0; i < synonyms.size(); i++) {
                bufferedWriter.write(synonyms.get(i) + ",");
            }
            bufferedWriter.write("\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addHistory(String word, String path) {
        try {
            if (path.equals("EN_VI")) {
                History = EN_History;
                HisPath = EN_HisPath;
            }
            else if (path.equals("VI_EN")) {
                History = VI_History;
                HisPath = VI_HisPath;
            }
            File outfile = new File(HisPath);
            FileWriter fileWriter = new FileWriter(outfile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            if (History.add(word)) bufferedWriter.write(word + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LinkedHashSet<String> getHistory(String path) {
        if (path.equals("EN_VI")) {
            return EN_History;
        }
        return VI_History;
    }
}