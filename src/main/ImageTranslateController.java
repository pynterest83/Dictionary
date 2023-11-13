package main;

import base.ImageTranslate;
import base.TranslateAPI;
import com.google.cloud.vision.v1.BoundingPoly;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

public class ImageTranslateController extends MainController {
    private static String imagePath;
    private static String outputImagePath;
    private static double scale;
    @FXML
    private static ArrayList<Text> SourceText;
    private static ArrayList<Text> TranslatedText;
    @FXML
    private ImageView input;
    @FXML
    private ImageView output;
    @FXML
    private Pane inputPane;
    @FXML
    private Pane outputPane;

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
        input.setImage(new javafx.scene.image.Image(f.toURI().toString()));
        outputImagePath = f.toURI().toString();
        imagePath = f.getAbsolutePath();
        scale = input.getImage().getWidth() / input.getFitWidth();
    }

    @FXML
    public void translateImage() {
        SourceText = new ArrayList<>();

        new Thread(() -> {
            try {
                ImageTranslate.detectText(imagePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ImageTranslate.textAnnotations.sort((o1, o2) -> {
                if (o1.getKey().length() > o2.getKey().length()) return -1;
                if (o1.getKey().length() < o2.getKey().length()) return 1;
                return 0;
            });
            ImageTranslate.textAnnotations.remove(0);

            for (Pair<String, BoundingPoly> wordImg : ImageTranslate.textAnnotations) {
                String word = wordImg.getKey();
                BoundingPoly value = wordImg.getValue();
                Text t = new Text(word);
                t.setLayoutX(value.getVertices(3).getX() / scale);
                t.setLayoutY(value.getVertices(3).getY() / scale);
                double width = value.getVertices(1).getX() / scale - value.getVertices(0).getX() / scale;
                double fontSize = t.getFont().getSize();
                double curWidth = t.getLayoutBounds().getWidth();
                t.setFont(Font.font(fontSize * width / curWidth));
                t.setFocusTraversable(true);

                SourceText.add(t);
            }
            Platform.runLater(() -> {
                for (Text t : SourceText) {
                    inputPane.getChildren().add(t);
                }
            });
        }).start();
    }
}
