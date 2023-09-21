package main;

import base.DictionaryManager;
import base.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditController {
    @FXML
    private TextField modifyText;
    @FXML
    private HTMLEditor modifyEditor;
    String[] suggestions;
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
        Stage stage = (Stage) modifyEditor.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"search.fxml");
    }
    @FXML
    public void GameButton(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) modifyEditor.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"game.fxml");
    }
    @FXML
    public void GGTranslateButton(ActionEvent actionEvent) {
        return;
    }
    @FXML
    public void FavouriteButton(ActionEvent actionEvent) {
        return;
    }

    // Add
    @FXML
    public void onClickAddButton(ActionEvent actionEvent) {
        if (modifyText.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must enter a word.");
            alert.showAndWait();

            return;
        }
        modifyEditor.setHtmlText("<html>" + modifyText.getText() + " /" + modifyText.getText() + "/"
                + "<ul><li><b><i> loại từ: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ nhất: </b></font><ul></li></ul></ul></li></ul><ul><li><b><i>loại từ khác: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ hai: </b></font></li></ul></li></ul></html>");
    }
    @FXML
    public void addReset() {
        modifyEditor.setHtmlText("<html>" + modifyText.getText() + " /" + modifyText.getText() + "/"
                + "<ul><li><b><i> loại từ: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ nhất: </b></font><ul></li></ul></ul></li></ul><ul><li><b><i>loại từ khác: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ hai: </b></font></li></ul></li></ul></html>");
    }
    @FXML
    public void add(ActionEvent actionEvent) throws Exception {
        String meaning = modifyEditor.getHtmlText().replace(" dir=\"ltr\"", "");
        if (DictionaryManager.addWord(modifyText.getText(), meaning)) {
            addReset();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Thêm từ thành công");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Từ bạn thêm đã tồn tại! Hãy chọn chức năng sửa đổi!");
            alert.showAndWait();
        }
    }

    // Modify
    @FXML
    private void initialize() {
        // Auto complete
        // update search bar with new suggestions and prepare text field for auto completion
        // convert suggestions to list and fill in the search bar
        TextFields.bindAutoCompletion(modifyText, input -> {
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
    }
}
