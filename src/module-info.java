module main {
        requires javafx.controls;
        requires javafx.fxml;
        requires javafx.media;
        requires org.controlsfx.controls;
        requires javafx.web;
        requires AnimateFX;
    requires json.simple;
    opens base to javafx.fxml;
        exports base;
        exports main;
        opens main to javafx.fxml;
}