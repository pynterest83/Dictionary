package main;

import animatefx.animation.Shake;
import animatefx.animation.ZoomInUp;
import base.DictionaryManager;
import base.Sounds;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
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
    @FXML
    private Pane SubmitPane1;
    @FXML
    private Pane SubmitPane2;
    @FXML
    private MediaView Bot1Speak;
    @FXML
    private MediaView Bot2Speak;
    private int availableWord;
    private String[] contains;
    String[] current;
    ArrayList<String> answerList = new ArrayList<>();
    ArrayList<String> submission = new ArrayList<>(20);
    @FXML
    private static final MediaPlayer[] babblebot = {
            new MediaPlayer(new Media(Paths.get("src/style/media/babblebot1.mp4").toUri().toString())),
            new MediaPlayer(new Media(Paths.get("src/style/media/babblebot2.mp4").toUri().toString()))
    };
    @FXML
    private static final MediaPlayer[] babblebot_speak = {
            new MediaPlayer(new Media(Paths.get("src/style/media/babblebot1s.mp4").toUri().toString())),
            new MediaPlayer(new Media(Paths.get("src/style/media/babblebot2s.mp4").toUri().toString()))
    };
    private int score;
    static boolean side;
    static ArrayList<ArrayList<Node>> toRemove = new ArrayList<>(List.of(new ArrayList<Node>(),new ArrayList<Node>()));
    @FXML
    private void initialize() {
        loadOtherScences();
        PrepareMenu();
        Answer.textProperty().addListener((obs, oldText, newText) -> Submit.setDisable(newText.length() <= 2));
        AnswerList.setCellFactory(e -> new ListCell<>() {
            @Override
            protected void updateItem(String s, boolean empty) {
                super.updateItem(s, empty);
                getStyleClass().clear();
                if (empty) {
                    setText(null);
                    setStyle("-fx-alignment:center; -fx-font-size:16;");
                } else {
                    setText(s);
                    if (submission.contains(s)) {
                        setStyle("-fx-alignment:center; -fx-font-size:16; -fx-text-fill: #7CFC00");
                    } else {
                        setStyle("-fx-alignment:center; -fx-font-size:16;");
                    }
                }
            }
        });
        babblebot[0].setOnEndOfMedia(() -> {
            babblebot[0].seek(javafx.util.Duration.ZERO);
            babblebot[0].play();
        });
        babblebot[1].setOnEndOfMedia(() -> {
            babblebot[1].seek(javafx.util.Duration.ZERO);
            babblebot[1].play();
        });
    }
    @FXML
    private void startCountdown() {
        ObjectProperty<Duration> timeLeft = new SimpleObjectProperty<>(Duration.ofSeconds((long) (1.4*availableWord)));
        Time.textProperty().bind(Bindings.createStringBinding(() ->
                String.format("%02d:%02d", timeLeft.get().toMinutesPart(), timeLeft.get().toSecondsPart()), timeLeft));
        Timeline countdown = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),(ActionEvent e) -> {
            Duration time = timeLeft.get();
            if (time.getSeconds() == 11) Sounds.countdownsound.play();
            timeLeft.setValue(time.minus(1, ChronoUnit.SECONDS));
    }));
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
        side = false;
        submission.clear();
        AnswerList.getItems().clear();
        Answer.setText("");
        reset(false);
        score = 0;
        Score.setText(Integer.toString(score));
        Submit.setDisable(true);
        startCountdown();
        SetUpAnimation();
    }
    private void SetUpAnimation() {
        Bot1Speak.setMediaPlayer(babblebot[0]);
        Bot2Speak.setMediaPlayer(babblebot[1]);
        Bot1Speak.getMediaPlayer().play();
        Bot2Speak.getMediaPlayer().play();
        toRemove.get(0).clear();
        toRemove.get(1).clear();
        SubmitPane1.getChildren().clear();
        SubmitPane2.getChildren().clear();
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
            Answer.setText(Answer.getText().substring(0,Answer.getText().length() - 1));
        }
    }
    @FXML
    private void onSubmission() throws Exception {
        checksubmission();
        Answer.setText("");
        reset(false);
    }
    @FXML
    private Pane InstructionPane;
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
        InstructionPane.setVisible(false);
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
    private void checksubmission() {
        String submit = Answer.getText();
        if (Objects.equals(DictionaryManager.dictionaryLookup(submit, "EN_VI"), "Word not found.")) {
            Submit.setText("NOT FOUND");
            Submit.getStyleClass().clear();
            Submit.getStyleClass().add("wrong-answer");
            Shake wrong = new Shake(Submit);
            wrong.setOnFinished(e -> {
                Submit.setText("SUBMIT");
                Submit.getStyleClass().clear();
                Submit.getStyleClass().add("keyboard-button");
            });
            wrong.play();
            Sounds.wrongsound.play();
        }
        else if (submission.contains(submit)) {
            Submit.setText("ALREADY SUBMITTED");
            Submit.getStyleClass().clear();
            Submit.getStyleClass().add("already-answered");
            Shake wrong = new Shake(Submit);
            wrong.setOnFinished(e -> {
                Submit.setText("SUBMIT");
                Submit.getStyleClass().clear();
                Submit.getStyleClass().add("keyboard-button");
            });
            wrong.play();
            Sounds.wrongsound.play();
        }
        else {
            score++;
            Score.setText(Integer.toString(score));
            submission.add(submit);
            int index = side ? 0 : 1;
            Pane SubmitPane = side ? SubmitPane1 : SubmitPane2;
            MediaView BotSpeak = side ? Bot1Speak : Bot2Speak;
            ArrayList<Node> removeList = toRemove.get(index);
            Label label = setLabel(submit);
            SubmitPane.getChildren().add(label);
            ParallelTransition transitions = new ParallelTransition();
            for (Node node: SubmitPane.getChildren()) {
                if (node.getTranslateY() - 60 < 0) {
                    removeList.add(node);
                }
                else {
                    TranslateTransition transition = new TranslateTransition(javafx.util.Duration.seconds(0.5));
                    transition.setNode(node);
                    transition.setOnFinished(e -> node.setOpacity(node.getTranslateY() / 165));
                    transition.setToY(node.getTranslateY() - 60);
                    transitions.getChildren().add(transition);
                }
            }
            SubmitPane.getChildren().removeAll(toRemove.get(index));
            toRemove.get(index).clear();
            ZoomInUp inUp = new ZoomInUp(label);
            inUp.setSpeed(2);
            inUp.setOnFinished(e -> {
                label.setLayoutY(0);
                label.setTranslateY(165);
            });
            transitions.playFromStart();
            Platform.runLater(inUp::play);
            BotSpeak.setMediaPlayer(babblebot_speak[index]);
            BotSpeak.getMediaPlayer().setOnEndOfMedia(()->{
                BotSpeak.getMediaPlayer().stop();
                BotSpeak.setMediaPlayer(babblebot[index]);
                BotSpeak.getMediaPlayer().play();
            });
            BotSpeak.getMediaPlayer().play();
            Sounds.correctsound.get(new Random().nextInt(2)).play();
            side = !side;
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

    boolean nextPermutation(String[] p) {
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
    private Label setLabel(String submit) {
        Label label = new Label(submit);
        label.setTranslateY(80);
        label.getStyleClass().add("speech-bubble");
        label.setMinWidth(178);
        label.setMinHeight(55);
        label.setLayoutY(165);
        label.setAlignment(Pos.CENTER);
        return label;
    }
}
