module main {
        requires javafx.controls;
        requires javafx.fxml;
        requires javafx.media;
        requires voicerss.tts;
    requires org.controlsfx.controls;

    opens base to javafx.fxml;
        exports base;
        exports main;
        opens main to javafx.fxml;
}
