module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires javafx.web;
    requires AnimateFX;
    requires org.apache.commons.text;
    requires org.apache.commons.lang3;
    requires org.apache.commons.codec;
    requires java.net.http;
    opens base to javafx.fxml;
        exports base;
        exports main;
        opens main to javafx.fxml;
}