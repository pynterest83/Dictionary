package main;

import base.DictionaryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import org.controlsfx.control.textfield.TextFields;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModifyController extends MainController {
    @FXML
    private TextField modifyText;
    @FXML
    private HTMLEditor modifyEditor;
    String[] suggestions;

    @FXML
    private void initialize() {
        TextFields.bindAutoCompletion(modifyText, input -> {
            if (modifyText.getText().length() <= 1) return Collections.emptyList();
            return Stream.of(suggestions)
                    .filter(s -> s.contains(modifyText.getText()))
                    .collect(Collectors.toList());
        });
        modifyText.textProperty().addListener((obs, oldText, newText) -> {
            try {
                // get suggestions from user input and add it the text field which is search bar
                UserInput();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    protected void UserInput() throws Exception {
        suggestions = DictionaryManager.dictionarySearcher(modifyText.getText());
    }
    @FXML
    public void onClickModifyButton(ActionEvent actionEvent) throws Exception {
        if (modifyText.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must enter a word.");
            alert.showAndWait();

            return;
        }
        if (DictionaryManager.dictionaryLookup(modifyText.getText()).equals("Word not found.")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Word not found.");
            alert.showAndWait();
        }
        else modifyEditor.setHtmlText(DictionaryManager.dictionaryLookup(modifyText.getText()));
    }
    @FXML
    public void modifyReset() {
        modifyEditor.setHtmlText("<html>" + modifyText.getText() + " /" + modifyText.getText() + "/"
                + "<ul><li><b><i> loại từ: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ nhất: </b></font><ul></li></ul></ul></li></ul><ul><li><b><i>loại từ khác: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ hai: </b></font></li></ul></li></ul></html>");
    }
    @FXML
    public void modify(ActionEvent actionEvent) throws Exception {
        String meaning = modifyEditor.getHtmlText().replace(" dir=\"ltr\"", "");
        modifyReset();
        DictionaryManager.modifyWord(modifyText.getText(), meaning);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Sửa từ thành công");
        alert.showAndWait();
        DictionaryManager.exportToFile();
    }

    // Delete
    @FXML
    public void delete(ActionEvent actionEvent) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Word");
        alert.setHeaderText("Are you sure you want to delete " + modifyText.getText() + "?");
        alert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            DictionaryManager.deleteWord(modifyText.getText());
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Thông báo");
            alert2.setHeaderText(null);
            alert2.setContentText("Xóa từ thành công");
            alert2.showAndWait();
        }
        DictionaryManager.exportToFile();
    }

    public void onEnterModify(ActionEvent actionEvent) {
        try {
            onClickModifyButton(actionEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
