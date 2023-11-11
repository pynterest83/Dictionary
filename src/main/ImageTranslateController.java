package main;

import base.ImageTranslate;
import base.TranslateAPI;
import com.google.cloud.vision.v1.BoundingPoly;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ImageTranslateController extends MainController {
    private static String imagePath;
    private static String outputImagePath;
    private static double scale;
    private static Label TranslatedLabel;
    @FXML
    private ImageView input;
    @FXML
    private ImageView output;
    @FXML
    private Pane outputPane;
    @FXML
    public void inputImage(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File f = fileChooser.showOpenDialog(null);
        input.setImage(new javafx.scene.image.Image(f.toURI().toString()));
        outputImagePath = f.toURI().toString();
        imagePath = f.getAbsolutePath();
        scale = input.getImage().getWidth() / input.getFitWidth();
        System.out.println(scale);
    }

    @FXML
    public void translateImage() {
        new Thread(() -> {
            try {
                ImageTranslate.detectText(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            outputPane.getChildren().remove(TranslatedLabel);
            String word = ImageTranslate.textAnnotations.firstKey();
            BoundingPoly value = ImageTranslate.textAnnotations.get(word);
            try {
                String translate = TranslateAPI.googleTranslate("auto","vi",word);
                TranslatedLabel = new Label(translate);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            TranslatedLabel.setStyle("-fx-text-fill:black; -fx-background-color: white;");
            TranslatedLabel.setLayoutX(value.getVertices(3).getX() / scale);
            TranslatedLabel.setLayoutY(value.getVertices(3).getY() / scale);
            double width = value.getVertices(1).getX() / scale - value.getVertices(0).getX() / scale;
            double fontSize = TranslatedLabel.getFont().getSize();
            double curWidth = TranslatedLabel.getLayoutBounds().getWidth();
            TranslatedLabel.setFont(Font.font(fontSize * width / curWidth));
            Platform.runLater(() -> {
                outputPane.getChildren().add(TranslatedLabel);
                output.setImage(new Image(outputImagePath));
            });
        }).start();
    }
}
