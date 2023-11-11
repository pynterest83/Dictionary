package main;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutLeft;
import base.CompleteSentence;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;

public class MainController {
    public static double SpeakVolume = 1.0;
    @FXML
    protected AnchorPane Root;
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
    protected ButtonBar TitleBar;
    protected Boolean inside = false;
    private static String currentScene = "main.fxml";
    @FXML
    private void initialize() {
        PrepareMenu();
        Root.disabledProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (oldValue && !newValue) {
                try {
                    StartGame();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
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
    public void onClickGGTranslateButton() throws IOException {
        currentScene = "ggTranslate.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"ggTranslate.fxml");
    }
    @FXML
    public void onClickLearningButton() throws IOException {
        currentScene = "learning.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"learning.fxml");
    }
    @FXML
    public void onClickImageTranslateButton() throws IOException {
        currentScene = "imageTranslate.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"imageTranslate.fxml");
    }
    @FXML
    public void onClickSetting() {
        currentScene = "setting.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"setting.fxml");
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
    private int CurrentQuestion;
    private final Random rand = new Random();
    @FXML
    private RadioButton ChoiceA;
    @FXML
    private RadioButton ChoiceB;
    @FXML
    private RadioButton ChoiceC;
    @FXML
    private RadioButton ChoiceD;
    @FXML
    private RadioButton ChoiceE;
    @FXML
    private WebView gameScreen;
    @FXML
    private Button NextButton;
    @FXML
    private Button StartGameButton;
    @FXML
    private AnchorPane QuizPane;
    @FXML
    private void StartGame() throws MalformedURLException {
        ChoiceA.setVisible(true);
        ChoiceB.setVisible(true);
        ChoiceC.setVisible(true);
        ChoiceD.setVisible(true);
        ChoiceE.setVisible(true);
        NextButton.setVisible(true);
        CurrentQuestion = rand.nextInt(500);
        String content = "<html>" + CompleteSentence.askQuestion(CurrentQuestion) + "</html>";
        gameScreen.getEngine().loadContent(content, "text/html");
    }
    @FXML
    private String CheckResult() {
        if (ChoiceA.isSelected()) return "a";
        if (ChoiceB.isSelected()) return "b";
        if (ChoiceC.isSelected()) return "c";
        if (ChoiceD.isSelected()) return "d";
        if (ChoiceE.isSelected()) return "e";
        return "";
    }
    @FXML
    private void ToggleRadioButtons(boolean toggle) {
        ChoiceA.setDisable(toggle);
        ChoiceB.setDisable(toggle);
        ChoiceC.setDisable(toggle);
        ChoiceD.setDisable(toggle);
        ChoiceE.setDisable(toggle);
    }
    @FXML
    private void ClearAllRadioButton() {
        ChoiceA.setSelected(false);
        ChoiceB.setSelected(false);
        ChoiceC.setSelected(false);
        ChoiceD.setSelected(false);
        ChoiceE.setSelected(false);
    }
    @FXML
    private void onNextButtonClick() {
        CurrentQuestion = rand.nextInt(501);
        ToggleRadioButtons(false);
        ClearAllRadioButton();
        String content = "<html>" + CompleteSentence.askQuestion(CurrentQuestion) + "</html>";
        gameScreen.getEngine().loadContent(content,"text/html");
    }
    @FXML
    private void RadioButtonClick() {
        String content = "<html>" + CompleteSentence.showAnswer(CurrentQuestion,CheckResult()) + "</html>";
        ToggleRadioButtons(true);
        gameScreen.getEngine().loadContent(content,"text/html");
    }
}
