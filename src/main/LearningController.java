package main;

import base.DictionaryManager;
import base.TranslateAPI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LearningController extends MainController {
    @FXML
    private ListView<String> learningList;
    @FXML
    private TextField searchLearningWord;
    @FXML
    private WebView learningExplain;
    @FXML
    private HTMLEditor editLearningWord;
    @FXML
    private Button saveLearningWord;
    private String suggestions[];
    private String currentWord;
    @FXML
    private void initialize() {
        PrepareMenu();
        for (int i = 0; i < DictionaryManager.learningDict.size(); i++) {
            learningList.getItems().add(DictionaryManager.learningDict.get(i).getWordTarget());
        }
        TextFields.bindAutoCompletion(searchLearningWord, input -> {
            if (searchLearningWord.getText().length() <= 1) return Collections.emptyList();
            return Stream.of(suggestions)
                    .filter(s -> s.contains(searchLearningWord.getText()))
                    .collect(Collectors.toList());
        });
        searchLearningWord.textProperty().addListener((obs, oldText, newText) -> {
            try {
                UserInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    private void UserInput() throws Exception {
        suggestions = DictionaryManager.learningWordSearcher(searchLearningWord.getText());
    }
    @FXML
    private void DisplayWordExplain(String word_target) throws Exception {
        String explain = DictionaryManager.learningWordLookup(word_target);
        learningExplain.getEngine().loadContent(explain, "text/html");
    }
    @FXML
    private void enterSearch() throws Exception {
        HideMenuBar();
        DisplayWordExplain(searchLearningWord.getText());
        currentWord = searchLearningWord.getText();
        searchLearningWord.clear();
    }
    @FXML
    private void selectSearch() throws Exception {
        HideMenuBar();
        DisplayWordExplain(learningList.getSelectionModel().getSelectedItem());
        currentWord = learningList.getSelectionModel().getSelectedItem();
    }
    @FXML
    private void onClickSpeakButton() throws Exception {
        TranslateAPI.speakAudio(currentWord,"English");
    }
    @FXML
    private void onClickModifyButton() throws Exception {
        editLearningWord.setHtmlText(DictionaryManager.learningWordLookup(currentWord));
        editLearningWord.setVisible(true);
        saveLearningWord.setVisible(true);
    }
    @FXML
    public void saveEdited(ActionEvent actionEvent) throws Exception {
        DictionaryManager.modifyLearningWord(currentWord, editLearningWord.getHtmlText());
        editLearningWord.setVisible(false);
        saveLearningWord.setVisible(false);
        learningExplain.getEngine().loadContent(DictionaryManager.learningWordLookup(currentWord), "text/html");
    }
    @FXML
    public void onClickRemoveLearningWord(ActionEvent actionEvent) throws IOException {
        DictionaryManager.removeLearningWord(currentWord);
        learningList.getItems().remove(currentWord);
        learningExplain.getEngine().loadContent("", "text/html");
    }
}
