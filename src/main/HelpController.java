package main;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.util.Duration;

public class HelpController extends MainController {
    @FXML
    private TitledPane Q1;
    @FXML
    private TitledPane Q2;
    @FXML
    private TitledPane Q3;
    @FXML
    private TitledPane Q4;
    @FXML
    private TitledPane Q5;
    @FXML
    private void initialize() {
        loadOtherScences();
        Q1.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                TranslateTransition tt = new TranslateTransition(Duration.millis(380), Q1);
                if (Q2.isExpanded()) tt.setToY(-Q1.getHeight() + 5);
                else tt.setToY(-Q1.getHeight() + 40);
                tt.play();
            }
            else {
                TranslateTransition tt = new TranslateTransition(Duration.millis(380), Q1);
                if (Q2.isExpanded()) tt.setToY(-40);
                else tt.setToY(0);
                tt.play();
            }
        });
        Q2.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(380), Q2);
                tt1.setToY(-Q2.getHeight() + 40);
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(380), Q1);
                if (!Q1.isExpanded()) tt2.setToY(-35);
                else tt2.setToY(-Q1.getHeight() + 10);
                tt1.play();
                tt2.play();
            }
            else {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(380), Q2);
                tt1.setToY(0);
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(380), Q1);
                if (!Q1.isExpanded()) tt2.setToY(0);
                else tt2.setToY(-Q1.getHeight() + 40);
                tt1.play();
                tt2.play();
            }
        });
        Q3.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(380), Q4);
                tt1.setToY(Q3.getHeight() - 40);
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(380), Q5);
                if (Q4.isExpanded()) tt2.setToY(Q3.getHeight() - 40 + Q4.getHeight() - 45);
                else tt2.setToY(Q3.getHeight() - 40);
                tt1.play();
                tt2.play();
            }
            else {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(380), Q4);
                tt1.setToY(0);
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(380), Q5);
                if (Q4.isExpanded()) tt2.setToY(Q4.getHeight() - 45);
                else tt2.setToY(0);
                tt1.play();
                tt2.play();
            }
        });
        Q4.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(380), Q5);
                tt1.setToY(Q4.getHeight() - 40 + Q3.getHeight() - 45);
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(380), Q5);
                tt2.setToY(Q4.getHeight() - 45);
                if (Q3.isExpanded())  tt1.play();
                else tt2.play();
            }
            else {
                TranslateTransition tt1 = new TranslateTransition(Duration.millis(380), Q5);
                tt1.setToY(Q4.getHeight() - 40);
                if (Q3.isExpanded()) tt1.play();
                TranslateTransition tt2 = new TranslateTransition(Duration.millis(380), Q5);
                tt2.setToY(0);
                if (!Q3.isExpanded()) tt2.play();
            }
        });
    }
}
