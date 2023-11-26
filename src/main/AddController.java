package main;

import base.DictionaryManager;
import controls.GeneralControls;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

import java.io.IOException;

public class AddController extends GeneralControls {
    @FXML
    private TextField addText;
    @FXML
    private HTMLEditor addEditor;
    @FXML
    private Button en_vi_dict;
    @FXML
    private Button vi_en_dict;
    private String type_Dict = "EN_VI";
    @FXML
    private void initialize() {
        loadOtherScences();
        en_vi_dict.setVisible(true);
        vi_en_dict.setVisible(false);
        PrepareMenu();
    }
    @FXML
    protected void MouseClick() {
        if (!inside) HideMenuBar();
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
        if (DictionaryManager.addWord(addText.getText(), meaning, type_Dict)) {
            addReset();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Thêm từ thành công");
            alert.showAndWait();
            addEditor.setHtmlText("");
            addText.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Thông báo");
            alert.setHeaderText(null);
            alert.setContentText("Từ bạn thêm đã tồn tại! Hãy chọn chức năng sửa đổi!");
            alert.showAndWait();
            addEditor.setHtmlText("");
            addText.clear();
            onClickModify();
        }
        DictionaryManager.exportToFile(type_Dict);
    }
    @FXML
    protected void onClickModify() throws IOException {
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"modify.fxml");
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
