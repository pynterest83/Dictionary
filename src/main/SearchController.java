package main;

import base.DictionaryManager;
import base.TranslateAPI;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchController extends MainController {

    String[] suggestions;
    String[] history = DictionaryManager.History.toArray(new String[0]);
    @FXML
    private TextField searchBar;
    @FXML
    private Button SpeakButton;
    @FXML
    private Button addLearningButton;
    @FXML
    private WebView wordExplain;
    @FXML
    private VBox wordSynonyms;
    @FXML
    private AnchorPane addNote;
    @FXML
    private TextField Notes;
    @FXML
    private TextField Syms;
    @FXML
    private ToggleButton EnVi;
    @FXML
    private ScrollPane synonymPane;
    @FXML
    private ToggleButton Synonyms;
    private String searched = null;
    @FXML
    private void initialize() {
        AutoCompletionBinding<String> completion = TextFields.bindAutoCompletion(searchBar, input -> {
            if (searchBar.getText().length() <= 1) {
                String[] reversedHistory = new String[history.length];
                for (int i = 0; i < history.length; i++) {
                    reversedHistory[i] = history[history.length - i - 1];
                }
                return
                Stream.of(reversedHistory).filter(s -> s.contains(searchBar.getText())).collect(Collectors.toList());
            }
            else {
                return Stream.of(suggestions).filter(s -> s.contains(searchBar.getText())).collect(Collectors.toList());
            }
        });
        completion.setDelay(0);
        Notes.focusedProperty().addListener((ObservableValue <? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
            if (oldValue && !newValue) {
                Notes.setVisible(false);
                addNote.setVisible(false);
            }
        });
        Syms.focusedProperty().addListener((ObservableValue <? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
            if (oldValue && !newValue) {
                Syms.setVisible(false);
                addNote.setVisible(false);
            }
        });
        wordSynonyms.setSpacing(20);
        SpeakButton.setVisible(false);
        addLearningButton.setVisible(false);
        addNote.setVisible(false);
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            try {
                UserInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    protected void CreateSearchHyperlink(String word) throws Exception {
        if (!Objects.equals(DictionaryManager.dictionaryLookup(word), "Word not found.")) {
            Hyperlink wordLink = new Hyperlink(word);
            wordLink.setFont(new Font(16));
            wordLink.setOnAction(actionEvent -> {
                searchBar.setText(wordLink.getText());
                try {
                    enterSearch();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            wordSynonyms.getChildren().add(wordLink);
        }
        else {
            Label wordLabel = new Label(" "+word);
            wordLabel.setFont(new Font(16));
            wordSynonyms.getChildren().add(wordLabel);
        }
    }
    @FXML
    protected void EnViClick() {
        EnVi.setStyle("-fx-background-color: #8A2BE2; -fx-text-fill: white;");
        Synonyms.setStyle(null);
        Synonyms.getStyleClass().add("src/style/main_styles.css");
        wordExplain.setVisible(true);
        synonymPane.setVisible(false);
    }
    @FXML
    protected void SynonymsClick() throws Exception {
        wordExplain.setVisible(false);
        synonymPane.setVisible(true);
        Synonyms.setStyle("-fx-background-color: #8A2BE2; -fx-text-fill: white;");
        EnVi.setStyle(null);
        EnVi.getStyleClass().add("src/style/main_styles.css");
        if (DictionaryManager.symDict.get(searched) == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("No synonyms found");
            alert.setHeaderText(null);
            alert.setContentText("DO YOU WANT TO ADD THIS WORD TO SYNONYMS LIST?");
            alert.showAndWait();
            if (alert.getResult().getText().equals("OK")) {
                addNote.setVisible(true);
                Syms.setVisible(true);
                Notes.setVisible(false);
                Syms.requestFocus();
            }
            return;
        }
        if (wordSynonyms.getChildren().isEmpty()) {
            ArrayList<String> synonyms = DictionaryManager.symDict.get(searched);
            for (String s : synonyms) {
                CreateSearchHyperlink(s);
            }
        }
    }
    @FXML
    protected void UserInput() {
        suggestions = DictionaryManager.dictionarySearcher(searchBar.getText());
    }
    @FXML
    protected void enterSearch() throws Exception {
        EnViClick();
        wordSynonyms.getChildren().clear();
        addNote.setVisible(false);
        synonymPane.setVisible(false);
        wordExplain.setVisible(true);
        if (!Objects.equals(DictionaryManager.dictionaryLookup(searchBar.getText()), "Word not found.")) {
            DisplayWordExplain();
            SpeakButton.requestFocus();
            searched = searchBar.getText();
            DictionaryManager.addHistory(searched);
            history = DictionaryManager.History.toArray(new String[0]);
        }
        else {
            SpeakButton.setVisible(false);
            addLearningButton.setVisible(false);
            wordExplain.getEngine().loadContent("Word not found.", "text/html");
        }
    }
    @FXML
    private void DisplayWordExplain() throws Exception {
        String explain = DictionaryManager.dictionaryLookup(searchBar.getText());
        wordExplain.getEngine().loadContent(explain, "text/html");
        SpeakButton.setVisible(true);
        addLearningButton.setVisible(true);
    }
    @FXML
    private void onClickSpeakButton() throws Exception {
        TranslateAPI.speakAudio(searched,"English");
    }
    @FXML
    public void onClickAddLearning(ActionEvent actionEvent) {
        addNote.setVisible(true);
        Syms.setVisible(false);
        Notes.setVisible(true);
        Notes.requestFocus();
    }
    @FXML
    public void addDescription(ActionEvent actionEvent) throws IOException {
        DictionaryManager.addLearning(searched, Notes.getText());
        RunApplication.Reload("learning.fxml");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Added");
        alert.setHeaderText(null);
        alert.setContentText("Added to learning list");
        alert.showAndWait();
        addNote.setVisible(false);
    }

    public void addSyms(ActionEvent actionEvent) throws Exception {
        String[] sym = Syms.getText().split(",");
        ArrayList<String> synonyms = new ArrayList<>(Arrays.asList(sym));
        DictionaryManager.addSynonyms(searched, synonyms);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Added");
        alert.setHeaderText(null);
        alert.setContentText("Added to synonyms list");
        alert.showAndWait();
        Syms.setText("");
        Syms.setVisible(false);
        addNote.setVisible(false);
        SynonymsClick();
    }
}
