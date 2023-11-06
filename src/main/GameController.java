package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GameController extends MainController {
    @FXML
    private Button Wordle;
    @FXML
    private Button Babble;
    @FXML
    private void initialize() {
        PrepareMenu();
    }
    @FXML
    protected void MouseClick() {
        if (!inside) HideMenuBar();
    }
    @FXML
    public void onClickWordle(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) Wordle.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"wordle.fxml");
    }
    @FXML
    public void onClickBabble() {
        Stage stage = (Stage) Babble.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"babblebot.fxml");
    }
}
