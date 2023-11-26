package controls;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutLeft;
import base.Dictionary;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.RunApplication;

import java.io.IOException;

public abstract class GeneralControls {
    @FXML
    protected AnchorPane root;
    @FXML
    protected Pane TitleBar;
    @FXML
    protected Button searchButton;
    @FXML
    protected Button GameButton;
    @FXML
    protected Button GGTranslateButton;
    @FXML
    protected Button LearningButton;
    @FXML
    protected Button ImageTranslateButton;
    @FXML
    protected Button SettingButton;
    @FXML
    protected Button CloseButton;
    @FXML
    protected Button MinimizeButton;
    @FXML
    protected Button HomeButton;
    @FXML
    protected Button HelpButton;
    protected Boolean inside = false;
    protected boolean menuOpen = false;
    @FXML
    protected Button menuBarButton;
    @FXML
    protected Pane menuBar;
    @FXML
    protected TextField user_name;
    @FXML
    protected Label Name;
    @FXML
    protected Button Avatar;
    @FXML
    protected Button Streak;
    @FXML
    protected Label streakInfo;
    public static String currentScene = "main.fxml";
    @FXML
    protected void PrepareMenu() {
        HomeButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)-> {
            if (oldValue && !newValue && menuOpen && !inside) {
                MenuBarClick();
            }
        });
        menuBar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> inside = true);
        menuBar.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> inside = false);
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
                case "setting.fxml":
                    SettingButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
                case "help.fxml":
                    HelpButton.setStyle("-fx-background-color: linear-gradient(to right, #D2C3C6 0%, #E7E7E9 100%);");
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
    protected void loadOtherScences() {
        if (Dictionary.user.username != null) Name.setText(Dictionary.user.username);

        if (Dictionary.user.gender == 0) {
            Avatar.getStyleClass().clear();
            Avatar.getStyleClass().add("woman-avatar");
        }
        else if (Dictionary.user.gender == 1) {
            Avatar.getStyleClass().clear();
            Avatar.getStyleClass().add("man-avatar");
        }
        if (Dictionary.user.streakOn) {
            Streak.getStyleClass().clear();
            Streak.getStyleClass().add("on-streak");
            streakInfo.setText("You are on a streak!");
        }
        Avatar.setVisible(true);
        Name.setVisible(true);
        Streak.setVisible(true);
    }
    @FXML
    public void onClickHomeButton() throws Exception {
        currentScene = "main.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"main.fxml");
    }
    @FXML
    public void onClickSearchButton(ActionEvent actionEvent) throws Exception {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    protected void onClickGameButton() throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void onClickGGTranslateButton() throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"ggTranslate.fxml");
    }
    @FXML
    public void onClickLearningButton() throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"learning.fxml");
    }
    @FXML
    public void onClickImageTranslateButton() throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"imageTranslate.fxml");
    }
    @FXML
    public void onClickSetting() {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"setting.fxml");
    }
    @FXML
    public void onClickHelp() {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"help.fxml");
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
                + position +"%, #D2C3C6 100%);");
    }
    @FXML
    public void onCloseClick() {
        Platform.exit();
    }
    @FXML
    public void onMinimizeClick() {
        ((Stage) TitleBar.getScene().getWindow()).setIconified(true);
    }
    public void showStreak() {
        if (streakInfo.isVisible()) streakInfo.setVisible(false);
        else if (!streakInfo.isVisible()) {
            streakInfo.setVisible(true);
        }
    }
}
