package main;

import animatefx.animation.Shake;
import base.DictionaryManager;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

public class BabblebotController extends MainController {
    @FXML
    private Text Score;
    @FXML
    private Text Time;
    @FXML
    private HBox Keys;
    @FXML
    private Button Submit;
    @FXML
    private ListView<String> Submissions;
    @FXML
    private TextField Answer;
    @FXML
    private Button StartButton;
    @FXML
    private AnchorPane GamePane;
    @FXML
    private AnchorPane ResultPane;
    @FXML
    private Text ScoreAnnounce;
    @FXML
    private ListView<String> AnswerList;
    @FXML
    private Text PrepareStart;
    private int availableWord;
    private String[] contains;
    String[] current;
    ArrayList<String> answerList = new ArrayList<>();
    private int score;

    @FXML
    private void initialize() {
        PrepareMenu(true);
        Answer.textProperty().addListener((obs, oldText, newText) -> Submit.setDisable(newText.length() <= 2));
        AnswerList.setCellFactory(e -> new ListCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                if (empty) {
                    setText(null);
                    setStyle("-fx-alignment:center; -fx-font-size:16;");
                } else {
                    setText(s);
                    if (Submissions.getItems().contains(s)) {
                        setStyle("-fx-alignment:center; -fx-font-size:16; -fx-background-color: #7CFC00");
                    } else {
                        setStyle("-fx-alignment:center; -fx-font-size:16;");
                    }
                }
            }
        });
    }
    @FXML
    private void startCountdown() {
        ObjectProperty<Duration> timeLeft = new SimpleObjectProperty<>(Duration.ofSeconds(2L *availableWord));
        Time.textProperty().bind(Bindings.createStringBinding(() ->
                String.format("%02d:%02d", timeLeft.get().toMinutesPart(), timeLeft.get().toSecondsPart()), timeLeft));
        Timeline countdown = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),(ActionEvent e) ->
                timeLeft.setValue(timeLeft.get().minus(1, ChronoUnit.SECONDS))));
        countdown.setCycleCount((int)timeLeft.get().getSeconds());
        countdown.setOnFinished(e -> endGame());
        countdown.play();
    }
    @FXML
    private void ChooseSet() throws IOException {
        long lineCount;
        try (Stream<String> stream = Files.lines(Paths.get("src/resources/babble.txt"), StandardCharsets.UTF_8)) {
            lineCount = stream.count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileReader file = new FileReader("src/resources/babble.txt");
        SecureRandom random = new SecureRandom();
        BufferedReader bufferedReader = new BufferedReader(file);
        int temp = random.nextInt((int) lineCount);
        for (int i = 0; i < temp; i ++) {
            bufferedReader.readLine();
        }
        contains = bufferedReader.readLine().split(",");
    }
    @FXML
    private void start() throws IOException {
        Submissions.getItems().clear();
        AnswerList.getItems().clear();
        Answer.setText("");
        reset(false);
        score = 0;
        Score.setText(Integer.toString(score));
        Submit.setDisable(true);
        startCountdown();
    }
    private void reset(boolean shuffle) {
        if (shuffle) {
            List<String> list = Arrays.asList(contains);
            Collections.shuffle(list);
            list.toArray(contains);
        }
        int i = 0;
        ArrayList<String> count = new ArrayList<>(Arrays.asList(Answer.getText().split("")));
        for (Node x: Keys.getChildren()) {
            ((Button) x).setText(contains[i]);
            ChangeStyle(x,"keyboard-button");
            if (!Objects.equals(Answer.getText(), "")) {
                for (int j = 0; j < Answer.getText().length(); j++) {
                    if (String.valueOf(Answer.getText().charAt(j)).equals(((Button) x).getText())
                            && x.getStyleClass().contains("keyboard-button")
                            && count.contains(((Button) x).getText())) {
                        ChangeStyle(x,"keyboard-clicked");
                        count.remove(((Button) x).getText());
                        break;
                    }
                }
            }
            i++;
        }
    }
    @FXML
    private void onType(ActionEvent e) {
        Button clicked = (Button) e.getSource();
        if (!clicked.getStyleClass().contains("keyboard-clicked")) {
            Answer.setText(Answer.getText() + clicked.getText());
            ChangeStyle(clicked, "keyboard-clicked");
        }
    }
    @FXML
    private void ChangeStyle(Node node, String style) {
        node.getStyleClass().clear();
        node.getStyleClass().add(style);
    }
    @FXML
    private void onShuffleClick() {
        reset(true);
    }
    @FXML
    private void onDeleteClick() {
        if (!Objects.equals(Answer.getText(), "")) {
            String deleteChar = Answer.getText().substring(Answer.getText().length()-1);
            for (Node x: Keys.getChildren()) {
                String s = ((Button)x).getText();
                if ((s.substring(s.length() - 1).equals(deleteChar)) && x.getStyleClass().contains("keyboard-clicked")) {
                    ChangeStyle(x,"keyboard-button");
                    break;
                }
            }
            Answer.setText(Answer.getText().substring(0,Answer.getText().length() -1));
        }
    }
    @FXML
    private void onSubmission() throws Exception {
        checksubmission();
        Answer.setText("");
        reset(false);
    }
    @FXML
    private void onClickStart() throws IOException {
        ChooseSet();
        new Thread(() -> {
            try {
                createAnswerList(contains);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        StartButton.setVisible(false);
        ResultPane.setVisible(false);
        PrepareStart.setVisible(true);
        ObjectProperty<Duration> timeLeft = new SimpleObjectProperty<>(Duration.ofSeconds(3));
        PrepareStart.textProperty().bind(Bindings.createStringBinding(() ->
                String.format("%d", timeLeft.get().toSecondsPart()), timeLeft));
        Timeline countdown = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),(ActionEvent e) ->
                timeLeft.setValue(timeLeft.get().minus(1, ChronoUnit.SECONDS))));
        countdown.setCycleCount((int)timeLeft.get().getSeconds());
        countdown.setOnFinished(e -> {
            PrepareStart.setVisible(false);
            GamePane.setVisible(true);
            ResultPane.setVisible(false);
            try {
                start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        countdown.play();
    }
    @FXML
    private void checksubmission() throws Exception {
        String submit = Answer.getText();
        if (Objects.equals(DictionaryManager.dictionaryLookup(submit, "EN_VI"), "Word not found.")) {
            new Shake(Submit).play();
        }
        else if (Submissions.getItems().contains(submit)) {
            new Shake(Submit).play();
        }
        else {
            score++;
            Score.setText(Integer.toString(score));
            Submissions.getItems().add(submit);
        }
    }
    @FXML
    private void endGame() {
        Time.textProperty().unbind();
        GamePane.setVisible(false);
        ScoreAnnounce.setText("You found " + score + "/" + availableWord + " words.");
        AnchorPane root = (AnchorPane) GamePane.getParent();
        Text gameOver = new Text("Game over");
        gameOver.setFont(new Font(24));
        gameOver.setLayoutX((root.getWidth() - gameOver.getLayoutBounds().getWidth())/2);
        gameOver.setLayoutY((root.getHeight() - gameOver.getLayoutBounds().getHeight())/2);
        root.getChildren().add(gameOver);
        PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(e -> {
            ResultPane.setVisible(true);
            root.getChildren().remove(gameOver);
        });
        for (String s: answerList) AnswerList.getItems().add(s);
        pause.playFromStart();
    }

    boolean nextPermutation(String[] p) throws Exception {
        for (int a = p.length - 2; a >= 0; a--)
            if (p[a].compareTo(p[a + 1]) < 0)
                for (int b = p.length - 1;; b--)
                    if (p[b].compareTo(p[a]) > 0) {
                        String t = p[a];
                        p[a] = p[b];
                        p[b] = t;
                        for (++a, b = p.length - 1; a < b; a++, b--) {
                            t = p[a];
                            p[a] = p[b];
                            p[b] = t;
                        }
                        StringBuilder s = new StringBuilder();
                        for (String str: p) {
                            s.append(str);
                        }
                        if (!Objects.equals(DictionaryManager.dictionaryLookup(s.toString().toLowerCase(), "EN_VI"), "Word not found.")) {
                            answerList.add(s.toString());
                            availableWord++;
                        }
                        current = p;
                        return true;
                    }
        return false;
    }
    String[][] generateCombinations(String[] input, int sequenceLength) {
        ArrayList<String[]> combinations = new ArrayList<>();
        int[] indices = new int[sequenceLength];
        if (sequenceLength <= input.length) {
            for (int i = 0; (indices[i] = i) < sequenceLength - 1; i++);
            combinations.add(getCombination(input,indices));
            while(true) {
                int i;
                for (i = sequenceLength - 1; i >= 0 && indices[i] == input.length - sequenceLength + i; i--);
                if (i < 0) break;
                indices[i]++;
                for (++i; i < sequenceLength; i++) {
                    indices[i] = indices[i-1] + 1;
                }
                combinations.add(getCombination(input,indices));
            }
        }
        return combinations.toArray(new String[][]{});
    }
    String[] getCombination(String[] input, int[] indices) {
        String[] result = new String[indices.length];
        for (int i = 0;i < indices.length; i++) result[i] = input[indices[i]];
        return result;
    }
    void createAnswerList(String[] input) throws Exception {
        availableWord = 0;
        answerList.clear();
        Arrays.sort(input);
        for (int i = 3;i <= 6; i++) {
            String[][] combinations = generateCombinations(input,i);
            for (String[] strings: combinations) {
                current = strings;
                if (!Objects.equals(DictionaryManager.dictionaryLookup(String.join("",current).toLowerCase(), "EN_VI"), "Word not found.")) {
                    answerList.add(String.join("",current));
                    availableWord++;
                }
                boolean check = true;
                while (check) {
                    check = nextPermutation(current);
                }
            }
        }
    }
}
