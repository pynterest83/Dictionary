package base;

import java.util.*;


public class DictionaryCommandline extends Dictionary {
    public static void showAllWords() {
        System.out.printf("%-6s%c %-15s%c %-20s%n","No", '|' ,"English", '|', "Vietnamese");
        for (int i = 0; i < curDict.size(); i++) {
            System.out.printf("%-6d%c %-15s%c %-15s%n", i + 1,'|', curDict.get(i).getWordTarget(), '|',curDict.get(i).getWordExplain());
        }
    }
    private static void view(){
        System.out.println("Welcome to my Application!");
        System.out.println("[0] Exit");
        System.out.println("[1] Add");
        System.out.println("[2] Remove");
        System.out.println("[3] Update");
        System.out.println("[4] Display");
        System.out.println("[5] Lookup");
        System.out.println("[6] Search");
        System.out.println("[7] Game");
        System.out.println("[8] Import from file");
        System.out.println("[9] Export to file");
        System.out.print("Your action: ");
    }
    public static void dictionaryAdvanced() throws Exception {
        DictionaryManager.defaultFile();
        while (true) {
            view();
            Scanner input = new Scanner(System.in);
            int action = input.nextInt();
            switch (action) {
                case 0:
                    System.out.println("Goodbye!");
                    return;
                case 1:
                    DictionaryManager.insertFromCommandline();
                    break;
                case 2:
                    DictionaryManager.deleteWord();
                    break;
                case 3:
                    DictionaryManager.modifyWord();
                    break;
                case 4:
                    DictionaryCommandline.showAllWords();
                    break;
                case 5:
                    DictionaryManager.dictionaryLookup();
                    break;
                case 6:
                    //System.out.println(DictionaryManager.dictionarySearcher());
                    break;
                case 7:
                    //DictionaryManager.dictionaryGame();
                    break;
                case 8:
                    DictionaryManager.importFromFile();
                    break;
                case 9:
                    DictionaryManager.exportToFile();
                    break;
                default:
                    System.out.println("Invalid action!");
            }
        }
    }
}
