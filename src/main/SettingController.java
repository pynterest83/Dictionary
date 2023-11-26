package main;

import controls.GeneralControls;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class SettingController extends GeneralControls {
    @FXML
    private Pane TitleBar;
    @FXML
    private Pane menuBar;
    @FXML
    private Slider SpeakVolumeSlider;
    @FXML
    private void initialize() {
        loadOtherScences();
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
}
