package main;

import base.ImageTranslate;
import base.TranslateAPI;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.Vertex;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ImageTranslateController extends MainController {
    private static String imagePath;
    private static String outputImagePath;
    private static ArrayList<Text> texts;
    private static double scale;
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
    }

    @FXML
    public void translateImage() {
        texts = new ArrayList<>();

        new Thread(() -> {
            try {
                ImageTranslate.detectText(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String word : ImageTranslate.textAnnotations.keySet()) {
                //if (word.length() > 10) continue;
                BoundingPoly value = ImageTranslate.textAnnotations.get(word);
                Text text = new Text(word);
                text.setLayoutX(value.getVertices(3).getX() / scale);
                text.setLayoutY(value.getVertices(3).getY() / scale);
                double width = value.getVertices(1).getX() / scale - value.getVertices(0).getX() / scale;
                double fontSize = text.getFont().getSize();
                double curWidth = text.getLayoutBounds().getWidth();
                text.setFont(javafx.scene.text.Font.font(fontSize * width / curWidth));

                texts.add(text);
            }
            Platform.runLater(() -> {
                outputPane.getChildren().addAll(texts);
                output.setImage(new javafx.scene.image.Image(outputImagePath));
            });
        }).start();
    }
}
