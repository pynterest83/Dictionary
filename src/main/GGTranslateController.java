package main;

import base.TranslateAPI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class GGTranslateController extends MainController {
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
    private TextArea output;
    @FXML
    private TextArea synonyms;
    @FXML
    private TextArea antonyms;
    @FXML
    private Button symButton;
    @FXML
    private Button antButton;
    @FXML
    private Image loadImage = new Image(Paths.get("src/style/media/loading.gif").toUri().toURL().toString());

    public GGTranslateController() throws MalformedURLException {
    }

    @FXML
    private void initialize() {
        PrepareMenu(true);
        for (HashMap.Entry<String, String> entry : TranslateAPI.langMap.entrySet()) {
            SourceLang.getItems().add(entry.getKey());
            TargetLang.getItems().add(entry.getKey());
        }
    }

    @FXML
    public void SelectSourceLang() {
        sourceLangCode = TranslateAPI.langMap.get(SourceLang.getValue());
    }
    @FXML
    public void SelectTargetLang() {
        targetLangCode = TranslateAPI.langMap.get(TargetLang.getValue());
        input.requestFocus();
    }
    @FXML
    public void swap() {
        String temp = SourceLang.getValue();
        SourceLang.setValue(TargetLang.getValue());
        TargetLang.setValue(temp);

        sourceLangCode = TranslateAPI.langMap.get(SourceLang.getValue());
        targetLangCode = TranslateAPI.langMap.get(TargetLang.getValue());
        if (Objects.equals(sourceLangCode, null)){
            sourceLangCode = "";
        }
        if (Objects.equals(targetLangCode, null)){
            targetLangCode = "";
        }

        String tmp = input.getText();
        input.setText(output.getText());
        output.setText(tmp);
    }
    @FXML
    public void translate() {
        if (SourceLang.getValue() == null) {
            SourceLang.setValue("Auto Detect");
            sourceLangCode = "auto";
        }
        if (input.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        if (Objects.equals(targetLangCode, "")) {
            TargetLang.setValue("Auto Detect");
            targetLangCode = "auto";
        }
        ImageView loading = new ImageView(loadImage);
        String translate = input.getText();
        output.clear();
        new Thread(()-> {
            Pane parent = (Pane) output.getParent();
            loading.setFitHeight(60);
            loading.setFitWidth(60);
            loading.setLayoutX(20);
            loading.setLayoutY(334);
            Platform.runLater(()-> {
                parent.getChildren().add(loading);
                //input.setDisable(true);
                });
            try {
                output.setText(TranslateAPI.googleTranslate(sourceLangCode, targetLangCode, translate));
                Platform.runLater(()-> {
                    parent.getChildren().remove(loading);
                    input.setDisable(false);
                });
            } catch (IOException | URISyntaxException e) {
                parent.getChildren().remove(loading);
                input.setDisable(false);
            }
        }).start();
    }

    public void onClickSpeak1() throws IOException, URISyntaxException {
        if (SourceLang.getValue() == null || SourceLang.getValue().equals("Auto Detect")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a language");
            alert.setContentText("Please select a language");
            alert.showAndWait();
            return;
        }
        if (input.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        TranslateAPI.speakAudio(input.getText(),SourceLang.getValue());
    }

    public void onClickSpeak2() throws IOException, URISyntaxException {
        if (TargetLang.getValue() == null || TargetLang.getValue().equals("Auto Detect")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select a language");
            alert.setContentText("Please select a language");
            alert.showAndWait();
            return;
        }
        if (output.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        TranslateAPI.speakAudio(output.getText(),TargetLang.getValue());
    }

    public void onClickSynonyms() {
        synonyms.setText("");
        String word;
        if (input.getText().isEmpty() && output.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        if (sourceLangCode.equals("en") || (sourceLangCode.isEmpty())) {
            word = input.getText();
        } else if (targetLangCode.equals("en")) {
            word = output.getText();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("This Language is not supported");
            alert.setContentText("This Language is not supported");
            alert.showAndWait();
            return;
        }
        ImageView loading = new ImageView(loadImage);
        new Thread(() -> {
            loading.setFitWidth(60);
            loading.setFitHeight(60);
            loading.setLayoutX(17);
            loading.setLayoutY(58);
            Pane parent = (Pane) synonyms.getParent();
            Platform.runLater(() -> {
                parent.getChildren().add(loading);
                symButton.setDisable(true);
            });
            try {
                String[] synonymsList = TranslateAPI.theSaurus(word, "syn");
                if (synonymsList == null) {
                    synonyms.setText("No synonyms found");
                }
                else {
                    StringBuilder synonymsString = new StringBuilder();
                    for (String s : synonymsList) {
                        synonymsString.append(s).append("\n");
                    }
                    synonyms.setText(synonymsString.toString());
                }
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                parent.getChildren().remove(loading);
                symButton.setDisable(false);
            });
        }).start();
    }
    public void onClickAntonyms() {
        antonyms.setText("");
        String word;
        if (input.getText().isEmpty() && output.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter a word");
            alert.setContentText("Please enter a word");
            alert.showAndWait();
            return;
        }
        if (sourceLangCode.equals("en")) {
            word = input.getText();
        } else if (targetLangCode.equals("en")) {
            word = output.getText();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("This Language is not supported");
            alert.setContentText("This Language is not supported");
            alert.showAndWait();
            return;
        }
        ImageView loading = new ImageView(loadImage);
        new Thread(() -> {
            loading.setFitWidth(60);
            loading.setFitHeight(60);
            loading.setLayoutX(17);
            loading.setLayoutY(335);
            Pane parent = (Pane) antonyms.getParent();
            Platform.runLater(() -> {
                parent.getChildren().add(loading);

                antButton.setDisable(true);
            });
            try {
                String[] antonymsList = TranslateAPI.theSaurus(word, "ant");
                if (antonymsList == null) {
                    antonyms.setText("No antonyms found");
                }
                else {
                    StringBuilder antonymsString = new StringBuilder();
                    for (String s : antonymsList) {
                        antonymsString.append(s).append("\n");
                    }
                    antonyms.setText(antonymsString.toString());
                }
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                parent.getChildren().remove(loading);
                antButton.setDisable(false);
            });
        }).start();
    }
}
