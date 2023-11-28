package main;

import base.Dictionary;
import base.DictionaryManager;
import controls.GeneralControls;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SettingController extends GeneralControls {
    @FXML
    private Pane TitleBar;
    @FXML
    private Pane menuBar;
    @FXML
    private Slider SpeakVolumeSlider;
    @FXML
    private Button AvaPane;
    @FXML
    private TextField NamePane;
    @FXML
    private TextField GenderPane;
    @FXML
    private Button saveButton;
    @FXML
    private void initialize() {
        loadOtherScences();
        setUp();
        PrepareMenu();
        SpeakVolumeSlider.valueProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) -> {
            try {
                SpeakVolumeSlider.lookup(".track").setStyle("-fx-background-insets: 0 " + (0.98 * SpeakVolumeSlider.getWidth() * ( 1 - SpeakVolumeSlider.getValue() / 100.0)) + " 0 0;");
                MainController.SpeakVolume = SpeakVolumeSlider.getValue()/100;
            } catch(Exception ignored){}
        });
        SpeakVolumeSlider.setValue(100);
    }
    @FXML
    private void MouseClick() {
        if (menuOpen) HideMenuBar();
    }
    @FXML
    public void onClickUpdateUser(ActionEvent actionEvent) throws IOException {
        if (NamePane.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Name cannot be empty");
            alert.showAndWait();
            return;
        }
        Dictionary.user.username = NamePane.getText();
        if (!GenderPane.getText().equalsIgnoreCase("Male") || !GenderPane.getText().equalsIgnoreCase("Female")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please enter valid gender: Male or Female");
        }
        if (GenderPane.getText().equalsIgnoreCase("Male")) Dictionary.user.gender = 1;
        else if (GenderPane.getText().equalsIgnoreCase("Female")) Dictionary.user.gender = 0;
        if (Dictionary.user.streak == null) {
            Dictionary.user.streak = java.time.LocalDate.now().toString();
            Dictionary.user.streakOn = true;
        }

        setUp();
        loadOtherScences();
        RunApplication.ReloadAll();
        DictionaryManager.updateUser();
    }

    @FXML
    private void setUp() {
        if (Dictionary.user.username != null) NamePane.setText(Dictionary.user.username);
        else NamePane.setText("User");

        if (Dictionary.user.gender == 0) {
            AvaPane.getStyleClass().clear();
            AvaPane.getStyleClass().add("woman-avatar");
            GenderPane.setText("Female");
        }
        else if (Dictionary.user.gender == 1) {
            AvaPane.getStyleClass().clear();
            AvaPane.getStyleClass().add("man-avatar");
            GenderPane.setText("Male");
        }
        else {
            GenderPane.setText("Unknown");
        }
    }
}
