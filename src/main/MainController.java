package main;

import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutLeft;
import base.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.HyperlinkLabel;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.Timer;

public class MainController {
    private int curNews = 0;
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
    protected Button HelpButton;
    @FXML
    protected Pane TitleBar;
    @FXML
    protected AnchorPane login;
    @FXML
    protected TextField user_name;
    @FXML
    protected Label Name;
    @FXML
    protected Button Avatar;
    @FXML
    private AnchorPane News_Pane;
    @FXML
    private AnchorPane WOD_Pane;
    @FXML
    private ImageView IMG1;
    @FXML
    private ImageView IMG2;
    @FXML
    private Label quiz;
    @FXML
    private ImageView IMG3;
    protected Boolean inside = false;
    private static String currentScene = "main.fxml";

    @FXML
    private void initialize() {
        try {
            News_WOD.getNews();
            News_WOD.getWordOfTheDay();
            News_WOD.getDefinition();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        PrepareMenu();
        Root.disabledProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (oldValue && !newValue) {
                try {
                    first_login();
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
    public void onClickHelp() {
        currentScene = "help.fxml";
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"help.fxml");
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

    @FXML
    protected void first_login() {
        if (Dictionary.user.username == null) {
            login.setVisible(true);
            menuBarButton.setVisible(false);
        }
        else {
            Name.setText(Dictionary.user.username);
            Name.setVisible(true);
            if (Dictionary.user.gender == 0) {
                Avatar.getStyleClass().clear();
                Avatar.getStyleClass().add("woman-avatar");
            }
            else {
                Avatar.getStyleClass().clear();
                Avatar.getStyleClass().add("man-avatar");
            }
            Avatar.setVisible(true);
            viewAll();
        }
    }

    @FXML
    private RadioButton Female;
    @FXML
    private RadioButton Male;
    public void login(MouseEvent mouseEvent) throws IOException {
        login.setVisible(false);
        if (user_name.getText().equals("")) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Username is required");
            a.showAndWait();
        }
        if (!Female.isSelected() && !Male.isSelected()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Gender is required");
            a.showAndWait();
        }
        if (Female.isSelected()) {
            Dictionary.user.gender = 0;
            Avatar.getStyleClass().clear();
            Avatar.getStyleClass().add("woman-avatar");
        }
        if (Male.isSelected()) {
            Dictionary.user.gender = 1;
            Avatar.getStyleClass().clear();
            Avatar.getStyleClass().add("man-avatar");
        }
        Dictionary.user.username = user_name.getText();
        Dictionary.user.streak = 0;

        Name.setText(Dictionary.user.username);
        Name.setVisible(true);
        Avatar.setVisible(true);

        menuBarButton.setVisible(true);
        viewAll();
        RunApplication.LoadScenes();
        DictionaryManager.updateUser();
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
        Avatar.setVisible(true);
        Name.setVisible(true);
    }

    public void cancle_login(MouseEvent mouseEvent) {
        login.setVisible(false);
        Avatar.setVisible(true);
        Name.setVisible(true);
        menuBarButton.setVisible(true);
        viewAll();
    }

    @FXML
    private ImageView news_image;
    @FXML
    private Label Title;
    @FXML
    private Label Description;
    @FXML
    private Label Content;
    @FXML
    private void setUpNews() {
        news_image.setImage(new Image(News_WOD.articles.get(curNews).getUrlToImage()));
        Rectangle clip = new Rectangle();
        clip.setWidth(400.0f);
        clip.setHeight(400.0f);
        clip.setArcHeight(20);
        clip.setArcWidth(20);
        clip.setStroke(Color.BLACK);
        news_image.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = news_image.snapshot(parameters, null);
        news_image.setClip(null);
        news_image.setEffect(new DropShadow(10, Color.RED));
        news_image.setImage(image);

        Title.setText(News_WOD.articles.get(curNews).getTitle());
        Title.setWrapText(true);
        Description.setText(News_WOD.articles.get(curNews).getDescription());
        Description.setWrapText(true);
        String content = News_WOD.articles.get(curNews).getContent();
        int idx = content.indexOf("[");
        if (idx != -1) content = content.substring(0,idx);
        Content.setText(content);
        Content.setWrapText(true);
    }

    @FXML
    private void onClickNextNews() {
        curNews++;
        if (curNews == News_WOD.articles.size()) curNews = 0;
        setUpNews();
    }

    @FXML
    private Line line1;
    @FXML
    private Line line2;
    @FXML
    private void viewAll() {
        QuizPane.setVisible(true);
        News_Pane.setVisible(true);
        WOD_Pane.setVisible(true);
        IMG1.setVisible(true);
        IMG2.setVisible(true);
        IMG3.setVisible(true);
        quiz.setVisible(true);
        line1.setVisible(true);
        line2.setVisible(true);
        setUpNews();
        setUpWOD();
    }

    @FXML
    private void open_link() {
        try {
            Desktop.getDesktop().browse(URI.create(News_WOD.articles.get(curNews).getUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Label Word;
    @FXML
    private Label Date;
    @FXML
    private Label Definition;
    @FXML
    private Label Pronunciation;
    @FXML
    private Label WordType;
    @FXML
    private void setUpWOD() {
        Word.setText(News_WOD.wordOfTheDayArticles.get(0).getTitle());
        Word.setWrapText(true);
        Date.setText(News_WOD.todayDate);
        Date.setWrapText(true);
        String description = News_WOD.wordOfTheDayArticles.get(0).getDescription();
        String pronunciation = description.substring(description.indexOf("\\"), description.lastIndexOf("\\") + 1);
        Pronunciation.setText(pronunciation);
        Pronunciation.setWrapText(true);
        String wordType = description.substring(description.lastIndexOf("\\") + 5, description.indexOf("\n", description.lastIndexOf("\\")));
        WordType.setText(wordType);
        WordType.setWrapText(true);
        Definition.setText(News_WOD.definition);
        Definition.setWrapText(true);
    }
    @FXML
    private void open_link1() {
        try {
            Desktop.getDesktop().browse(URI.create(News_WOD.wordOfTheDayArticles.get(0).getUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onClickSpeakButton() {
        TranslateAPI.speakAudio(News_WOD.wordOfTheDayArticles.get(0).getTitle(),"English");
    }
}
