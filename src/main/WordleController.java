package main;

import animatefx.animation.Pulse;
import base.DictionaryManager;
import base.Wordle;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

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
    private VBox VirtualKeyboard;
    private String ButtonStyle;
    @FXML
    private TextField CurrentFieldGuess;
    @FXML
    ChangeListener<Boolean> listener = (observableValue, oldValue, newValue) -> {
        if (oldValue && !newValue) {
            CurrentFieldGuess.requestFocus();
        }
    };
    Map<Character,int[]> Keycode = Map.ofEntries(
            entry('Q', new int[] {0,0}),
            entry('W', new int[] {0,1}),
            entry('E', new int[] {0,2}),
            entry('R', new int[] {0,3}),
            entry('T', new int[] {0,4}),
            entry('Y', new int[] {0,5}),
            entry('U', new int[] {0,6}),
            entry('I', new int[] {0,7}),
            entry('O', new int[] {0,8}),
            entry('P', new int[] {0,9}),
            entry('A', new int[] {1,0}),
            entry('S', new int[] {1,1}),
            entry('D', new int[] {1,2}),
            entry('F', new int[] {1,3}),
            entry('G', new int[] {1,4}),
            entry('H', new int[] {1,5}),
            entry('J', new int[] {1,6}),
            entry('K', new int[] {1,7}),
            entry('L', new int[] {1,8}),
            entry('Z', new int[] {2,1}),
            entry('X', new int[] {2,2}),
            entry('C', new int[] {2,3}),
            entry('V', new int[] {2,4}),
            entry('B', new int[] {2,5}),
            entry('N', new int[] {2,6}),
            entry('M', new int[] {2,7})
    );
    @FXML
    protected void initialize() {
        HBox node = (HBox) VirtualKeyboard.getChildren().get(0);
        ButtonStyle = node.getChildren().get(0).getStyle();
        PrepareMenu();
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
        for (Node hboxes: VirtualKeyboard.getChildren()) {
            HBox current = (HBox) hboxes;
            for (Node text: current.getChildren()) {
                text.setStyle(ButtonStyle + "-fx-background-color: transparent;");
            }
        }
    }
    @FXML
    protected void onClickStart() {
        WordlePane.setVisible(true);
        VirtualKeyboard.setVisible(true);
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
                text.setStyle("-fx-border-color: black; -fx-display-caret: false;");
            }
        }
        AnnounceBoard.setText("");
        AnnounceBoard2.setText("");
        for (Node hboxes: VirtualKeyboard.getChildren()) {
            HBox current = (HBox) hboxes;
            for (Node text: current.getChildren()) {
                text.setStyle(ButtonStyle + "-fx-background-color: transparent;");
            }
        }
    }
    @FXML
    protected void Start() {
        CurrentAttempt = 0;
        CurrentLetter = 0;
        VirtualKeyboard.setVisible(true);
        HBox first = (HBox) WordlePane.getChildren().get(0);
        TextField firstField = (TextField) first.getChildren().get(0);
        firstField.setEditable(true);
        firstField.focusedProperty().addListener(listener);
        CurrentFieldGuess = firstField;
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
    protected void onButtonTyped(MouseEvent event) throws Exception {
        Button button = (Button) event.getSource();
        lastKey = button.getText();
        if (CurrentLetter == 4 && Objects.equals(lastKey, "ENTER")) {
            onSubmission();
        }
        else {
            if (Objects.equals(lastKey, "")) lastKey = "BACK_SPACE";
            onType();
            onRelease();
        }
    }
    @FXML
    protected void MouseClick() {
        if (!inside) MenuBarClick();
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
                    CurrentField.focusedProperty().removeListener(listener);
                    CurrentLetter--;
                    CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
                    CurrentFieldGuess = CurrentField;
                    CurrentField.focusedProperty().addListener(listener);
                    CurrentField.setText("");
                    CurrentField.setEditable(true);
                    CurrentField.requestFocus();
                }
            }
        }
        else if (CurrentLetter < 4 && !Objects.equals(lastKey, "ENTER")) {
            if (Objects.equals(CurrentField.getText(),"")) CurrentField.setText(lastKey);
            CurrentField.setEditable(false);
            CurrentField.focusedProperty().removeListener(listener);
            new Pulse(CurrentField).setSpeed(4).play();
            CurrentLetter++;
            CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
            CurrentField.setEditable(true);
            CurrentField.requestFocus();
            CurrentFieldGuess = CurrentField;
            CurrentField.focusedProperty().addListener(listener);
        }
        else if (CurrentLetter == 4 && !Objects.equals(lastKey, "ENTER")) {
            if (!Objects.equals(lastKey, lastCharacter)) {
                CurrentField.setText(lastKey);
                new Pulse(CurrentField).setSpeed(4).play();
            }
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
        CurrentField.focusedProperty().removeListener(listener);
        String[] styles = CheckSubmission(s.toString());
        CheckAnimation(CurrentHbox,styles);
        if (CurrentAttempt<5 && !Win) {
            CurrentAttempt++;
            CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
            CurrentLetter = 0;
            CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
            CurrentField.setEditable(true);
            CurrentField.requestFocus();
            CurrentFieldGuess = CurrentField;
            CurrentField.focusedProperty().addListener(listener);
        }
    }
    protected String[] CheckSubmission(String guess) {
        String answer = Answer;
        String[] styles = new String[5];
        if (Objects.equals(answer, guess)) {
            for (int i = 0; i < 5; i++) {
                styles[i] = "-fx-background-color: Green;";
            }
            EndGame(true);
            return styles;
        }
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                styles[i] = "-fx-background-color: Green;";
                setKeyboardColor(guess.charAt(i),"-fx-background-color: Green;");
                answer = answer.substring(0, i) + "#" + answer.substring(i + 1);
                guess = guess.substring(0,i) + "@" + guess.substring(i+1);
            }
        }
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == '@') continue;
            boolean check = false;
            for (int j = 0; j < 5; j++) {
                if (guess.charAt(i) == answer.charAt(j)) {
                    styles[i] = "-fx-background-color: Orange;";
                    setKeyboardColor(guess.charAt(i),"-fx-background-color: Orange;");
                    answer = answer.substring(0, j) + "#" + answer.substring(j + 1);
                    guess = guess.substring(0,i) + "@" + guess.substring(i+1);
                    check = true;
                    break;
                }
            }
            if (!check) {
                styles[i] = "-fx-background-color: DarkGrey;";
                setKeyboardColor(guess.charAt(i),"-fx-background-color: DarkGrey;");
            }
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
            VirtualKeyboard.setVisible(false);
            ReplayButton.setVisible(true);
            AnnounceBoard.setText("You " + (win ? "won":"lost."));
            if (win) AnnounceBoard2.setText("Number of guess(es): " + (CurrentAttempt+1));
            else AnnounceBoard2.setText("The answer is: "+ Answer.charAt(0)+Answer.substring(1).toLowerCase());
        });
        replayHalt.play();
    }
    @FXML
    private void setKeyboardColor(Character c, String style) {
        PauseTransition pause = new PauseTransition(Duration.seconds(2.6));
        pause.setOnFinished(e -> {
            HBox Hboxtemp = (HBox) VirtualKeyboard.getChildren().get(Keycode.get(c)[0]);
            if (!Objects.equals(Hboxtemp.getChildren().get(Keycode.get(c)[1]).getStyle(), ButtonStyle + style)) {
                Hboxtemp.getChildren().get(Keycode.get(c)[1]).setStyle(ButtonStyle + style);
            }
        });
        pause.playFromStart();
    }
}
