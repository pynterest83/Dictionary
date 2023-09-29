package main;

import base.DictionaryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class GameController extends MainController {
    @FXML
    private Button CompleteSentence;
    @FXML
    private Button Wordle;

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
