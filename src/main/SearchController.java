package main;

import animatefx.animation.SlideInRight;
import animatefx.animation.SlideOutRight;
import base.DictionaryManager;
import base.TranslateAPI;
import base.Word;
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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchController extends MainController {

    String[] suggestions;
    @FXML
    private TextField searchBar;
    @FXML
    private Button SpeakButton;
    @FXML
    private Button addLearningButton;
    @FXML
    private Button addSynonymsButton;
    @FXML
    private WebView wordExplain;
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
    private Button ToolbarMenuButton;
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
        FileTool.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                TranslateTransition moveFirst = new TranslateTransition(Duration.seconds(0.38),DictionaryTool);
                moveFirst.setToY(0);
                TranslateTransition moveSecond = new TranslateTransition(Duration.seconds(0.38),HistoryTool);
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
        addNote.setVisible(false);
        addSynonymsButton.setVisible(false);
        en_vi_dict.setVisible(true);
        vi_en_dict.setVisible(false);
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 2) {
                try {
                    UserInput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        searchBar.focusedProperty().addListener((ObservableValue <? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
            if (!oldValue && newValue) {
                getHistory();
                searchBar.setText("s");
                searchBar.setText("");
            }
        });
        ToolPane.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            insideToolbar = true;
        });
        ToolPane.addEventHandler(MouseEvent.MOUSE_EXITED, mouseEvent -> {
            insideToolbar = false;
        });
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
                for (int i =0; i<repeated.size(); i++) {
                    Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                    alert1.setTitle("Remove Repeated Words");
                    alert1.setHeaderText(repeated.get(i).getWordTarget() + " is already in the dictionary.");
                    alert1.setContentText("Do you want to replace " + repeated.get(i).getWordTarget() +"?");
                    Optional<ButtonType> result1 = alert.showAndWait();

                    if (result1.get() == ButtonType.OK) {
                        int position = DictionaryManager.binSearch(repeated.get(i).getWordTarget());
                        DictionaryManager.curDict.remove(position);
                        DictionaryManager.curDict.add(repeated.get(i));
                        Collections.sort(DictionaryManager.curDict);
                    }
                }
            }
        }
    }
    @FXML
    protected void onClickAdd() {
        menuOpen = false;
        menuBar.setVisible(false);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        ToolbarClick();
        RunApplication.SwitchScenes(stage,"add.fxml");
    }
    @FXML
    protected void onClickModify() throws IOException {
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
    protected void LoadGraph() {
        UsageOverTime.getData().clear();
        ImageView loading = new ImageView(loadImage);
        loading.setFitWidth(60);
        loading.setFitHeight(60);
        loading.setLayoutX(310);
        loading.setLayoutY(460);
        AnchorPane parent = (AnchorPane) UsageOverTime.getParent();
        new Thread(() -> {
            boolean success = true;
            Platform.runLater(() -> parent.getChildren().add(loading));
            XYChart.Series<Integer,Double> data = new XYChart.Series<>();
            String requestString = searched.replace(' ','+');
            double[] wordData = TranslateAPI.graphData(requestString);
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
    protected void CreateSearchHyperlink(String word) throws Exception {
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
    protected void LoadSynonym() throws Exception {
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
    protected void enterSearch() throws Exception {
        HideMenuBar();
        if (graphFailed.isVisible()) graphFailed.setVisible(false);
        UsageOverTime.getData().clear();
        wordSynonyms.getChildren().clear();
        addNote.setVisible(false);
        if (!Objects.equals(DictionaryManager.dictionaryLookup(searchBar.getText(), type_Dict), "Word not found.")) {
            searched = searchBar.getText();
            DisplayWordExplain();
            LoadSynonym();
            SpeakButton.requestFocus();
            DictionaryManager.addHistory(searched, type_Dict);
            history = DictionaryManager.History.toArray(new String[0]);
            LoadGraph();
        }
        else {
            SpeakButton.setVisible(false);
            addLearningButton.setVisible(false);
            wordExplain.getEngine().loadContent("Word not found.", "text/html");
        }
    }
    @FXML
    private void DisplayWordExplain() throws Exception {
        String explain = DictionaryManager.dictionaryLookup(searchBar.getText(), type_Dict);
        wordExplain.getEngine().loadContent(explain, "text/html");
        SpeakButton.setVisible(true);
        addLearningButton.setVisible(true);
    }
    @FXML
    private void onClickSpeakButton() throws Exception {
        TranslateAPI.speakAudio(searched,"English");
    }
    @FXML
    public void onClickAddLearning() {
        addNote.setVisible(true);
        Syms.setVisible(false);
        Notes.setVisible(true);
        Notes.requestFocus();
    }
    @FXML
    public void addDescription(ActionEvent actionEvent) throws IOException {
        DictionaryManager.addLearning(searched, Notes.getText(), type_Dict);
        RunApplication.Reload("learning.fxml");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Added");
        alert.setHeaderText(null);
        alert.setContentText("Added to learning list");
        alert.showAndWait();
        addNote.setVisible(false);
    }
    @FXML
    public void addSyms() throws Exception {
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
        }
        else {
            type_Dict = "EN_VI";
            en_vi_dict.setVisible(true);
            vi_en_dict.setVisible(false);
        }
        getHistory();
    }
}
