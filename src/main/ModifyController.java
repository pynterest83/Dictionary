package main;

import base.DictionaryManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    @FXML
    private Button en_vi_dict;
    @FXML
    private Button vi_en_dict;
    String[] suggestions;
    private String type_Dict = "EN_VI";
    @FXML
    private void initialize() {
        en_vi_dict.setVisible(true);
        vi_en_dict.setVisible(false);
        PrepareMenu();
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
    protected void MouseClick() {
        if (!inside) MenuBarClick();
    }
    @FXML
    protected void UserInput() throws Exception {
        suggestions = DictionaryManager.dictionarySearcher(modifyText.getText(), type_Dict);
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
        if (DictionaryManager.dictionaryLookup(modifyText.getText(), type_Dict).equals("Word not found.")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Word not found.");
            alert.showAndWait();
        }
        else modifyEditor.setHtmlText(DictionaryManager.dictionaryLookup(modifyText.getText(), type_Dict));
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
        DictionaryManager.modifyWord(modifyText.getText(), meaning, type_Dict);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText("Sửa từ thành công");
        alert.showAndWait();
        DictionaryManager.exportToFile(type_Dict);

        modifyEditor.setHtmlText("");
        modifyText.clear();
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
            DictionaryManager.deleteWord(modifyText.getText(), type_Dict);
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Thông báo");
            alert2.setHeaderText(null);
            alert2.setContentText("Xóa từ thành công");
            alert2.showAndWait();
        }
        DictionaryManager.exportToFile(type_Dict);

        modifyEditor.setHtmlText("");
        modifyText.clear();
    }

    public void onEnterModify(ActionEvent actionEvent) {
        try {
            onClickModifyButton(actionEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeDict(ActionEvent actionEvent) {
        if (type_Dict.equals("EN_VI")) {
            type_Dict = "VI_EN";
            en_vi_dict.setVisible(false);
            vi_en_dict.setVisible(true);
        }
        else {
            type_Dict = "EN_VI";
            en_vi_dict.setVisible(true);
            vi_en_dict.setVisible(false);
        }
    }
}
