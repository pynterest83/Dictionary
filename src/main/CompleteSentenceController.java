package main;
import base.DictionaryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.web.WebView;
import base.CompleteSentence;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
public class CompleteSentenceController {
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
    protected void onImportFromFileClick() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f != null) {
            System.out.println(f.getAbsolutePath());
            DictionaryManager.importFromFile(f.getAbsolutePath());
        }
    }
    @FXML
    protected void onExportToFileClick() {
        DictionaryManager.exportToFile();
    }
    @FXML
    public void onClickSearchButton(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) gameScreen.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    protected void onClickGameButton() throws IOException {
        Stage stage = (Stage) gameScreen.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void onClickGGTranslateButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) gameScreen.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"ggTranslate.fxml");
    }
    @FXML
    public void onClickFavouriteButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    protected void onClickAdd() throws IOException {
        Stage stage = (Stage) gameScreen.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() throws IOException {
        Stage stage = (Stage) gameScreen.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"modify.fxml");
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
        CurrentQuestion = rand.nextInt(502);
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
