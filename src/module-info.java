module main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens main to javafx.fxml;
    exports main;
}