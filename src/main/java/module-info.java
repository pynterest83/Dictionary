module uet.chuhuyquang.dictionary {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens uet.chuhuyquang.dictionary to javafx.fxml;
    exports uet.chuhuyquang.dictionary;
}