package main;
import base.DictionaryManager;
import base.TranslateAPI;
import base.Word;
import base.VoiceRSS;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class GGTranslateController {
    private static final String PATH = "src/resources/Spelling.txt";
    private String sourceLangCode = "";
    private String targetLangCode = "";
    @FXML
    private ComboBox<String> SourceLang;
    @FXML
    private ComboBox<String> TargetLang;
    @FXML
    private TextField input;
    @FXML
    private TextField output;
    @FXML
    private void initialize() throws IOException {
        TranslateAPI.addDefault();
        for (HashMap.Entry<String, String> entry : TranslateAPI.langMap.entrySet()) {
            SourceLang.getItems().add(entry.getKey());
            TargetLang.getItems().add(entry.getKey());
        }
    }
    @FXML
    private Button GGTranslateButton;
    @FXML
    protected void onExportToFileClick(ActionEvent event) {
        DictionaryManager.exportToFile();
    }
    @FXML
    protected void onImportFromFileClick(ActionEvent event) {
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
    public void onClickSearchButton(ActionEvent actionEvent) throws Exception {
        Stage stage = (Stage) GGTranslateButton.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    protected void onClickGameButton() throws IOException {
        Stage stage = (Stage) GGTranslateButton.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void onClickFavouriteButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    protected void onClickAdd() throws IOException {
        Stage stage = (Stage) GGTranslateButton.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() throws IOException {
        Stage stage = (Stage) GGTranslateButton.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"modify.fxml");
    }

    @FXML
    public void SelectSourceLang(ActionEvent actionEvent) {
        sourceLangCode = TranslateAPI.langMap.get(SourceLang.getValue());
    }
    @FXML
    public void SelectTargetLang(ActionEvent actionEvent) {
        targetLangCode = TranslateAPI.langMap.get(TargetLang.getValue());
    }

    public void swap(ActionEvent actionEvent) {
        String temp = SourceLang.getValue();
        SourceLang.setValue(TargetLang.getValue());
        TargetLang.setValue(temp);

        String tmp = input.getText();
        input.setText(output.getText());
        output.setText(tmp);
    }

    public void translate(ActionEvent actionEvent) {
        try {
            output.setText(TranslateAPI.googleTranslate(sourceLangCode, targetLangCode, input.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickSpeak1(ActionEvent actionEvent) {
        return;
    }

    public void onClickSpeak2(ActionEvent actionEvent) {
        return;
    }
}
