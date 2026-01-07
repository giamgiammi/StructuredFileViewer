package com.github.giamgiammi.StructuredFileViewer.ui.main;

import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

/**
 * Controller for the main view
 * FXML: main.fxml
 */
public class MainViewController {
    @FXML
    private BorderPane rootPane;

    /**
     * Handler for the close item inside the File menu
     */
    public void handleCloseApp() {
        FXUtils.closeApp(rootPane.getScene().getWindow());
    }
}
