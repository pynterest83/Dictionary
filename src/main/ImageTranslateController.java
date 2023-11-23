package main;

import base.ImageTranslate;
import base.TranslateAPI;
import com.google.cloud.vision.v1.BoundingPoly;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.commons.validator.routines.UrlValidator;
import org.controlsfx.control.SearchableComboBox;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static java.lang.Math.sqrt;

public class ImageTranslateController extends MainController {
    private static double scale;
    private static byte[] imageBytes;
    @FXML
    private static ArrayList<Label> SourceText = new ArrayList<>();
    @FXML
    private ImageView image;
    @FXML
    private Pane imagePane;
    @FXML
    private TitledPane AddImage;
    @FXML
    private Label ErrorLabel;
    @FXML
    private Label ClipboardLabel;
    @FXML
    private SearchableComboBox<String> TranslateLanguage;
    @FXML
    private ToggleButton ShowTranslateButton;
    @FXML
    private ImageView loading;
    @FXML
    private Image loadImage = new Image(Paths.get("src/style/media/loading.gif").toUri().toURL().toString());
    private static boolean translated = false;

    public ImageTranslateController() throws MalformedURLException {
    }

    @FXML
    private void initialize() {
        loadOtherScences();
        PrepareMenu();
        imagePane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        AddImage.focusedProperty().addListener(((observableValue, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                pause.setOnFinished(e->AddImage.setExpanded(false));
                pause.playFromStart();
            }
        }));
        for (HashMap.Entry<String, String> entry : TranslateAPI.langMap.entrySet()) {
            if (!Objects.equals(entry.getKey(), "Auto Detect"))
                TranslateLanguage.getItems().add(entry.getKey());
        }
    }
    @FXML
    private void ChooseLanguage() {
        ResetTranslation();
    }
    @FXML
    private void MouseClick() {
        if (!inside) HideMenuBar();
    }
    @FXML
    public void inputImage() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.bmp"));
        File f = fileChooser.showOpenDialog(null);
        if (f != null) {
            image.setImage(new javafx.scene.image.Image(f.toURI().toString()));
            imageBytes = Files.readAllBytes(Path.of(f.toURI()));
            ResetTranslation();
            double scaleX = image.getImage().getWidth() / image.getFitWidth();
            double scaleY = image.getImage().getHeight() / image.getFitHeight();
            scale = Math.max(scaleX, scaleY);
        }
    }
    @FXML
    private void inputImageLink() throws IOException, URISyntaxException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter link to image:");
        dialog.showAndWait();
        String path = dialog.getEditor().getText();
        UrlValidator validator = new UrlValidator();
        if (validator.isValid(path)) {
            image.setImage(new javafx.scene.image.Image(path));
            if (!image.getImage().isError()) {
                imageBytes = new URI(path).toURL().openConnection().getInputStream().readAllBytes();
                ResetTranslation();
                double scaleX = image.getImage().getWidth() / image.getFitWidth();
                double scaleY = image.getImage().getHeight() / image.getFitHeight();
                scale = Math.max(scaleX, scaleY);
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR,"Something when wrong. Please check your internet connection and check if the file type is supported.");
                alert.setHeaderText("Unable to open.");
                alert.showAndWait();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Please enter a valid URL.");
            alert.setHeaderText("Invalid URL.");
            alert.showAndWait();
        }
    }
    @FXML
    private void DragDroppedImage(DragEvent event) throws IOException {
        Dragboard dragboard = event.getDragboard();
        if (!dragboard.hasFiles()) return;
        File file = dragboard.getFiles().get(0);
        image.setImage(new Image(file.toURI().toString()));
        imageBytes = Files.readAllBytes(Path.of(file.toURI()));
        if (image.getImage().isError()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Invalid image.");
            alert.setHeaderText("Please choose a valid image file.");
            alert.showAndWait();
        }
        else {
            ResetTranslation();
            double scaleX = image.getImage().getWidth() / image.getFitWidth();
            double scaleY = image.getImage().getHeight() / image.getFitHeight();
            scale = Math.max(scaleX, scaleY);
        }
    }
    @FXML
    private void showTranslation() {
        for (Label t : SourceText) {
            t.setVisible(!t.isVisible());
        }
    }
    @FXML
    public void translateImage() {
        if (translated || image.getImage() == null) return;
        if (TranslateLanguage.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Please choose a target language to translate to.");
            alert.setHeaderText("No target language chosen.");
            alert.showAndWait();
            return;
        }
        new Thread(() -> {
            Platform.runLater(this::Loading);
            boolean connected = true;
            try {
                ImageTranslate.detectText(imageBytes);
            } catch (Exception e) {
                connected = false;
            }
            if (!connected) {
                StopLoading();
                Error();
                return;
            }
            ShowTranslateButton.setVisible(true);
            ShowTranslateButton.setSelected(true);
            translated = true;
            SourceText.clear();
            for (Pair<String, BoundingPoly> wordImg : ImageTranslate.textAnnotations) {
                String word = wordImg.getKey();
                BoundingPoly value = wordImg.getValue();
                String translation = null;
                try {
                    translation = TranslateAPI.googleTranslate("auto",TranslateAPI.langMap.get(TranslateLanguage.getValue()),word);
                } catch (IOException | URISyntaxException ignored) {}
                if (translation == null) {
                    continue;
                }
                Label t = new Label(translation);
                t.setOnMousePressed(event -> ((Node)event.getSource()).toFront());
                t.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                            if(mouseEvent.getClickCount() == 2){
                                ClipboardContent content = new ClipboardContent();
                                content.putString(((Label)mouseEvent.getSource()).getText());
                                Clipboard.getSystemClipboard().setContent(content);
                                NotifyClipboard();
                            }
                        }
                    }
                });
                t.setStyle("-fx-background-color: white; -fx-border-width: 0.5; -fx-border-color: red;");
                t = SetSize(t,value);
                t.setFont(new Font(FontSize(translation,t.getMaxWidth(),t.getMaxHeight())));
                SourceText.add(t);
            }
            Platform.runLater(() -> {
                for (Label t : SourceText) {
                    imagePane.getChildren().add(t);
                    FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
                    double charWidth = fontLoader.getCharWidth(t.getText().charAt(0), t.getFont());
                    double textWidth = charWidth * t.getText().length();
                    if (textWidth > t.getMaxWidth()) {
                        t.setMinWidth(t.getMinWidth() + 15);
                        t.setMaxWidth(t.getMaxWidth() + 15);
                    }
                    else {
                        t.setMinHeight(t.getMinHeight() + 15);
                        t.setMaxHeight(t.getMaxHeight() + 15);
                    }
                    StopLoading();
                }
            });
        }).start();
    }
    @FXML
    private void Error() {
        TranslateTransition translateIn = new TranslateTransition(Duration.millis(500), ErrorLabel);
        translateIn.setToX(0);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        translateIn.setOnFinished(e -> pause.playFromStart());
        pause.setOnFinished(e -> {
            TranslateTransition translateOut = new TranslateTransition(Duration.millis(500), ErrorLabel);
            translateOut.setByX(-ErrorLabel.getWidth());
            translateOut.play();
        });
        translateIn.play();
    }
    @FXML
    private void NotifyClipboard() {
        TranslateTransition translateIn = new TranslateTransition(Duration.millis(500), ClipboardLabel);
        translateIn.setToX(0);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        translateIn.setOnFinished(e -> pause.playFromStart());
        pause.setOnFinished(e -> {
            TranslateTransition translateOut = new TranslateTransition(Duration.millis(500), ClipboardLabel);
            translateOut.setByX(-ErrorLabel.getWidth());
            translateOut.play();
        });
        translateIn.play();
    }
    @FXML
    private void Loading() {
        loading = new ImageView(loadImage);
        loading.setFitHeight(60);
        loading.setFitWidth(60);
        loading.setLayoutX(445);
        loading.setLayoutY(55);
        Root.getChildren().add(loading);
        Root.setDisable(true);
    }
    @FXML
    private void StopLoading() {
        Root.getChildren().remove(loading);
        Root.setDisable(false);
    }
    private double FontSize(String translation,double width, double height) {
        double fontScale = 1;
        if (Objects.equals(TranslateLanguage.getValue(), "Chinese") || Objects.equals(TranslateLanguage.getValue(), "Japanese") || Objects.equals(TranslateLanguage.getValue(), "Korean")) {
            fontScale = 1.7;
        }
        return Math.round(sqrt((width*height)/translation.length()/fontScale));
    }
    private Label SetSize(Label t,BoundingPoly value) {
        int offset = 0;
        if (Objects.equals(TranslateLanguage.getValue(), "Chinese") || Objects.equals(TranslateLanguage.getValue(), "Japanese") || Objects.equals(TranslateLanguage.getValue(), "Korean")) {
            offset = 7;
        }
        t.setLayoutX(value.getVertices(0).getX() / scale);
        t.setLayoutY(value.getVertices(0).getY() / scale - offset);
        double width = (value.getVertices(1).getX() - value.getVertices(0).getX()) / scale;
        double height = (value.getVertices(3).getY() - value.getVertices(0).getY()) / scale + offset;
        t.setMinWidth(width);
        t.setMinHeight(height);
        t.setMaxWidth(width);
        t.setMaxHeight(height);
        t.setWrapText(true);
        t.setAlignment(Pos.TOP_LEFT);
        return t;
    }
    @FXML
    private void ResetTranslation() {
        translated = false;
        for (Label t : SourceText) {
            imagePane.getChildren().remove(t);
        }
        SourceText.clear();
        ShowTranslateButton.setVisible(false);
        ShowTranslateButton.setSelected(false);
    }
}
