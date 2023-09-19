package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import base.DictionaryManager;
import org.controlsfx.control.textfield.TextFields;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController {

    @FXML
    private TextField searchBar;
    @FXML
    private TextArea wordExplain;
    @FXML
    protected void SearchButton() {
        return;
    }
    @FXML
    protected void onExportToFileClick(ActionEvent event) {
        DictionaryManager.exportToFile();
    }

    @FXML
    protected void onImportFromFileClick(ActionEvent event) {
        DictionaryManager.importFromFile();
    }
    @FXML
    protected void onTextChange() {
            String[] suggestions = DictionaryManager.dictionarySearcher(searchBar.getText());
            TextFields.bindAutoCompletion(searchBar, suggestions);
    }

}
