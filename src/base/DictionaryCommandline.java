package base;

public class DictionaryCommandline extends Dictionary {
    public static void showAllWords() {
        System.out.printf("%-6s%c %-15s%c %-20s%n","No", '|' ,"English", '|', "Vietnamese");
        for (int i = 0; i < curDict.size(); i++) {
            System.out.printf("%-6d%c %-15s%c %-15s%n", i + 1,'|', curDict.get(i).getWordTarget(), '|',curDict.get(i).getWordExplain());
        }
    }

    public static void dictionaryBasic() {
        DictionaryManager.insertFromCommandline();
        DictionaryCommandline.showAllWords();
    }

    public static void main(String[] args) {
        DictionaryCommandline.dictionaryBasic();
    }
}
