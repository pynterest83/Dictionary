package main;

import base.DictionaryManager;
import base.TranslateAPI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchController extends MainController {

    String[] suggestions;
    @FXML
    private TextField searchBar;
    @FXML
    private Button SpeakButton;
    @FXML
    private Button addLearningButton;
    @FXML
    private WebView wordExplain;
    @FXML
    private WebView wordSynonyms;
    @FXML
    private AnchorPane addNote;
    @FXML
    private TextField Notes;
    @FXML
    private TextField Syms;
    @FXML
    private ToggleButton EnVi;
    @FXML
    private ToggleButton Synonyms;
    private String searched;
    @FXML
    private void initialize() throws IOException {
        TextFields.bindAutoCompletion(searchBar, input -> {
            if (searchBar.getText().length() <= 1) return Collections.emptyList();
            return Stream.of(suggestions)
                    .filter(s -> s.contains(searchBar.getText()))
                    .collect(Collectors.toList());
        });
        SpeakButton.setVisible(false);
        addLearningButton.setVisible(false);
        addNote.setVisible(false);
        wordSynonyms.setVisible(false);
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            try {
                UserInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    protected void EnViClick() throws Exception {
        EnVi.setStyle("-fx-background-color: #8A2BE2; -fx-text-fill: white;");
        Synonyms.setStyle(null);
        Synonyms.getStyleClass().add("src/style/main_styles.css");
        wordExplain.setVisible(true);
        wordSynonyms.setVisible(false);
    }
    @FXML
    protected void SynonymsClick() {
        wordExplain.setVisible(false);
        wordSynonyms.setVisible(true);
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
            }
            return;
        }
        ArrayList<String> synonyms = DictionaryManager.symDict.get(searched);
        String sym = "";
        for (String s : synonyms) {
            sym += s + "<br>";
        }
        wordSynonyms.getEngine().loadContent(sym, "text/html");
    }
    @FXML
    protected void UserInput() throws Exception {
        suggestions = DictionaryManager.dictionarySearcher(searchBar.getText());
    }
    @FXML
    protected void enterSearch() throws Exception {
        EnViClick();
        addNote.setVisible(false);
        wordSynonyms.setVisible(false);
        wordExplain.setVisible(true);
        wordSynonyms.getEngine().loadContent("", "text/html");
        if (!Objects.equals(DictionaryManager.dictionaryLookup(searchBar.getText()), "Word not found.")) {
            DisplayWordExplain();
            SpeakButton.requestFocus();
            searched = searchBar.getText();
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

    public void addSyms(ActionEvent actionEvent) {
        String[] sym = Syms.getText().split(",");
        ArrayList<String> synonyms = new ArrayList<>();
        for (String s : sym) {
            synonyms.add(s);
        }
        DictionaryManager.addSynonyms(searched, synonyms);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Added");
        alert.setHeaderText(null);
        alert.setContentText("Added to synonyms list");
        alert.showAndWait();
        addNote.setVisible(false);
        SynonymsClick();
    }
}
