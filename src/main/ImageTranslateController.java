package main;

import base.ImageTranslate;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageTranslateController extends MainController {
    private static String imagePath;
    private static String outputImagePath;
    private static ArrayList<Polygon> polygons;
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
        polygons = new ArrayList<>();

        new Thread(() -> {
            try {
                ImageTranslate.detectText(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String word : ImageTranslate.textAnnotations.keySet()) {
                System.out.println(word);
                BoundingPoly value = ImageTranslate.textAnnotations.get(word);
                Polygon p = new Polygon();
                p.setStyle("-fx-fill: transparent; -fx-stroke: red; -fx-stroke-width: 2;");
                for (Vertex vertex : value.getVerticesList()) {
                    p.getPoints().addAll(vertex.getX() / scale, vertex.getY() / scale);
                }
                polygons.add(p);
            }
            Platform.runLater(() -> {
                outputPane.getChildren().addAll(polygons);
                output.setImage(new javafx.scene.image.Image(outputImagePath));
            });
        }).start();
    }
}
