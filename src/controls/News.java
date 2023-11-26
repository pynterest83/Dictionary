package controls;

import base.News_WOD;
import base.TranslateAPI;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class News implements NewsControls {
    private int curNews = 0;
    private final ImageView news_image;
    private final javafx.scene.control.Label Title;
    private final javafx.scene.control.Label Description;
    private final javafx.scene.control.Label Content;
    private final Line line1;
    private final Line line2;
    private final javafx.scene.control.Label Word;
    private final javafx.scene.control.Label Date;
    private final javafx.scene.control.Label Definition;
    private final javafx.scene.control.Label Pronunciation;
    private final Label WordType;

    public News(ImageView news_image, Label title, Label description, Label content, Line line1, Line line2, Label word, Label date, Label definition, Label pronunciation, Label wordType) {
        this.news_image = news_image;
        Title = title;
        Description = description;
        Content = content;
        this.line1 = line1;
        this.line2 = line2;
        Word = word;
        Date = date;
        Definition = definition;
        Pronunciation = pronunciation;
        WordType = wordType;
    }

    @FXML
    public void setUpNews() {
        news_image.setImage(new Image(News_WOD.articles.get(curNews).getUrlToImage()));
        Rectangle clip = new Rectangle();
        clip.setWidth(400.0f);
        clip.setHeight(400.0f);
        clip.setArcHeight(20);
        clip.setArcWidth(20);
        clip.setStroke(Color.BLACK);
        news_image.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = news_image.snapshot(parameters, null);
        news_image.setClip(null);
        news_image.setEffect(new DropShadow(10, Color.RED));
        news_image.setImage(image);

        Title.setText(News_WOD.articles.get(curNews).getTitle());
        Title.setWrapText(true);
        Description.setText(News_WOD.articles.get(curNews).getDescription());
        Description.setWrapText(true);
        String content = News_WOD.articles.get(curNews).getContent();
        int idx = content.indexOf("[");
        if (idx != -1) content = content.substring(0,idx);
        Content.setText(content);
        Content.setWrapText(true);
    }

    @FXML
    public void onClickNextNews() {
        if (++curNews == News_WOD.articles.size()) curNews = 0;
        setUpNews();
    }
    @FXML
    public void open_link() {
        try {
            Desktop.getDesktop().browse(URI.create(News_WOD.articles.get(curNews).getUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void setUpWOD() {
        Word.setText(News_WOD.wordOfTheDayArticles.get(0).getTitle());
        Word.setWrapText(true);
        Date.setText(News_WOD.todayDate);
        Date.setWrapText(true);
        String description = News_WOD.wordOfTheDayArticles.get(0).getDescription();
        String pronunciation = description.substring(description.indexOf("\\"), description.lastIndexOf("\\") + 1);
        Pronunciation.setText(pronunciation);
        Pronunciation.setWrapText(true);
        String wordType = description.substring(description.lastIndexOf("\\") + 5, description.indexOf("\n", description.lastIndexOf("\\")));
        WordType.setText(wordType);
        WordType.setWrapText(true);
        Definition.setText(News_WOD.definition);
        Definition.setWrapText(true);
    }
    @FXML
    public void open_link1() {
        try {
            Desktop.getDesktop().browse(URI.create(News_WOD.wordOfTheDayArticles.get(0).getUrl()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void onClickSpeakButton() {
        TranslateAPI.speakAudio(News_WOD.wordOfTheDayArticles.get(0).getTitle(),"English");
    }
}
