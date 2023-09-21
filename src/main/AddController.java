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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class AddController {
    @FXML
    private TextField addText;
    @FXML
    private HTMLEditor addEditor;
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

    @FXML
    public void onClickAddButton(ActionEvent actionEvent) {
        if (addText.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("You must enter a word.");
            alert.showAndWait();

            return;
        }
        addEditor.setHtmlText("<html>" + addText.getText() + " /" + addText.getText() + "/"
                + "<ul><li><b><i> loại từ: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ nhất: </b></font><ul></li></ul></ul></li></ul><ul><li><b><i>loại từ khác: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ hai: </b></font></li></ul></li></ul></html>");
    }

    @FXML
    public void addReset() {
        addEditor.setHtmlText("<html>" + addText.getText() + " /" + addText.getText() + "/"
                + "<ul><li><b><i> loại từ: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ nhất: </b></font><ul></li></ul></ul></li></ul><ul><li><b><i>loại từ khác: </i></b><ul><li><font color='#cc0000'><b> Nghĩa thứ hai: </b></font></li></ul></li></ul></html>");
    }

    @FXML
    public void add(ActionEvent actionEvent) throws Exception {
        String meaning = addEditor.getHtmlText().replace(" dir=\"ltr\"", "");
        if (DictionaryManager.addWord(addText.getText(), meaning)) {
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
}
