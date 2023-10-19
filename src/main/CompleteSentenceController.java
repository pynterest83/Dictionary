package main;

import base.CompleteSentence;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.web.WebView;

import java.util.Random;
public class CompleteSentenceController extends MainController {
    int CurrentQuestion;
    Random rand = new Random();
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
    protected void initialize() {
    }
    @FXML
    protected void StartGame() {
        ChoiceA.setVisible(true);
        ChoiceB.setVisible(true);
        ChoiceC.setVisible(true);
        ChoiceD.setVisible(true);
        ChoiceE.setVisible(true);
        NextButton.setVisible(true);
        StartGameButton.setVisible(false);
        CurrentQuestion = rand.nextInt(502);
        String content = "<html>" + CompleteSentence.askQuestion(CurrentQuestion) + "</html>";
        gameScreen.getEngine().loadContent(content,"text/html");
    }
    @FXML
    protected String CheckResult() {
        if (ChoiceA.isSelected()) return "a";
        if (ChoiceB.isSelected()) return "b";
        if (ChoiceC.isSelected()) return "c";
        if (ChoiceD.isSelected()) return "d";
        if (ChoiceE.isSelected()) return "e";
        return "";
    }
    @FXML
    protected void ToggleRadioButtons(boolean toggle) {
        ChoiceA.setDisable(toggle);
        ChoiceB.setDisable(toggle);
        ChoiceC.setDisable(toggle);
        ChoiceD.setDisable(toggle);
        ChoiceE.setDisable(toggle);
    }
    @FXML
    protected void ClearAllRadioButton() {
        ChoiceA.setSelected(false);
        ChoiceB.setSelected(false);
        ChoiceC.setSelected(false);
        ChoiceD.setSelected(false);
        ChoiceE.setSelected(false);
    }
    @FXML
    protected void onNextButtonClick() {
        CurrentQuestion = rand.nextInt(501);
        ToggleRadioButtons(false);
        ClearAllRadioButton();
        String content = "<html>" + CompleteSentence.askQuestion(CurrentQuestion) + "</html>";
        gameScreen.getEngine().loadContent(content,"text/html");
    }
    @FXML
    protected void RadioButtonClick() {
            String content = "<html>" + CompleteSentence.showAnswer(CurrentQuestion,CheckResult()) + "</html>";
            ToggleRadioButtons(true);
            gameScreen.getEngine().loadContent(content,"text/html");
    }
}
