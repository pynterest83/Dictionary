package main;

import base.DictionaryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class GameController {
    @FXML
    private Button CompleteSentence;
    @FXML
    private Button Wordle;
    @FXML
    public void onClickSearchButton(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) Wordle.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    public void onClickGGTranslateButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) Wordle.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"ggTranslate.fxml");
    }
    @FXML
    public void onClickFavouriteButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    protected void onClickAdd() throws IOException {
        Stage stage = (Stage) Wordle.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() throws IOException {
        Stage stage = (Stage) Wordle.getScene().getWindow();
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
    public void onClickCompleteSentence(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) CompleteSentence.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"completeSentenceGame.fxml");
    }

    @FXML
    public void onClickWordle(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) Wordle.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"wordle.fxml");
    }

}
