package main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class GameController extends MainController {
    @FXML
    private Button Wordle;
    @FXML
    private Button Babble;
    private static Background BabbleImage = null;
    private static Background WordleImage = null;
    private static final Image BabbleGif = new Image(Paths.get("src/style/media/babbleplay.gif").toUri().toString());
    private static final Image WordleGif = new Image(Paths.get("src/style/media/wordleplay.gif").toUri().toString());
    @FXML
    private void initialize() {
        loadOtherScences();
        PrepareMenu();
        Babble.hoverProperty().addListener((observableValue, oldValue, newValue) -> {
            Babble.getStyleClass().clear();
            if (newValue) {
                if (BabbleImage == null) {
                    BackgroundImage image = Babble.getBackground().getImages().get(0);
                    BabbleImage = new Background(new BackgroundImage(BabbleGif, image.getRepeatX(),image.getRepeatY(),image.getPosition(),image.getSize()));
                }
                Babble.setBackground(BabbleImage);
                Babble.setStyle("-fx-border-color: grey; -fx-border-width: 3; -fx-font-family: 'Roboto Slab', serif;");
            }
            else {
                Babble.setStyle(null);
                Babble.setBackground(null);
                Babble.getStyleClass().add("babble-image");
            }
        });
        Wordle.hoverProperty().addListener((observableValue, oldValue, newValue) -> {
            Wordle.getStyleClass().clear();
            if (newValue) {
                if (WordleImage == null) {
                    BackgroundImage image = Wordle.getBackground().getImages().get(0);
                    WordleImage = new Background(new BackgroundImage(WordleGif, image.getRepeatX(),image.getRepeatY(),image.getPosition(),image.getSize()));
                }
                Wordle.setBackground(WordleImage);
                Wordle.setStyle("-fx-border-color: grey; -fx-border-width: 3; -fx-font-family: 'Roboto Slab', serif;");
            }
            else {
                Wordle.setStyle(null);
                Wordle.setBackground(null);
                Wordle.getStyleClass().add("wordle-image");
            }
        });
    }
    @FXML
    protected void MouseClick() {
        if (!inside) HideMenuBar();
    }
    @FXML
    public void onClickWordle() {
        Stage stage = (Stage) Wordle.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"wordle.fxml");
    }
    @FXML
    public void onClickBabble() {
        Stage stage = (Stage) Babble.getScene().getWindow();
        RunApplication.SwitchScenes(stage,"babblebot.fxml");
    }
}
