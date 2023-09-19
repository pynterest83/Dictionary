module base {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires voicerss.tts;

    opens base to javafx.fxml;
    exports base;
}
