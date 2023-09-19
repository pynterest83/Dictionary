package main;

import base.DictionaryCommandline;
import base.DictionaryManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class RunApplication extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(RunApplication.class.getResource("search.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 895, 559);
        stage.setTitle("Hello World!");
        stage.setScene(scene);
        stage.show();
        DictionaryManager.defaultFile();
    }
}
