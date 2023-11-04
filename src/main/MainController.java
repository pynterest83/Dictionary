package main;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutLeft;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    protected Button menuBarButton;
    @FXML
    protected Pane menuBar;
    protected boolean menuOpen = false;
    @FXML
    protected Button searchButton;
    @FXML
    protected Button GameButton;
    @FXML
    protected Button GGTranslateButton;
    @FXML
    protected Button LearningButton;
    @FXML
    protected Button CloseButton;
    @FXML
    protected Button MinimizeButton;
    @FXML
    protected Button HomeButton;
    @FXML
    protected ButtonBar TitleBar;
    protected Boolean inside = false;
    private static String currentScene = "main.fxml";
    @FXML
    private void initialize() {
        PrepareMenu();
    }
    @FXML
    protected void PrepareMenu() {
        HomeButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)-> {
            if (oldValue && !newValue && menuOpen && !inside) {
                MenuBarClick();
            }
        });
        menuBar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            inside = true;
        });
        menuBar.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            inside = false;
        });
    }
    @FXML
    protected void MenuExited(MouseEvent mouseEvent) {
        Button source = (Button) mouseEvent.getSource();
        source.setStyle(null);
    }
    @FXML
    protected void MenuMoved(MouseEvent mouseEvent) {
        Button source = (Button) mouseEvent.getSource();
        double position = mouseEvent.getX()/source.getWidth() * 100;
        source.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 "
                + Double.toString(position) +"%, #D2C3C6 100%);");
    }
    @FXML
    public void onCloseClick() {
        Platform.exit();
    }
    @FXML
    public void onMinimizeClick() {
        ((Stage) TitleBar.getScene().getWindow()).setIconified(true);
    }
    @FXML
    public void onClickHomeButton(ActionEvent actionEvent) throws Exception {
        currentScene = "main.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"main.fxml");
    }
    @FXML
    public void onClickSearchButton(ActionEvent actionEvent) throws Exception {
        currentScene = "search.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    protected void onClickGameButton() throws IOException {
        currentScene = "game.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void onClickGGTranslateButton(ActionEvent actionEvent) throws IOException {
        currentScene = "ggTranslate.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"ggTranslate.fxml");
    }
    @FXML
    public void onClickLearningButton(ActionEvent actionEvent) throws IOException {
        currentScene = "learning.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"learning.fxml");
    }
    @FXML
    protected void MenuBarClick() {
        inside = false;
        menuOpen = !menuOpen;
        if (menuOpen) {
            menuBar.setVisible(true);
            menuBar.setDisable(true);
            SlideInLeft inLeft = new SlideInLeft(menuBar);
            inLeft.setOnFinished(e -> {
                menuBar.setDisable(false);
                HomeButton.requestFocus();
            });
            inLeft.setSpeed(3);
            inLeft.play();
            switch (currentScene) {
                case "main.fxml":
                    HomeButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
                    break;
                case "search.fxml":
                    searchButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
                    break;
                case "game.fxml":
                    GameButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
                    break;
                case "ggTranslate.fxml":
                    GGTranslateButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
                    break;
                case "learning.fxml":
                    LearningButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
                    break;
            }
        }
        else {
            menuBar.setDisable(true);
            SlideOutLeft outLeft= new SlideOutLeft(menuBar);
            outLeft.setOnFinished(e -> menuBar.setVisible(menuOpen));
            outLeft.setSpeed(3);
            outLeft.play();
        }
    }
    @FXML
    protected void HideMenuBar() {
        if (menuOpen) MenuBarClick();
    }
    @FXML
    protected void MouseClick() {

    }
}
