package main;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import base.DictionaryManager;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import javafx.scene.web.WebView;
import base.VoiceRSS;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchController {

    String[] suggestions;
    @FXML
    private TextField searchBar;
    @FXML
    private Button SpeakButton;
    @FXML
    private WebView wordExplain;
    @FXML
    private void initialize() {
        TextFields.bindAutoCompletion(searchBar, input -> {
            if (searchBar.getText().length()<=1) return Collections.emptyList();
            return Stream.of(suggestions)
                    .filter(s -> s.contains(searchBar.getText()))
                    .collect(Collectors.toList());
        });
        SpeakButton.setVisible(false);
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            try {
                UserInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    protected void SearchButton() {
        return;
    }
    @FXML
    protected void onExportToFileClick(ActionEvent event) {
        DictionaryManager.exportToFile();
    }

    @FXML
    protected void onImportFromFileClick(ActionEvent event) {
        DictionaryManager.importFromFile();
    }
    @FXML
    protected void UserInput() throws Exception {
        suggestions = DictionaryManager.dictionarySearcher(searchBar.getText());
    }
    @FXML
    protected void enterSearch() throws Exception {
        if (!searchBar.getText().isEmpty() && !Objects.equals(DictionaryManager.dictionaryLookup(searchBar.getText()), "Word not found")) {
            DisplayWordExplain();
            SpeakButton.requestFocus();
        }
    }
    @FXML
    private void DisplayWordExplain() throws Exception {
        String explain = DictionaryManager.dictionaryLookup(searchBar.getText());
        wordExplain.getEngine().loadContent(explain, "text/html");
        if (!SpeakButton.isVisible()) SpeakButton.setVisible(true);
    }
    @FXML
    private void onClickSpeakButton() throws Exception {
        VoiceRSS.speak(searchBar.getText());
    }
}
