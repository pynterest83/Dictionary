package base;

import java.util.*;

public class Dictionary {
    public static ArrayList<Word> curDict;

    public static ArrayList<Word> EN_VI_Dict;

    public static ArrayList<Word> VI_EN_Dict;

    public static ArrayList<Word> learningDict = new ArrayList<Word>();

    public static LinkedHashMap<String, ArrayList<String>> symDict;

    public static LinkedHashSet<String> EN_History = new LinkedHashSet<String>();
    public static LinkedHashSet<String> VI_History = new LinkedHashSet<String>();

    public static LinkedHashSet<String> History;

    public static User user = new User();
}
