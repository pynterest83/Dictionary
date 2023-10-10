package base;

import java.util.*;

public class Dictionary {
    public static ArrayList<Word> curDict = new ArrayList<Word>();

    public static ArrayList<Word> learningDict = new ArrayList<Word>();

    public static LinkedHashMap<String, ArrayList<String>> symDict = new LinkedHashMap<String, ArrayList<String>>();

    public static LinkedHashSet<String> History = new LinkedHashSet<String>();
}
