package controls;

import base.CompleteSentence;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import java.util.Random;

public class CompleteSentences implements CompleteSentenceControls {
    Random rand = new Random();
    public static int CurrentQuestion = 0;
    RadioButton ChoiceA;
    RadioButton ChoiceB;
    RadioButton ChoiceC;
    RadioButton ChoiceD;
    RadioButton ChoiceE;
    WebView gameScreen;
    Button NextButton;
    AnchorPane QuizPane;

    public CompleteSentences(RadioButton choiceA, RadioButton choiceB, RadioButton choiceC, RadioButton choiceD, RadioButton choiceE, WebView gameScreen, Button nextButton, AnchorPane quizPane) {
        ChoiceA = choiceA;
        ChoiceB = choiceB;
        ChoiceC = choiceC;
        ChoiceD = choiceD;
        ChoiceE = choiceE;
        this.gameScreen = gameScreen;
        NextButton = nextButton;
        QuizPane = quizPane;
    }

    public void StartGame() {
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
    public String CheckResult() {
        if (ChoiceA.isSelected()) return "a";
        if (ChoiceB.isSelected()) return "b";
        if (ChoiceC.isSelected()) return "c";
        if (ChoiceD.isSelected()) return "d";
        if (ChoiceE.isSelected()) return "e";
        return "";
    }
    public void ToggleRadioButtons(boolean toggle) {
        ChoiceA.setDisable(toggle);
        ChoiceB.setDisable(toggle);
        ChoiceC.setDisable(toggle);
        ChoiceD.setDisable(toggle);
        ChoiceE.setDisable(toggle);
    }
    public void ClearAllRadioButton() {
        ChoiceA.setSelected(false);
        ChoiceB.setSelected(false);
        ChoiceC.setSelected(false);
        ChoiceD.setSelected(false);
        ChoiceE.setSelected(false);
    }
    public void onNextButtonClick() {
        CurrentQuestion = rand.nextInt(500);
        ToggleRadioButtons(false);
        ClearAllRadioButton();
        String content = "<html>" + CompleteSentence.askQuestion(CurrentQuestion) + "</html>";
        gameScreen.getEngine().loadContent(content,"text/html");
    }
    public void RadioButtonClick() {
        String content = "<html>" + CompleteSentence.showAnswer(CurrentQuestion,CheckResult()) + "</html>";
        ToggleRadioButtons(true);
        gameScreen.getEngine().loadContent(content,"text/html");
    }
}
