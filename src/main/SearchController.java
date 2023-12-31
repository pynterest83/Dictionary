package main;

import animatefx.animation.SlideInDown;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideInRight;
import animatefx.animation.SlideOutRight;
import base.*;
import controls.GeneralControls;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchController extends GeneralControls {
    @FXML
    private AnchorPane root;
    String[] suggestions;
    @FXML
    private TextField searchBar;
    @FXML
    private Button SpeakButton;
    @FXML
    private Button recordButton;
    @FXML
    private Button addLearningButton;
    @FXML
    private Button removeLearningButton;
    @FXML
    private Button addSynonymsButton;
    @FXML
    private WebView wordExplain;
    @FXML
    private WebView wordHeader;
    @FXML
    private VBox wordSynonyms;
    @FXML
    private AnchorPane addNote;
    @FXML
    private TextField Notes;
    @FXML
    private TextField Syms;
    @FXML
    private ScrollPane synonymPane;
    @FXML
    private Button en_vi_dict;
    @FXML
    private Button vi_en_dict;
    @FXML
    private LineChart<Integer,Double> UsageOverTime;
    @FXML
    private NumberAxis YearAxis;
    @FXML
    private Label graphFailed;
    @FXML
    private Pane ToolPane;
    @FXML
    private TitledPane FileTool;
    @FXML
    private TitledPane DictionaryTool;
    @FXML
    private TitledPane HistoryTool;
    @FXML
    private WebView EtymologyPane;
    @FXML
    private Button ToolbarMenuButton;
    @FXML
    private Label ErrorLabel;
    @FXML
    private Image loadImage = new Image(Paths.get("src/style/media/loading.gif").toUri().toURL().toString());
    private String searched = null;
    private String type_Dict = "EN_VI";
    private String[] history;
    private boolean ToolbarOpen = false;
    private boolean insideToolbar = false;
    public SearchController() throws MalformedURLException {
    }

    @FXML
    private void initialize() {
        loadOtherScences();
        FileTool.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                TranslateTransition moveFirst = new TranslateTransition(Duration.seconds(0.38), DictionaryTool);
                moveFirst.setToY(0);
                TranslateTransition moveSecond = new TranslateTransition(Duration.seconds(0.38), HistoryTool);
                if (DictionaryTool.isExpanded()) {
                    moveSecond.setToY(0);
                }
                else moveSecond.setToY(-83);
                moveFirst.play();
                moveSecond.play();
            }
            else {
                TranslateTransition moveFirst = new TranslateTransition(Duration.seconds(0.25),DictionaryTool);
                moveFirst.setToY(-83);
                TranslateTransition moveSecond = new TranslateTransition(Duration.seconds(0.25),HistoryTool);
                if (DictionaryTool.isExpanded()) {
                    moveSecond.setToY(-83);
                }
                else moveSecond.setToY(-166);
                moveFirst.play();
                moveSecond.play();
            }
        });
        DictionaryTool.expandedProperty().addListener((observableValue, oldValue, newValue) -> {
            TranslateTransition move;
            if (newValue) {
                move = new TranslateTransition(Duration.seconds(0.38), HistoryTool);
                if (FileTool.isExpanded()) {
                    move.setToY(0);
                }
                else move.setToY(-83);
            }
            else {
                move = new TranslateTransition(Duration.seconds(0.25), HistoryTool);
                if (FileTool.isExpanded()) {
                    move.setToY(-83);
                }
                else move.setToY(-166);
            }
            move.play();
        });
        PrepareMenu();
        AutoCompletionBinding<String> completion = TextFields.bindAutoCompletion(searchBar, input -> {
            if (searchBar.getText().length() <= 1) {
                String[] reversedHistory = new String[history.length];
                for (int i = 0; i < history.length; i++) {
                    reversedHistory[i] = history[history.length - i - 1];
                }
                return Stream.of(reversedHistory).collect(Collectors.toList());
            }
            else {
                return Stream.of(suggestions).filter(s -> s.contains(searchBar.getText())).collect(Collectors.toList());
            }
        });
        completion.setDelay(0);
        Notes.focusedProperty().addListener((ObservableValue <? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
            if (oldValue && !newValue) {
                Notes.setVisible(false);
                addNote.setVisible(false);
            }
        });
        Syms.focusedProperty().addListener((ObservableValue <? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
            if (oldValue && !newValue) {
                Syms.setVisible(false);
                addNote.setVisible(false);
            }
        });
        synonymPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        synonymPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        wordExplain.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) change -> {
            Set<Node> deadSeaScrolls = wordExplain.lookupAll(".scroll-bar");
            for (Node scroll : deadSeaScrolls) {
                scroll.setVisible(false);
            }
        });
        wordHeader.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) change -> {
            Set<Node> deadSeaScrolls = wordHeader.lookupAll(".scroll-bar");
            for (Node scroll : deadSeaScrolls) {
                scroll.setVisible(false);
            }
        });
        EtymologyPane.getChildrenUnmodifiable().addListener((ListChangeListener<Node>) change -> {
            Set<Node> deadSeaScrolls = EtymologyPane.lookupAll(".scroll-bar");
            for (Node scroll : deadSeaScrolls) {
                scroll.setVisible(false);
            }
        });
        getHistory();
        UsageOverTime.setCreateSymbols(false);
        NumberFormat format = new DecimalFormat("####");
        YearAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return format.format(number);
            }

            @Override
            public Number fromString(String s) {
                try {
                    return format.parse(s);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        wordSynonyms.setSpacing(5);
        SpeakButton.setVisible(false);
        addLearningButton.setVisible(false);
        removeLearningButton.setVisible(false);
        addNote.setVisible(false);
        addSynonymsButton.setVisible(false);
        en_vi_dict.setVisible(true);
        vi_en_dict.setVisible(false);
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 2) {
                try {
                    UserInput();
                } catch (Exception ignored) {}
            }
        });
        searchBar.focusedProperty().addListener((ObservableValue <? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
            if (!oldValue && newValue) {
                getHistory();
                searchBar.setText("s");
                searchBar.setText("");
            }
        });
        ToolPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> insideToolbar = true);
        ToolPane.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> insideToolbar = false);
        if (!RunApplication.micAvailable) {
            recordButton.getStyleClass().clear();
            recordButton.getStyleClass().add("mic-unavailable");
            recordButton.setDisable(true);
        }
        String path = Paths.get("src/style/webviews.css").toUri().toString();
        wordHeader.getEngine().setUserStyleSheetLocation(path);
        wordExplain.getEngine().setUserStyleSheetLocation(path);
        EtymologyPane.getEngine().setUserStyleSheetLocation(path);
    }
    @FXML
    protected void MouseClick() {
        if (!insideToolbar) HideToolbar();
    }
    @FXML
    protected void HideToolbar() {
        if (ToolbarOpen) ToolbarClick();
    }
    @FXML
    protected void ToolbarClick() {
        ToolbarOpen = !ToolbarOpen;
        insideToolbar = false;
        if (ToolbarOpen) {
            ToolPane.setVisible(true);
            ToolPane.setDisable(true);
            SlideInRight inRight = new SlideInRight(ToolPane);
            inRight.setOnFinished(e -> {
                ToolPane.setDisable(false);
                ToolPane.requestFocus();
            });
            inRight.setSpeed(3);
            inRight.play();
        }
        else {
            ToolPane.setDisable(true);
            SlideOutRight outRight= new SlideOutRight(ToolPane);
            outRight.setOnFinished(e -> ToolPane.setVisible(ToolbarOpen));
            outRight.setSpeed(3);
            outRight.play();
        }
    }
    @FXML
    protected void onExportToFileClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Export to File");
        alert.setHeaderText("Choose file to export?");
        alert.setContentText("Choose File");
        ButtonType buttonTypeOne = new ButtonType("Export to EN_VI.txt");
        ButtonType buttonTypeTwo = new ButtonType("Export to VI_EN.txt");
        ButtonType buttonTypeCancel = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){
            DictionaryManager.exportToFile("EN_VI");
        } else if (result.get() == buttonTypeTwo) {
            DictionaryManager.exportToFile("VI_EN");
        }
        else {
            alert.close();
        }
    }
    @FXML
    protected void onImportFromFileClick(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File f = fc.showOpenDialog(null);
        if (f != null) {
            ArrayList<Word> repeated = new ArrayList<>();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Import from File");
            alert.setHeaderText("Choose file to import?");
            alert.setContentText("Choose File");
            ButtonType buttonTypeOne = new ButtonType("Import to EN_VI");
            ButtonType buttonTypeTwo = new ButtonType("Import to VI_EN");
            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne){
                repeated = DictionaryManager.importFromFile(f.getAbsolutePath(), "EN_VI");
            } else if (result.get() == buttonTypeTwo) {
                repeated = DictionaryManager.importFromFile(f.getAbsolutePath(), "VI_EN");
            }
            if (!repeated.isEmpty()) {
                for (Word word : repeated) {
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setTitle("Remove Repeated Words");
                    alert1.setHeaderText(word.getWordTarget() + " is already in the dictionary.");
                    alert1.setContentText("Do you want to replace " + word.getWordTarget() + "?");
                    Optional<ButtonType> result1 = alert.showAndWait();

                    if (result1.get() == ButtonType.OK) {
                        int position = DictionaryManager.binSearch(word.getWordTarget());
                        DictionaryManager.curDict.remove(position);
                        DictionaryManager.curDict.add(word);
                        Collections.sort(DictionaryManager.curDict);
                    }
                }
            }
        }
    }
    @FXML
    protected void onClickAdd() {
        searchBar.clear();
        wordHeader.getEngine().loadContent("");
        wordExplain.getEngine().loadContent("");
        EtymologyPane.getEngine().loadContent("");
        wordSynonyms.getChildren().clear();
        UsageOverTime.getData().clear();
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        ToolbarClick();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() {
        searchBar.clear();
        wordHeader.getEngine().loadContent("");
        wordExplain.getEngine().loadContent("");
        EtymologyPane.getEngine().loadContent("");
        wordSynonyms.getChildren().clear();
        UsageOverTime.getData().clear();
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        ToolbarClick();
        RunApplication.SwitchScenes(stage,"modify.fxml");
    }
    @FXML
    public void onClickClearENHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText("Are you sure you want to clear history?");
        alert.setContentText("Choose your option.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            DictionaryManager.EN_History.clear();
            DictionaryManager.addHistory("", "EN_VI");
        }
    }

    @FXML
    public void onClickClearVIHistory() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear History");
        alert.setHeaderText("Are you sure you want to clear history?");
        alert.setContentText("Choose your option.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            DictionaryManager.VI_History.clear();
            DictionaryManager.addHistory("", "VI_EN");
        }
    }
    @FXML
    protected void LoadEtymology() {
        ImageView loading = new ImageView(loadImage);
        loading.setFitWidth(60);
        loading.setFitHeight(60);
        loading.setLayoutX(755);
        loading.setLayoutY(460);
        AnchorPane parent = (AnchorPane) UsageOverTime.getParent();
        new Thread(() -> {
            Platform.runLater(() -> {
                EtymologyPane.getEngine().loadContent("");
                parent.getChildren().add(loading);
            });
            String response;
            try {
                response = Etymology.etymologyLookup(searched, Objects.equals(type_Dict, "EN_VI") ? "English" : "Vietnamese");
            } catch (IOException | URISyntaxException e) {
                response = "Not available.";
            }
            String finalResponse = response;
            Platform.runLater(() -> {
                parent.getChildren().remove(loading);
                EtymologyPane.getEngine().loadContent(finalResponse,"text/html");
            });
        }).start();
    }
    @FXML
    protected void LoadGraph() {
        UsageOverTime.getData().clear();
        ImageView loading = new ImageView(loadImage);
        loading.setFitWidth(60);
        loading.setFitHeight(60);
        loading.setLayoutX(360);
        loading.setLayoutY(460);
        AnchorPane parent = (AnchorPane) UsageOverTime.getParent();
        new Thread(() -> {
            boolean success = true;
            Platform.runLater(() -> parent.getChildren().add(loading));
            XYChart.Series<Integer,Double> data = new XYChart.Series<>();
            String requestString = searched.replace(' ','+');
            double[] wordData = UsageGraph.graphData(requestString);
            if (type_Dict.equals("VI_EN") || wordData == null || wordData.length == 0) {
                success = false;
            }
            else {
                for (int i = 0; i < wordData.length; i++) {
                    data.getData().add(new XYChart.Data<>(i + 1800, 100 * wordData[i]));
                }
            }
            boolean succeed = success;
            Platform.runLater(() -> {
                parent.getChildren().remove(loading);
                if (succeed) UsageOverTime.getData().add(data);
                else {
                    if (type_Dict.equals("VI_EN")) graphFailed.setText("This language is not supported");
                    else if (wordData == null) graphFailed.setText("No internet connection");
                    else graphFailed.setText("Stats not available");
                    graphFailed.setVisible(true);
                }
            });
        }).start();
    }
    @FXML
    protected void CreateSearchHyperlink(String word) {
        if (!Objects.equals(DictionaryManager.dictionaryLookup(word, type_Dict), "Word not found.")) {
            Hyperlink wordLink = new Hyperlink(word);
            wordLink.setFont(new Font(16));
            wordLink.setOnAction(actionEvent -> {
                searchBar.setText(wordLink.getText());
                try {
                    enterSearch();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            wordSynonyms.getChildren().add(wordLink);
        }
        else {
            Label wordLabel = new Label(" "+word);
            wordLabel.setFont(new Font(16));
            wordSynonyms.getChildren().add(wordLabel);
        }
    }
    @FXML
    protected void LoadSynonym() {
        HideMenuBar();
        addSynonymsButton.setVisible(true);
        if (DictionaryManager.symDict.get(searched) == null) {
            Label label = new Label(" No synonym found.");
            label.setFont(new Font(16));
            wordSynonyms.getChildren().add(label);
            return;
        }
        if (wordSynonyms.getChildren().isEmpty()) {
            ArrayList<String> synonyms = DictionaryManager.symDict.get(searched);
            for (String s : synonyms) {
                SlideInDown slideInDown = new SlideInDown(wordSynonyms);
                slideInDown.setSpeed(1);
                slideInDown.play();
                CreateSearchHyperlink(s);
            }
        }
    }
    protected void UserInput() {
        new Thread(() -> suggestions = DictionaryManager.dictionarySearcher(searchBar.getText(), type_Dict)).start();
    }
    protected void getHistory() {
        new Thread(() -> history = DictionaryManager.getHistory(type_Dict).toArray(new String[0])).start();
    }
    @FXML
    protected void enterSearch() {
        HideMenuBar();
        if (graphFailed.isVisible()) graphFailed.setVisible(false);
        UsageOverTime.getData().clear();
        wordSynonyms.getChildren().clear();
        addNote.setVisible(false);
        EtymologyPane.getEngine().loadContent("");
        if (!Objects.equals(DictionaryManager.dictionaryLookup(searchBar.getText(), type_Dict), "Word not found.")) {
            searched = searchBar.getText();
            DisplayWordExplain();
            LoadSynonym();
            SpeakButton.requestFocus();
            DictionaryManager.addHistory(searched, type_Dict);
            history = DictionaryManager.History.toArray(new String[0]);
            LoadGraph();
            LoadEtymology();
        }
        else {
            SpeakButton.setVisible(false);
            addLearningButton.setVisible(false);
            wordHeader.getEngine().loadContent("<html><header><h2>Word Not Found !</h2></header></html>", "text/html");
            wordExplain.getEngine().loadContent("", "text/html");
        }
    }
    @FXML
    private void DisplayWordExplain() {
        addLearningButton.setVisible(false);
        removeLearningButton.setVisible(false);
        String header = "<html><header><h2>"+ searchBar.getText() +"</h2></header></html>";
        String explain = DictionaryManager.dictionaryLookup(searchBar.getText(), type_Dict);
        if(explain.contains("contenteditable=\"true\"")){
            explain=explain.replace("contenteditable=\"true\"", "contenteditable=\"false\"");
        }
        wordHeader.getEngine().loadContent(header, "text/html");
        wordExplain.getEngine().loadContent(explain, "text/html");
        SlideInLeft slideInLeft = new SlideInLeft(wordExplain);
        SlideInLeft slideInLeft1 = new SlideInLeft(wordHeader);
        slideInLeft1.setSpeed(1);
        slideInLeft.setSpeed(1);
        slideInLeft.play();
        slideInLeft1.play();
        SpeakButton.setVisible(true);
        boolean isLearning = false;
        for (int i = 0; i < DictionaryManager.learningDict.size(); i++) {
            if (DictionaryManager.learningDict.get(i).getWordTarget().equals(searched)) {
                isLearning = true;
                break;
            }
        }
        if (isLearning) {
            addLearningButton.setVisible(true);
        }
        else {
            removeLearningButton.setVisible(true);
        }
    }
    @FXML
    private void onClickSpeakButton() {
        if (type_Dict.equals("EN_VI")) TranslateAPI.speakAudio(searched,"English");
        else TranslateAPI.speakAudio(searched,"Vietnamese");
    }
    @FXML
    public void onClickAddLearning() throws IOException {
        if (removeLearningButton.isVisible()) {
            addNote.setVisible(true);
            Syms.setVisible(false);
            Notes.setVisible(true);
            Notes.requestFocus();
        }
        else {
            DictionaryManager.removeLearningWord(searched);
            RunApplication.Reload("learning.fxml");
            removeLearningButton.setVisible(true);
            addLearningButton.setVisible(false);
        }

    }
    @FXML
    public void onClickRecording() {
        if (type_Dict.equals("EN_VI")) SpeechRecognition.changeLanguage("en");
        else SpeechRecognition.changeLanguage("vi");
        if (!SpeechRecognition.isListening) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    recordButton.getStyleClass().clear();
                    recordButton.getStyleClass().add("recording-button");
                });
                try {
                    SpeechRecognition.recordFor(4000);
                } catch (LineUnavailableException | IOException ignored) {}
                Platform.runLater(()->{
                    recordButton.getStyleClass().clear();
                    recordButton.getStyleClass().add("micload-button");
                    recordButton.setDisable(true);
                });
                try {
                    SpeechRecognition.sendRequest();
                } catch(Exception ignored) {}
                Platform.runLater(() -> {
                    recordButton.getStyleClass().clear();
                    recordButton.getStyleClass().add("mic-button");
                    recordButton.setDisable(false);
                    if (!SpeechRecognition.alternatives.isEmpty()) {
                        searchBar.setText(SpeechRecognition.alternatives.get(0));
                        try {
                            enterSearch();
                        } catch (Exception ignored) {}
                    } else {
                        TranslateTransition translateIn = new TranslateTransition(Duration.millis(500), ErrorLabel);
                        translateIn.setToX(0);
                        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
                        translateIn.setOnFinished(e -> pause.playFromStart());
                        pause.setOnFinished(e -> {
                            TranslateTransition translateOut = new TranslateTransition(Duration.millis(500), ErrorLabel);
                            translateOut.setByX(-ErrorLabel.getWidth());
                            translateOut.play();
                        });
                        translateIn.play();
                    }
                });
            }).start();
        }
        else {
            SpeechRecognition.isListening = false;
        }
    }
    @FXML
    public void addDescription() throws IOException {
        DictionaryManager.addLearning(searched, Notes.getText(), type_Dict);
        RunApplication.Reload("learning.fxml");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Added");
        alert.setHeaderText(null);
        alert.setContentText("Added to learning list");
        alert.showAndWait();
        addNote.setVisible(false);

        removeLearningButton.setVisible(false);
        addLearningButton.setVisible(true);
    }
    @FXML
    public void addSyms() {
        String[] sym = Syms.getText().split(",");
        ArrayList<String> synonyms = new ArrayList<>(Arrays.asList(sym));
        if (DictionaryManager.symDict.get(searched) != null) {
            synonyms.addAll(DictionaryManager.symDict.get(searched));
        }
        DictionaryManager.addSynonyms(searched, synonyms);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Added");
        alert.setHeaderText(null);
        alert.setContentText("Added to synonyms list");
        alert.showAndWait();
        Syms.setText("");
        Syms.setVisible(false);
        addNote.setVisible(false);
        wordSynonyms.getChildren().clear();
        LoadSynonym();
    }
    @FXML
    public void onClickAddSynonyms() {
        addNote.setVisible(true);
        Syms.setVisible(true);
        Notes.setVisible(false);
        Syms.requestFocus();
    }
    @FXML
    public void changeDict() {
        if (type_Dict.equals("EN_VI")) {
            type_Dict = "VI_EN";
            en_vi_dict.setVisible(false);
            vi_en_dict.setVisible(true);
            SpeechRecognition.changeLanguage("vi");
        }
        else {
            type_Dict = "EN_VI";
            en_vi_dict.setVisible(true);
            vi_en_dict.setVisible(false);
            SpeechRecognition.changeLanguage("en");
        }
        getHistory();
    }
}
