package main;

import base.TranslateAPI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.SearchableComboBox;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

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
    private ImageView loading = new ImageView(Paths.get("src/style/media/loading.gif").toUri().toURL().toString());
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
    }
    @FXML
    public void swap() {
        String temp = SourceLang.getValue();
        SourceLang.setValue(TargetLang.getValue());
        TargetLang.setValue(temp);

        String tmp = input.getText();
        input.setText(output.getText());
        output.setText(tmp);
    }
    @FXML
    public void translate() {
            if (input.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please enter a word");
                alert.setContentText("Please enter a word");
                alert.showAndWait();
                return;
            }
            if (Objects.equals(targetLangCode, "")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Please select a target language");
                alert.setContentText("Please select a target language");
                alert.showAndWait();
                return;
            }
            String translate = input.getText();
            output.clear();
            new Thread(()-> {
                AnchorPane parent = (AnchorPane) output.getParent();
                loading.setFitHeight(60);
                loading.setFitWidth(60);
                loading.setLayoutY(0);
                ImageView finalLoading = loading;
                Platform.runLater(()-> {
                    parent.getChildren().add(finalLoading);
                    input.setDisable(true);
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
        if (SourceLang.getValue() == null) {
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
        if (TargetLang.getValue() == null) {
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
        new Thread(() -> {
            loading.setFitWidth(60);
            loading.setFitHeight(60);
            loading.setLayoutY(180);
            AnchorPane parent = (AnchorPane) synonyms.getParent();
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
            } catch (URISyntaxException | ParseException | IOException e) {
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
        new Thread(() -> {
            loading.setFitWidth(60);
            loading.setFitHeight(60);
            loading.setLayoutY(180);
            AnchorPane parent = (AnchorPane) antonyms.getParent();
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
            } catch (URISyntaxException | ParseException | IOException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                parent.getChildren().remove(loading);
                antButton.setDisable(false);
            });
        }).start();
    }
}
