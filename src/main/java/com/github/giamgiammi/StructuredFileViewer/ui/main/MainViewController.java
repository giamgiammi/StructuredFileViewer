package com.github.giamgiammi.StructuredFileViewer.ui.main;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.task.ParseFileTask;
import com.github.giamgiammi.StructuredFileViewer.ui.about.AboutDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.lang.ChangeLanguageDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.load.LoadFileDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.tab.CloseTabAlert;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Controller for the main view
 * FXML: main.fxml
 */
@Slf4j
public class MainViewController {
    private final ResourceBundle bundle = App.getBundle();

    @FXML
    private BorderPane rootPane;

    @FXML
    private TabPane tabPane;

    /**
     * Handler for the close item inside the File menu
     */
    public void handleCloseApp() {
        FXUtils.closeApp(rootPane.getScene().getWindow());
    }

    /**
     * Handler for the new-window menu item
     */
    public void handleNewWindows() {
        try {
            App.openNewWindow();
        } catch (IOException e) {
            log.error("Failed to open new window", e);
            new ExceptionAlert(rootPane.getScene().getWindow(), e).showAndWait();
        }
    }

    /**
     * Handler for the change-language menu item
     */
    public void handleChangeLanguage() {
        new ChangeLanguageDialog(rootPane.getScene().getWindow()).showAndWait()
                .ifPresent(locale -> App.changeLocale(locale.value()));
    }

    /**
     * Handler for the about menu item
     */
    public void handleAbout() {
        new AboutDialog(rootPane.getScene().getWindow()).showAndWait();
    }

    /**
     * Handler for the new tab menu item
     * Opens a new tab
     */
    public void handleNewTab() {
        new LoadFileDialog(rootPane.getScene().getWindow()).showAndWait().ifPresent(result -> {
            val task = result.file() != null ? new ParseFileTask<>(result.model(), result.file()) : new ParseFileTask<>(result.model(), result.fileContent());
            val name = result.file() != null ? result.file().getFileName().toString() : bundle.getString("label.pasted_content");
            val tab = new Tab(name, new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));
            tab.setOnCloseRequest(evt -> new CloseTabAlert(tabPane.getScene().getWindow(), tab.getText()).showAndWait()
                    .ifPresent(btn -> {
                        if (btn.getButtonData() != ButtonBar.ButtonData.OK_DONE) evt.consume();
                        else if (tabPane.getTabs().size() == 1) Platform.runLater(this::handleNewTab);
                    }));
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            task.setOnFailed(evt -> {
                tab.setContent(new Label(bundle.getString("label.failed_load_file")));
            });

            task.setOnSucceeded(evt -> {
                //todo create actual show component
                tab.setContent(new Label("Loading succeeded"));
            });

            Thread.ofVirtual().name("parse-task").start(task);
        });
    }
}
