module com.github.giamgiammi.StructuredFileViewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.prefs;
    requires org.slf4j;
    requires java.desktop;

    opens com.github.giamgiammi.StructuredFileViewer to javafx.fxml;
    opens com.github.giamgiammi.StructuredFileViewer.ui.main to javafx.fxml;
    exports com.github.giamgiammi.StructuredFileViewer;
}
