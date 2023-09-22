package main;

import base.CompleteSentence;
import base.DictionaryCommandline;
import base.DictionaryManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.nio.file.Paths;

public class RunApplication extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }
    public static void SwitchScenes(Stage stage,String sceneName) throws IOException {
        Parent root = FXMLLoader.load(Paths.get("src/main/"+sceneName).toUri().toURL());
        stage.getScene().setRoot(root);
        stage.show();
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 895, 559);
        stage.setTitle("Hello World!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        DictionaryManager.defaultFile();
        CompleteSentence.LoadQuestionsAndAnswers();
    }
}
