package main;

import base.Dictionary;
import base.DictionaryManager;
import base.News_WOD;
import controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.nio.file.Paths;

public class MainController extends GeneralControls implements CompleteSentenceControls, NewsControls {
    public static double SpeakVolume = 1.0;
    @FXML
    protected AnchorPane login;
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
    @FXML
    RadioButton ChoiceA;
    @FXML
    RadioButton ChoiceB;
    @FXML
    RadioButton ChoiceC;
    @FXML
    RadioButton ChoiceD;
    @FXML
    RadioButton ChoiceE;
    @FXML
    WebView gameScreen;
    @FXML
    Button NextButton;
    @FXML
    AnchorPane QuizPane;
    @FXML
    private RadioButton Female;
    @FXML
    private RadioButton Male;
    @FXML
    private ImageView news_image;
    @FXML
    private Label Title;
    @FXML
    private Label Description;
    @FXML
    private Label Content;
    @FXML
    private Line line1;
    @FXML
    private Line line2;
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
    private void initialize() {
        try {
            News_WOD.getNews();
            News_WOD.getWordOfTheDay();
            News_WOD.getDefinition();
        }
        catch (Exception ignored) {}
        PrepareMenu();
        root.disabledProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                    first_login();
                    StartGame();
            }
        });
        String path = Paths.get("src/style/webviews.css").toUri().toString();
        gameScreen.getEngine().setUserStyleSheetLocation(path);
        completeSentences = new CompleteSentences(ChoiceA,ChoiceB,ChoiceC,ChoiceD,ChoiceE,gameScreen,NextButton,QuizPane);
        newsControls = new News(news_image,Title,Description,Content,line1,line2,Word,Date,Definition,Pronunciation,WordType);
    }
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
    private void first_login() {
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
            if (Dictionary.user.streakOn) {
                // get date from streak
                int year = Integer.parseInt(Dictionary.user.streak.substring(0,4));
                int month = Integer.parseInt(Dictionary.user.streak.substring(5,7));
                int day = Integer.parseInt(Dictionary.user.streak.substring(8,10));
                // get current date from date object
                java.time.LocalDate currentDate = java.time.LocalDate.now();
                int currentYear = currentDate.getYear();
                int currentMonth = currentDate.getMonthValue();
                int currentDay = currentDate.getDayOfMonth();
                // check if user is on a streak
                if (year == currentYear && month == currentMonth && (day == currentDay || day == currentDay - 1)) {
                    Streak.getStyleClass().clear();
                    Streak.getStyleClass().add("on-streak");
                    streakInfo.setText("You are on a streak!");
                }
                else {
                    Dictionary.user.streakOn = false;
                    streakInfo.setText("You are not on a streak!");
                }
            }
            Streak.setVisible(true);
            viewAll();
        }
    }
    @FXML
    private void login(MouseEvent mouseEvent) throws IOException {
        login.setVisible(false);
        if (user_name.getText().isEmpty()) {
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
        Dictionary.user.streak = java.time.LocalDate.now().toString();
        Dictionary.user.streakOn = true;

        Name.setText(Dictionary.user.username);
        Name.setVisible(true);
        Avatar.setVisible(true);
        Streak.setVisible(true);

        menuBarButton.setVisible(true);
        viewAll();
        RunApplication.LoadScenes();
        DictionaryManager.updateUser();
    }
    @FXML
    private void cancel_login(MouseEvent mouseEvent) {
        login.setVisible(false);
        Avatar.setVisible(true);
        Name.setVisible(true);
        Streak.setVisible(true);
        menuBarButton.setVisible(true);
        viewAll();
    }
    CompleteSentences completeSentences;
    @Override
    public void StartGame() {
        completeSentences.StartGame();
    }
    @Override
    public String CheckResult() {
        return completeSentences.CheckResult();
    }
    @Override
    public void ToggleRadioButtons(boolean toggle) {
        completeSentences.ToggleRadioButtons(toggle);
    }
    @Override
    public void ClearAllRadioButton() {
        completeSentences.ClearAllRadioButton();
    }
    @Override
    public void onNextButtonClick() {
        completeSentences.onNextButtonClick();
    }
    @Override
    public void RadioButtonClick() {
        completeSentences.RadioButtonClick();
    }
    NewsControls newsControls;
    @Override
    public void setUpNews() {
        newsControls.setUpNews();
    }
    @Override
    public void onClickNextNews() {
        newsControls.onClickNextNews();
    }
    @Override
    public void open_link() {
        newsControls.open_link();
    }
    @Override
    public void setUpWOD() {
        newsControls.setUpWOD();
    }
    @Override
    public void open_link1() {
        newsControls.open_link1();
    }
    @Override
    public void onClickSpeakButton() {
        newsControls.onClickSpeakButton();
    }
}
