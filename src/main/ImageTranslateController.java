package main;

import base.ImageTranslate;
import base.TranslateAPI;
import com.google.cloud.vision.v1.BoundingPoly;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Math.sqrt;

public class ImageTranslateController extends MainController {
    private static String imagePath;
    private static String translatePath;
    private static double scale;
    @FXML
    private static ArrayList<Label> SourceText = new ArrayList<>();
    @FXML
    private ImageView image;
    @FXML
    private Pane imagePane;
    @FXML
    private void initialize() {
        loadOtherScences();
        PrepareMenu();
    }
    @FXML
    public void inputImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File f = fileChooser.showOpenDialog(null);
        image.setImage(new javafx.scene.image.Image(f.toURI().toString()));
        if (f.getAbsolutePath().equals(imagePath)) return;
        for (Label t : SourceText) {
            imagePane.getChildren().remove(t);
        }
        SourceText.clear();
        imagePath = f.getAbsolutePath();
        double scaleX = image.getImage().getWidth() / image.getFitWidth();
        double scaleY = image.getImage().getHeight() / image.getFitHeight();
        scale = Math.max(scaleX, scaleY);
    }
    @FXML
    private void showTranslation() {
        for (Label t : SourceText) {
            t.setVisible(!t.isVisible());
        }
    }
    @FXML
    public void translateImage() {
        if (Objects.equals(translatePath, imagePath)) return;
        translatePath = imagePath;
        new Thread(() -> {
            try {
                ImageTranslate.detectText(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
}
