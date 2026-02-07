module com.github.giamgiammi.StructuredFileViewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.prefs;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    requires java.desktop;
    requires org.apache.commons.csv;
    requires tools.jackson.databind;
    requires org.antlr.antlr4.runtime;
    requires java.net.http;
    requires ch.qos.logback.core;

    opens com.github.giamgiammi.StructuredFileViewer to javafx.fxml;
    opens com.github.giamgiammi.StructuredFileViewer.ui.main to javafx.fxml;
    opens com.github.giamgiammi.StructuredFileViewer.ui.csv to javafx.fxml;
    opens com.github.giamgiammi.StructuredFileViewer.ui.table to javafx.fxml;
    opens com.github.giamgiammi.StructuredFileViewer.ui.fixed to javafx.fxml;

    opens com.github.giamgiammi.StructuredFileViewer.model to tools.jackson.databind;
    opens com.github.giamgiammi.StructuredFileViewer.model.csv to tools.jackson.databind;
    opens com.github.giamgiammi.StructuredFileViewer.model.fixed to tools.jackson.databind;

    exports com.github.giamgiammi.StructuredFileViewer;
    opens com.github.giamgiammi.StructuredFileViewer.model.updater to tools.jackson.databind;
}
