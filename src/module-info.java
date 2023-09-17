module base {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens base to javafx.fxml;
    exports base;
}
