package com.github.giamgiammi.StructuredFileViewer.ui.main;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.model.TabData;
import com.github.giamgiammi.StructuredFileViewer.task.ParseFileTask;
import com.github.giamgiammi.StructuredFileViewer.task.ParseStringTask;
import com.github.giamgiammi.StructuredFileViewer.ui.about.AboutDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.lang.ChangeLanguageDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.load.LoadFileDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.tab.CloseTabAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.table.TableDataController;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import com.github.giamgiammi.StructuredFileViewer.utils.OSUtils;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for the main view
 * FXML: main.fxml
 */
@Slf4j
public class MainViewController implements Initializable {
    private final ResourceBundle bundle = App.getBundle();
    private final Map<Tab, TabData> tabDataMap = new HashMap<>();

    @FXML
    private BorderPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu dataMenu;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            dataMenu.setVisible(newTab != null);
        });

        if (OSUtils.isMac()) menuBar.useSystemMenuBarProperty().set(true);
    }

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
            final Task<?> task;
            if (result.file() != null) task = new ParseFileTask<>(result.model(), result.file());
            else task = new ParseStringTask<>(result.model(), result.fileContent());

            val name = result.file() != null ? result.file().getFileName().toString() : bundle.getString("label.pasted_content");
            val tab = new Tab(name, new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));
            tab.setOnCloseRequest(evt -> new CloseTabAlert(tabPane.getScene().getWindow(), tab.getText()).showAndWait()
                    .ifPresent(btn -> {
                        if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            tabDataMap.remove(tab);
                        } else {
                            evt.consume();
                        }
                    }));
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);

            task.setOnFailed(evt -> {
                log.error("Failed to load file", task.getException());
                tab.setContent(new Label(bundle.getString("label.failed_load_file")));
                new ExceptionAlert(rootPane.getScene().getWindow(), task.getException()).showAndWait();
            });

            task.setOnSucceeded(evt -> {
                val data = task.getValue();

                if (data == null) {
                    log.error("Failed to load file: null data");
                    tab.setContent(new Label(bundle.getString("label.failed_load_file")));
                    new ExceptionAlert(rootPane.getScene().getWindow(), new NullPointerException("Null data")).showAndWait();
                }

                val context = new TabData();
                context.setModel(result.model());
                tabDataMap.put(tab, context);

                if (data instanceof TableLikeData tableLikeData) {
                    tab.setContent(FXUtils.loadFXML(TableDataController.class, "table", controller -> {
                        controller.setData(tableLikeData);
                        context.setController(controller);
                    }));
                } else {
                    log.error("Failed to load file: unexpected data type {}", data.getClass());
                    tab.setContent(new Label(bundle.getString("label.failed_load_file")));
                    new ExceptionAlert(rootPane.getScene().getWindow(), new IllegalStateException("Unexpected data type: " + data.getClass())).showAndWait();
                }
            });

            FXUtils.start(task);
        });
    }
}
