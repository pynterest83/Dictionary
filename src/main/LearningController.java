package main;

import base.DictionaryManager;
import base.TranslateAPI;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebView;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
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
    private Boolean isSaved = true;
    @FXML
    private void initialize() {
        loadOtherScences();
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
        learningExplain.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) change -> {
            Set<Node> deadSeaScrolls = learningExplain.lookupAll(".scroll-bar");
            for (Node scroll : deadSeaScrolls) {
                scroll.setVisible(false);
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
        if(explain.contains("contenteditable=\"true\"")){
            explain=explain.replace("contenteditable=\"true\"", "contenteditable=\"false\"");
        }
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
        if (isSaved) {
            HideMenuBar();
            DisplayWordExplain(learningList.getSelectionModel().getSelectedItem());
            currentWord = learningList.getSelectionModel().getSelectedItem();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("You have not saved your changes");
            alert.setContentText("Save your changes before selecting another word");
            alert.showAndWait();
        }
    }
    @FXML
    private void onClickSpeakButton() throws Exception {
        TranslateAPI.speakAudio(currentWord,"English");
    }
    @FXML
    private void onClickModifyButton() throws Exception {
        editLearningWord.setHtmlText(DictionaryManager.learningWordLookup(currentWord));
        isSaved = false;
        editLearningWord.setVisible(true);
        editLearningWord.requestFocus();
        saveLearningWord.setVisible(true);
    }
    @FXML
    public void saveEdited(ActionEvent actionEvent) throws Exception {
        DictionaryManager.modifyLearningWord(currentWord, editLearningWord.getHtmlText());
        editLearningWord.setVisible(false);
        saveLearningWord.setVisible(false);
        String edited = DictionaryManager.learningWordLookup(currentWord).replace("contenteditable=\"true\"", "contenteditable=\"false\"");
        learningExplain.getEngine().loadContent(edited, "text/html");
        learningList.requestFocus();
        isSaved = true;
    }
    @FXML
    public void onClickRemoveLearningWord(ActionEvent actionEvent) throws IOException {
        if (isSaved) {
            DictionaryManager.removeLearningWord(currentWord);
            learningList.getItems().remove(currentWord);
            learningExplain.getEngine().loadContent("", "text/html");
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("You have not saved your changes");
            alert.setContentText("Save your changes before removing this word");
            alert.showAndWait();
        }
    }
}
