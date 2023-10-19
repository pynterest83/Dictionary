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

public class AddController extends MainController {
    @FXML
    private TextField addText;
    @FXML
    private HTMLEditor addEditor;
    private String type_Dict = "EN_VI";
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
        if (DictionaryManager.addWord(addText.getText(), meaning, type_Dict)) {
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

            onClickModify();
        }
        DictionaryManager.exportToFile("");
    }
    @FXML
    public void onEnterAdd(ActionEvent actionEvent) {
        try {
            onClickAddButton(actionEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeDict(ActionEvent actionEvent) {
        if (type_Dict.equals("EN_VI")) {
            type_Dict = "VI_EN";
        }
        else {
            type_Dict = "EN_VI";
        }
    }
}
