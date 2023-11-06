package main;

import base.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class RunApplication extends Application {
    static HashMap <String,Parent> FXML_scenes = new HashMap<>();
    public static void main(String[] args) throws Exception {
        launch(args);
    }
    public static void SwitchScenes(Stage stage,String sceneName) {
        stage.getScene().setRoot(FXML_scenes.get(sceneName));
        stage.show();
    }
    public static void Reload(String sceneName) throws IOException {
        FXML_scenes.replace(sceneName, FXMLLoader.load(Paths.get("src/scene/"+sceneName).toUri().toURL()));
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXML_scenes.put("main.fxml",FXMLLoader.load(Paths.get("src/scene/main.fxml").toUri().toURL()));
        Scene scene = new Scene(FXML_scenes.get("main.fxml"), 950, 700);
        stage.getIcons().add(new Image(Paths.get("src/style/media/dictionary.png").toUri().toURL().toString()));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        AnchorPane root = (AnchorPane)scene.getRoot();
        ImageView loading = new ImageView(Paths.get("src/style/media/loading2.gif").toUri().toURL().toString());
        loading.setFitHeight(200);
        loading.setFitWidth(200);
        loading.setLayoutX(root.getWidth()/2 - 100);
        loading.setLayoutY(root.getHeight()/2 - 100);
        LoadScenes();
        new Thread(() -> {
            Platform.runLater(() -> {
                root.setDisable(true);
                root.getChildren().add(loading);
            });
            try {
                LoadData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Platform.runLater(() -> {
                try {
                    LoadScenes();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                root.setDisable(false);
                root.getChildren().remove(loading);
            });
        }).start();
    }
    public static void LoadData() throws IOException {
        DictionaryManager.defaultFile();
        CompleteSentence.LoadQuestionsAndAnswers();
        Wordle.LoadWordleList();
        TranslateAPI.addDefault();
        SpeechRecognizer.prepare();
    }
    public static void LoadScenes() throws IOException {
        File dir = new File("src/scene");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().equals("main.fxml")) {
                FXML_scenes.put(file.getName(), FXMLLoader.load(Paths.get("src/scene/" + file.getName()).toUri().toURL()));
            }
        }
    }
}