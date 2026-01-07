package com.github.giamgiammi.StructuredFileViewer.ui.main;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Controller for the main view
 * FXML: main.fxml
 */
@Slf4j
public class MainViewController {
    @FXML
    private BorderPane rootPane;

    /**
     * Handler for the close item inside the File menu
     */
    public void handleCloseApp() {
        FXUtils.closeApp(rootPane.getScene().getWindow());
    }

    /**
     * Handler for the new-window item
     */
    public void handleNewWindows() {
        try {
            App.openNewWindow();
        } catch (IOException e) {
            log.error("Failed to open new window", e);
        }
    }
}
