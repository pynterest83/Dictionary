package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import base.DictionaryManager;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import javafx.scene.web.WebView;
import base.VoiceRSS;
import java.io.File;
import java.io.IOException;
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
    private String searched;
    @FXML
    private void initialize() {
        TextFields.bindAutoCompletion(searchBar, input -> {
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
    protected void onClickGameButton() throws IOException {
        Stage stage = (Stage) searchBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void onClickGGTranslateButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    public void onClickFavouriteButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    protected void onClickAdd() throws IOException {
        Stage stage = (Stage) searchBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() throws IOException {
        Stage stage = (Stage) searchBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"modify.fxml");
    }
    @FXML
    protected void onExportToFileClick(ActionEvent event) {
        DictionaryManager.exportToFile();
    }

    @FXML
    protected void onImportFromFileClick(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f != null) {
            System.out.println(f.getAbsolutePath());
            DictionaryManager.importFromFile(f.getAbsolutePath());
        }
    }
    @FXML
    protected void UserInput() throws Exception {
        suggestions = DictionaryManager.dictionarySearcher(searchBar.getText());
    }
    @FXML
    protected void enterSearch() throws Exception {
        if (!Objects.equals(DictionaryManager.dictionaryLookup(searchBar.getText()), "Word not found.")) {
            DisplayWordExplain();
            SpeakButton.requestFocus();
            searched = searchBar.getText();
        }
        else {
            SpeakButton.setVisible(false);
            wordExplain.getEngine().loadContent("Word not found.", "text/html");
        }
    }
    @FXML
    private void DisplayWordExplain() throws Exception {
        String explain = DictionaryManager.dictionaryLookup(searchBar.getText());
        wordExplain.getEngine().loadContent(explain, "text/html");
        SpeakButton.setVisible(true);
    }
    @FXML
    private void onClickSpeakButton() throws Exception {
        VoiceRSS.speak(searched);
    }
}
