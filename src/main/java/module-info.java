module com.yaojing.mobaxtermkeygenjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.yaojing.mobaxtermkeygenjavafx to javafx.fxml;
    exports com.yaojing.mobaxtermkeygenjavafx;
}