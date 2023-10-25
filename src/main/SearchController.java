package main;

import base.DictionaryManager;
import base.TranslateAPI;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
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
    private Image loadImage = new Image(Paths.get("src/style/media/loading.gif").toUri().toURL().toString());
    private String searched = null;
    private String type_Dict = "EN_VI";
    private String[] history;

    public SearchController() throws MalformedURLException {
    }

    @FXML
    private void initialize() {
        PrepareMenu(true);
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
