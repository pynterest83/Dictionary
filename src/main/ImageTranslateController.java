package main;

import base.ImageTranslate;
import base.TranslateAPI;
import com.google.cloud.vision.v1.BoundingPoly;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    private static boolean translated = false;
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
            translated = false;
            for (Label t : SourceText) {
                imagePane.getChildren().remove(t);
            }
            SourceText.clear();
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
                imageBytes = ((HttpURLConnection) new URI(path).toURL().openConnection()).getInputStream().readAllBytes();
                translated = false;
                for (Label t : SourceText) {
                    imagePane.getChildren().remove(t);
                }
                SourceText.clear();
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
            translated = false;
            for (Label t : SourceText) {
                imagePane.getChildren().remove(t);
            }
            SourceText.clear();
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
    public void translateImage() throws URISyntaxException {
        if (translated) return;
        new Thread(() -> {
            boolean connected = true;
            try {
                ImageTranslate.detectText(imageBytes);
            } catch (Exception e) {
                connected = false;
            }
            if (!connected) {
                Error();
                return;
            }
            translated = true;
            for (Pair<String, BoundingPoly> wordImg : ImageTranslate.textAnnotations) {
                String word = wordImg.getKey();
                BoundingPoly value = wordImg.getValue();
                String translated = null;
                try {
                    translated = TranslateAPI.googleTranslate("auto","vi",word);
                } catch (IOException | URISyntaxException ignored) {}
                Label t = new Label(translated);
                t.setStyle("-fx-background-color: white; -fx-border-width: 0.5; -fx-border-color: red;");
                t.setLayoutX(value.getVertices(0).getX() / scale);
                t.setLayoutY(value.getVertices(0).getY() / scale);
                double width = (value.getVertices(1).getX() - value.getVertices(0).getX()) / scale;
                double height = (value.getVertices(3).getY() - value.getVertices(0).getY()) / scale;
                t.setMinWidth(width);
                t.setMinHeight(height);
                t.setMaxWidth(width);
                t.setMaxHeight(height);
                t.setWrapText(true);
                t.setTextOverrun(OverrunStyle.CLIP);
                try {
                    t.setFont(Font.loadFont(Paths.get("src/style/fonts/OpenSans-Medium.ttf").toUri().toURL().toString()
                            ,Math.round(sqrt((width*height)/translated.length()/1.2))));
                } catch (MalformedURLException ignored) {}
                SourceText.add(t);
            }
            Platform.runLater(() -> {
                for (Label t : SourceText) {
                    imagePane.getChildren().add(t);
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
}
