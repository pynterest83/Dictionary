package main;

import base.CompleteSentence;
import base.DictionaryManager;
import base.TranslateAPI;
import base.Wordle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
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
    @Override
    public void start(Stage stage) throws Exception {
        LoadData();
        Scene scene = new Scene(FXML_scenes.get("main.fxml"), 895, 559);
        stage.setTitle("Dictionary");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
    public static void LoadData() throws IOException {
        DictionaryManager.defaultFile();
        CompleteSentence.LoadQuestionsAndAnswers();
        Wordle.LoadWordleList();
        TranslateAPI.addDefault();
        File dir = new File("src/main");
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.getName().endsWith((".fxml"))) {
                FXML_scenes.put(file.getName(), FXMLLoader.load(Paths.get("src/main/" + file.getName()).toUri().toURL()));
            }
        }
    }
}