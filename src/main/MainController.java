package main;

import animatefx.animation.*;
import base.DictionaryManager;
import base.Word;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class MainController {
    @FXML
    protected Button menuBarButton;
    @FXML
    protected Pane menuBar;
    protected boolean menuOpen = false;
    @FXML
    protected Button searchButton;
    @FXML
    protected Button GameButton;
    @FXML
    protected Button GGTranslateButton;
    @FXML
    protected Button LearningButton;
    protected Boolean inside = false;
    @FXML
    private void initialize() {
        PrepareMenu(true);
    }
    @FXML
    protected void PrepareMenu(boolean isLeft) {
        menuBarButton.addEventHandler((MouseEvent.MOUSE_CLICKED), mouseEvent -> {
            ZoomIn zoomIn = new ZoomIn(menuBarButton);
            zoomIn.setSpeed(1);
            zoomIn.play();
        });
        menuBarButton.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)-> {
            if (oldValue && !newValue && menuOpen && !inside) {
                MenuBarClick();
            }
        });
        if (isLeft) {
            menuBar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                inside = true;
            });
            menuBar.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                inside = false;
            });
        }
        else {
            menuBar.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                inside = true;
            });
            menuBar.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
                inside = false;
            });
        }
    }
    @FXML
    protected void onExportToFileClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Export to File");
        alert.setHeaderText("Choose file to export?");
        alert.setContentText("Choose File");
        ButtonType buttonTypeOne = new ButtonType("Export to EN_VI.txt");
        ButtonType buttonTypeTwo = new ButtonType("Export to VI_EN.txt");
        ButtonType buttonTypeCancel = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            DictionaryManager.exportToFile("EN_VI");
        } else if (result.get() == buttonTypeTwo) {
            DictionaryManager.exportToFile("VI_EN");
        }
        else {
            alert.close();
        }
    }
    @FXML
    protected void onImportFromFileClick(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f != null) {
            ArrayList<Word> repeated = new ArrayList<>();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Import from File");
            alert.setHeaderText("Choose file to import?");
            alert.setContentText("Choose File");
            ButtonType buttonTypeOne = new ButtonType("Import to EN_VI");
            ButtonType buttonTypeTwo = new ButtonType("Import to VI_EN");
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne){
                repeated = DictionaryManager.importFromFile(f.getAbsolutePath(), "EN_VI");
            } else if (result.get() == buttonTypeTwo) {
                repeated = DictionaryManager.importFromFile(f.getAbsolutePath(), "VI_EN");
            }
            if (!repeated.isEmpty()) {
                for (int i =0; i<repeated.size(); i++) {
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setTitle("Remove Repeated Words");
                    alert1.setHeaderText(repeated.get(i).getWordTarget() + " is already in the dictionary.");
                    alert1.setContentText("Do you want to replace " + repeated.get(i).getWordTarget() +"?");
                    Optional<ButtonType> result1 = alert.showAndWait();

                    if (result1.get() == ButtonType.OK) {
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
    public void onClickHomeButton(ActionEvent actionEvent) throws Exception {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"main.fxml");
    }
    @FXML
    public void onClickSearchButton(ActionEvent actionEvent) throws Exception {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    protected void onClickGameButton() throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void onClickGGTranslateButton(ActionEvent actionEvent) throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"ggTranslate.fxml");
    }
    @FXML
    public void onClickLearningButton(ActionEvent actionEvent) throws IOException {
        inside = false;
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"learning.fxml");
    }
    @FXML
    protected void onClickAdd() {
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() throws IOException {
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"modify.fxml");
    }
    @FXML
    protected void MenuBarClick() {
        menuOpen = !menuOpen;
        if (menuOpen) {
            menuBar.setVisible(true);
            menuBar.setDisable(true);
            SlideInLeft inLeft = new SlideInLeft(menuBar);
            inLeft.setOnFinished(e -> menuBar.setDisable(false));
            inLeft.setSpeed(1);
            inLeft.play();
        }
        else {
            menuBar.setDisable(true);
            SlideOutLeft outLeft= new SlideOutLeft(menuBar);
            outLeft.setOnFinished(e -> menuBar.setVisible(menuOpen));
            outLeft.setSpeed(1);
            outLeft.play();
        }
    }
    @FXML
    protected void HideMenuBar() {
        menuOpen = false;
        menuBar.setVisible(false);
        menuBar.setDisable(true);
    }

    @FXML
    public void onClickClearENHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText("Are you sure you want to clear history?");
        alert.setContentText("Choose your option.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            DictionaryManager.EN_History.clear();
            DictionaryManager.addHistory("", "EN_VI");
        }
    }

    @FXML
    public void onClickClearVIHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText("Are you sure you want to clear history?");
        alert.setContentText("Choose your option.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            DictionaryManager.VI_History.clear();
            DictionaryManager.addHistory("", "VI_EN");
        }
    }

}
