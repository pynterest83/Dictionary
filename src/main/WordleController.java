package main;

import base.DictionaryManager;
import base.Word;
import base.Wordle;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WordleController extends MainController {
    static int CurrentAttempt;
    static int CurrentLetter;
    private static String lastKey;
    private static String lastCharacter = "";
    private static String Answer;
    private static boolean Win;
    @FXML
    private Pane WordlePane;
    @FXML
    private Button ReplayButton;
    @FXML
    private Button StartButton;
    @FXML
    private TextField AnnounceBoard;
    @FXML
    private TextField AnnounceBoard2;
    @FXML
    private TextField ErrorBoard;
    @FXML
    protected void initialize() {
        InitializeHBoxes();
    }
    @FXML
    private void InitializeHBoxes() {
        for (Node hboxes : WordlePane.getChildren()) {
            HBox current = (HBox) hboxes;
            for (Node text : current.getChildren()) {
                ((TextField) text).setEditable(false);
                ((TextField) text).setTextFormatter(new TextFormatter<String>((TextFormatter.Change change) -> {
                    String newText = change.getControlNewText();
                    if (newText.length() > 1 || !change.getText().chars().allMatch(Character::isLetter)) {
                        return null;
                    }
                    else {
                        change.setText(change.getText().toUpperCase());
                        return change;
                    }
                }));
            }
        }
    }
    @FXML
    protected void onClickStart() {
        WordlePane.setVisible(true);
        StartButton.setVisible(false);
        Start();
    }
    @FXML
    protected void ResetTiles() {
        for (Node hboxes : WordlePane.getChildren()) {
            HBox current = (HBox) hboxes;
            for (Node text : current.getChildren()) {
                ((TextField) text).setEditable(false);
                ((TextField) text).setText("");
                text.setStyle(null);
            }
        }
        AnnounceBoard.setText("");
        AnnounceBoard2.setText("");
    }
    @FXML
    protected void Start() {
        CurrentAttempt = 0;
        CurrentLetter = 0;
        HBox first = (HBox) WordlePane.getChildren().get(0);
        TextField firstField = (TextField) first.getChildren().get(0);
        firstField.setEditable(true);
        firstField.requestFocus();
        Win = false;
        Answer = Wordle.GetRandomWord();
    }
    @FXML
    protected void onClickReplay() {
        ReplayButton.setVisible(false);
        ResetTiles();
        Start();
    }
    @FXML
    protected void onPress(KeyEvent event) {
        lastKey = event.getCode().toString();
    }
    @FXML
    protected void onRelease() {
        HBox CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
        if (CurrentLetter == 4) {
            lastCharacter = ((TextField) CurrentHbox.getChildren().get(CurrentLetter)).getText();
        }
    }
    @FXML
    protected void onType() {
        HBox CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
        TextField CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
        if (Objects.equals(lastKey, "SPACE")) return;
        if (Objects.equals(lastKey, "BACK_SPACE")) {
            if (CurrentLetter>0) {
                if (CurrentLetter == 4 && !Objects.equals(lastCharacter, "")) {
                    CurrentField.setText("");
                    lastCharacter = "";
                }
                else {
                    CurrentField.setEditable(false);
                    CurrentLetter--;
                    CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
                    CurrentField.setText("");
                    CurrentField.setEditable(true);
                    CurrentField.requestFocus();
                }
            }
        }
        else if (CurrentLetter < 4 && !Objects.equals(lastKey, "ENTER")) {
            CurrentField.setEditable(false);
            CurrentLetter++;
            CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
            CurrentField.setEditable(true);
            CurrentField.requestFocus();
        }
    }
    @FXML
    protected void onSubmission() throws Exception {
        HBox CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
        TextField CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
        CurrentField.setEditable(false);
        StringBuilder s = new StringBuilder();
        for (Node text : CurrentHbox.getChildren()) {
            s.append(((TextField) text).getText());
        }
        if (Objects.equals(CurrentField.getText(), "")) return;
        if (Objects.equals(DictionaryManager.dictionaryLookup(s.toString(), "EN_VI"), "Word not found.")) {
            ErrorBoard.setText("Please enter a valid word.");
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> ErrorBoard.setText(""));
            pause.play();
            new animatefx.animation.Shake(CurrentHbox).play();
            return;
        }
        String[] styles = CheckSubmission(s.toString());
        CheckAnimation(CurrentHbox,styles);
        if (CurrentAttempt<5 && !Win) {
            CurrentAttempt++;
            CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
            CurrentLetter = 0;
            CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
            CurrentField.setEditable(true);
            CurrentField.requestFocus();
        }
    }
    protected String[] CheckSubmission(String guess) {
        String answer = Answer;
        String[] styles = new String[5];
        if (Objects.equals(answer, guess)) {
            for (int i = 0; i < 5; i++) {
                styles[i] = "-fx-control-inner-background: Green;";
            }
            EndGame(true);
            return styles;
        }
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                styles[i] = "-fx-control-inner-background: Green;";
                answer = answer.substring(0, i) + "#" + answer.substring(i + 1);
                guess = guess.substring(0,i) + "@" + guess.substring(i+1);
            }
        }
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == '@') continue;
            boolean check = false;
            for (int j = 0; j < 5; j++) {
                if (guess.charAt(i) == answer.charAt(j)) {
                    styles[i] = "-fx-control-inner-background: Orange;";
                    answer = answer.substring(0, j) + "#" + answer.substring(j + 1);
                    guess = guess.substring(0,i) + "@" + guess.substring(i+1);
                    check = true;
                    break;
                }
            }
            if (!check) styles[i] = "-fx-control-inner-background: DarkGrey;";
        }
        if (CurrentAttempt == 5) {
            EndGame(false);
        }
        return styles;
    }
    @FXML
    protected void CheckAnimation(HBox hbox, String[] styles) {
        ArrayList<ScaleTransition> transitions = new ArrayList<>();
        for (int i = 0;i < 5;i++) {
            ScaleTransition transition = new ScaleTransition();
            TextField current = (TextField) hbox.getChildren().get(i);
            transition.setToY(0);
            transition.setNode(current);
            transition.setRate(1.9);
            int finalI = i;
            transition.setOnFinished(e-> current.setStyle(styles[finalI]));
            transitions.add(transition);
            ScaleTransition reverseTransition = new ScaleTransition();
            reverseTransition.setToY(1);
            reverseTransition.setNode(current);
            reverseTransition.setRate(1.9);
            transitions.add(reverseTransition);
        }
        SequentialTransition animation = new SequentialTransition(transitions.get(0), transitions.get(1), transitions.get(2), transitions.get(3), transitions.get(4),transitions.get(5),transitions.get(6),transitions.get(7),transitions.get(8),transitions.get(9));
        animation.play();
    }
    @FXML
    protected void EndGame(boolean win) {
        if (win) {
            Win = true;
            for (int i = CurrentAttempt +1; i<6; i++) {
                HBox hbox = (HBox) WordlePane.getChildren().get(i);
                for (int j = 0;j < 5;j++) {
                    TextField text = (TextField) hbox.getChildren().get(j);
                    text.setEditable(false);
                }
            }
        }
        PauseTransition replayHalt = new PauseTransition(Duration.seconds(2.5));
        replayHalt.setOnFinished(e -> {
            ReplayButton.setVisible(true);
            AnnounceBoard.setText("You " + (win ? "won":"lost."));
            if (win) AnnounceBoard2.setText("Number of guess(es): " + (CurrentAttempt+1));
            else AnnounceBoard2.setText("The answer is: "+ Answer.charAt(0)+Answer.substring(1).toLowerCase());
        });
        replayHalt.play();
    }
}
