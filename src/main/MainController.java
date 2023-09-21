package main;

import base.DictionaryManager;
import base.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class MainController {
    @FXML
    private AnchorPane searchPane;
    @FXML
    private AnchorPane gamePane;
    @FXML
    private AnchorPane ggTranslatePane;
    @FXML
    private AnchorPane favouritePane;
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
    public void SearchButton(ActionEvent actionEvent) throws Exception {
        return;
    }
    @FXML
    public void GameButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    public void GGTranslateButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    public void FavouriteButton(ActionEvent actionEvent) {
        return;
    }
}
