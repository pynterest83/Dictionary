package main;

import base.TranslateAPI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class GGTranslateController extends MainController{
    private static final String PATH = "src/resources/Spelling.txt";
    private String sourceLangCode = "";
    private String targetLangCode = "";
    @FXML
    private SearchableComboBox<String> SourceLang;
    @FXML
    private SearchableComboBox<String> TargetLang;
    @FXML
    private TextField input;
    @FXML
    private TextField output;
    @FXML
    private void initialize() throws IOException {
        for (HashMap.Entry<String, String> entry : TranslateAPI.langMap.entrySet()) {
            SourceLang.getItems().add(entry.getKey());
            TargetLang.getItems().add(entry.getKey());
        }
    }

    @FXML
    public void SelectSourceLang(ActionEvent actionEvent) {
        sourceLangCode = TranslateAPI.langMap.get(SourceLang.getValue());
    }
    @FXML
    public void SelectTargetLang(ActionEvent actionEvent) {
        targetLangCode = TranslateAPI.langMap.get(TargetLang.getValue());
    }
    @FXML
    public void swap(ActionEvent actionEvent) {
        String temp = SourceLang.getValue();
        SourceLang.setValue(TargetLang.getValue());
        TargetLang.setValue(temp);

        String tmp = input.getText();
        input.setText(output.getText());
        output.setText(tmp);
    }
    @FXML
    public void translate(ActionEvent actionEvent) {
        try {
            if (input.getText().equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please enter a word");
                alert.setContentText("Please enter a word");
                alert.showAndWait();
                return;
            }
            if (targetLangCode == "") {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a target language");
                alert.setContentText("Please select a target language");
                alert.showAndWait();
                return;
            }
            output.setText(TranslateAPI.googleTranslate(sourceLangCode, targetLangCode, input.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void onClickSpeak1(ActionEvent actionEvent) throws IOException, URISyntaxException {
        if (SourceLang.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a language");
            alert.setContentText("Please select a language");
            alert.showAndWait();
            return;
        }
        if (input.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        TranslateAPI.speakAudio(input.getText(),SourceLang.getValue());
    }

    public void onClickSpeak2(ActionEvent actionEvent) throws IOException, URISyntaxException {
        if (TargetLang.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a language");
            alert.setContentText("Please select a language");
            alert.showAndWait();
            return;
        }
        if (output.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        TranslateAPI.speakAudio(output.getText(),TargetLang.getValue());
    }
}
