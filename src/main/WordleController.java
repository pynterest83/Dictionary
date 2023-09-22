package main;

import base.DictionaryManager;
import base.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class WordleController {
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
                ((TextField) text).setEditable(false);
                ((TextField) text).setText("");
                ((TextField) text).setStyle(null);
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
        Answer = "ALOOF";
    }
    @FXML
    protected void onClickReplay() {
        ReplayButton.setVisible(false);
        ResetTiles();
        Start();
    }
    @FXML
    protected void onImportFromFileClick() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f != null) {
            System.out.println(f.getAbsolutePath());
            ArrayList<Word> repeated = DictionaryManager.importFromFile(f.getAbsolutePath());
            if (repeated.size() > 0) {
                for (int i =0; i<repeated.size(); i++) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Remove Repeated Words");
                    alert.setHeaderText(repeated.get(i).getWordTarget() + " is already in the dictionary.");
                    alert.setContentText("Do you want to replace " + repeated.get(i).getWordTarget() +"?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.get() == ButtonType.OK) {
                        int position = DictionaryManager.binSearch(repeated.get(i).getWordTarget());
                        DictionaryManager.curDict.remove(position);
                        DictionaryManager.curDict.add(repeated.get(i));
                        Collections.sort(DictionaryManager.curDict);
                    }
                }
            }
        }
    }
    @FXML
    protected void onExportToFileClick() {
        DictionaryManager.exportToFile();
    }
    @FXML
    protected void onClickModify() throws IOException {
        Stage stage = (Stage) WordlePane.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"modify.fxml");
    }
    @FXML
    protected void onClickAdd() throws IOException {
        Stage stage = (Stage) WordlePane.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void SearchButton() throws IOException {
        Stage stage = (Stage) WordlePane.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    public void GameButton(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) WordlePane.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"completeSentenceGame.fxml");
    }
    @FXML
    public void GGTranslateButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    public void FavouriteButton(ActionEvent actionEvent) {
        return;
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
    protected void onType(KeyEvent event) {
        HBox CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
        TextField CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
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
    protected void onSubmission() {
        HBox CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
        TextField CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
        CurrentField.setEditable(false);
        if (Objects.equals(CurrentField.getText(), "")) return;
        CheckSubmission(CurrentHbox);
        if (CurrentAttempt<5 && !Win) {
            CurrentAttempt++;
            CurrentHbox = (HBox) WordlePane.getChildren().get(CurrentAttempt);
            CurrentLetter = 0;
            CurrentField = (TextField) CurrentHbox.getChildren().get(CurrentLetter);
            CurrentField.setEditable(true);
            CurrentField.requestFocus();
        }
    }
    protected void CheckSubmission(HBox hBox) {
        StringBuilder s = new StringBuilder();
        for (Node text : hBox.getChildren()) {
            s.append(((TextField) text).getText());
        }
        String guess = s.toString();
        String answer = Answer;
        if (Objects.equals(answer, guess)) {
            for (int i = 0; i < 5; i++) {
                hBox.getChildren().get(i).setStyle("-fx-control-inner-background: Green;");
            }
            EndGame(true);
            return;
        }
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                hBox.getChildren().get(i).setStyle("-fx-control-inner-background: Green;");
                answer = answer.substring(0, i) + "#" + answer.substring(i + 1);
                guess = guess.substring(0,i) + "@" + guess.substring(i+1);
            }
        }
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == '@') continue;
            boolean check = false;
            for (int j = 0; j < 5; j++) {
                if (guess.charAt(i) == answer.charAt(j)) {
                    hBox.getChildren().get(i).setStyle("-fx-control-inner-background: Orange;");
                    answer = answer.substring(0, j) + "#" + answer.substring(j + 1);
                    guess = guess.substring(0,i) + "@" + guess.substring(i+1);
                    check = true;
                    break;
                }
            }
            if (!check) hBox.getChildren().get(i).setStyle("-fx-control-inner-background: DarkGrey;");
        }
        if (CurrentAttempt == 5) {
            EndGame(false);
        }
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
        ReplayButton.setVisible(true);
        AnnounceBoard.setText("You " + (win ? "won":"lost."));
        if (win) AnnounceBoard2.setText("Number of guess(es): " + (CurrentAttempt+1));
    }
}
