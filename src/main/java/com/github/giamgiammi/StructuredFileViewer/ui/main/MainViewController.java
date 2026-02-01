package com.github.giamgiammi.StructuredFileViewer.ui.main;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.model.LoadResult;
import com.github.giamgiammi.StructuredFileViewer.model.TabData;
import com.github.giamgiammi.StructuredFileViewer.task.ParseFileTask;
import com.github.giamgiammi.StructuredFileViewer.task.ParseStringTask;
import com.github.giamgiammi.StructuredFileViewer.ui.about.AboutDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.lang.ChangeLanguageDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.load.EditSettingsDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.load.LoadFileDialog;
import com.github.giamgiammi.StructuredFileViewer.ui.tab.CloseTabAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.table.TableDataController;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import com.github.giamgiammi.StructuredFileViewer.utils.OSUtils;
import com.github.giamgiammi.StructuredFileViewer.utils.UpdateNotifier;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

/**
 * Controller for the main view
 * FXML: main.fxml
 */
@Slf4j
public class MainViewController implements Initializable {
    private static final String SYSTEM_MENU_BAR_KEY = "system.menu.bar";
    private static final String CHECK_FOR_UPDATES_KEY = "check.for.updates";

    private static final BooleanProperty CHECK_FOR_UPADTE = new SimpleBooleanProperty(
            Preferences.userNodeForPackage(MainViewController.class).getBoolean(CHECK_FOR_UPDATES_KEY, false)
    );
    private static final AtomicBoolean firstRun = new AtomicBoolean(true);

    private final ResourceBundle bundle = App.getBundle();
    private final Map<Tab, TabData> tabDataMap = new HashMap<>();
    private final BooleanProperty isMainView = new SimpleBooleanProperty(true);

    @Setter
    private List<MainViewController> controllers;

    @FXML
    private BorderPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu dataMenu;

    @FXML
    private CheckMenuItem mainViewMenuItem;

    @FXML
    private CheckMenuItem useSystemMenuBarMenuItem;

    @FXML
    private CheckMenuItem checkForUpdatesMenuItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            dataMenu.setVisible(newTab != null);
        });

        if (OSUtils.isMac()) {
            menuBar.setUseSystemMenuBar(Preferences.userNodeForPackage(getClass()).getBoolean(SYSTEM_MENU_BAR_KEY, true));
            useSystemMenuBarMenuItem.selectedProperty().bindBidirectional(menuBar.useSystemMenuBarProperty());
            useSystemMenuBarMenuItem.setOnAction(evt -> Preferences.userNodeForPackage(getClass()).putBoolean(SYSTEM_MENU_BAR_KEY, useSystemMenuBarMenuItem.isSelected()));
        } else {
            useSystemMenuBarMenuItem.setVisible(false);
        }

        mainViewMenuItem.selectedProperty().bindBidirectional(isMainView);
        mainViewMenuItem.setOnAction(evt -> {
            if (isMainView.get()) controllers.stream().filter(c -> c != this).forEach(c -> c.setIsMainView(false));
            else {
                val others = controllers.stream().filter(c -> c != this).toList();
                if (!others.isEmpty()) controllers.getLast().setIsMainView(true);
            }
        });

        checkForUpdatesMenuItem.selectedProperty().bindBidirectional(CHECK_FOR_UPADTE);
        checkForUpdatesMenuItem.setOnAction(evt -> {
            Preferences.userNodeForPackage(getClass()).putBoolean(CHECK_FOR_UPDATES_KEY, checkForUpdatesMenuItem.isSelected());
        });

        if (CHECK_FOR_UPADTE.get() && firstRun.compareAndSet(true, false)) {
            FXUtils.runLater(this::handleCheckForUpdates, 1000);
        }

        if (Preferences.userNodeForPackage(getClass()).get(CHECK_FOR_UPDATES_KEY, null) == null) {
            FXUtils.runLater(this::askForCheckingUpdates, 1000);
        }
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
        new LoadFileDialog(rootPane.getScene().getWindow()).showAndWait().ifPresent(this::loadTab);
    }

    private void loadTab(LoadResult<?> result) {
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
            context.setFile(result.file());
            context.setFileContent(result.fileContent());
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
    }

    /**
     * Handler for the edit settings data menu item
     */
    public void handleEditSettings() {
        new EditSettingsDialog(rootPane.getScene().getWindow(),
                tabDataMap.get(tabPane.getSelectionModel().getSelectedItem())).showAndWait()
                .ifPresent(this::loadTab);
    }

    public void openFiles(@NonNull Path...files) {
        for (val file: files) {
            new LoadFileDialog(rootPane.getScene().getWindow(), file).showAndWait().ifPresent(this::loadTab);
        }
    }

    public boolean isMainView() {
        return isMainView.get();
    }

    public void setIsMainView(boolean mainView) {
        isMainView.set(mainView);
    }

    public void handleOpenLogFolder() {
        App.openLogFolder();
    }

    public void handleCheckForUpdates() {
        val task = new Task<Optional<String>>() {
            @Override
            protected Optional<String> call() throws Exception {
                return new UpdateNotifier().checkForUpdates();
            }
        };
        task.setOnSucceeded(evt -> {
            task.getValue().ifPresent(url -> {
                val alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(rootPane.getScene().getWindow());
                alert.setTitle(bundle.getString("update.title"));
                alert.setHeaderText(bundle.getString("update.header"));

                val grid = new GridPane(2, 2);
                grid.add(new Label(bundle.getString("update.msg")), 0, 0);
                val link = new Hyperlink(url);
                link.setOnAction(evt1 -> App.openLink(url));
                grid.add(link, 1, 0);

                alert.getDialogPane().setContent(grid);

                val closeButton = new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(closeButton);

                alert.showAndWait();
            });
        });
        FXUtils.start(task);
    }

    private void askForCheckingUpdates() {
        val alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(rootPane.getScene().getWindow());
        alert.setTitle(bundle.getString("update.ask.title"));
        alert.setHeaderText(bundle.getString("update.ask.header"));
        alert.setContentText(bundle.getString("update.ask.content"));

        val yesButton = new ButtonType(bundle.getString("label.yes"), ButtonBar.ButtonData.YES);
        val noButton = new ButtonType(bundle.getString("label.no"), ButtonBar.ButtonData.NO);
        alert.getDialogPane().getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(btn -> {
            if (btn == yesButton) {
                CHECK_FOR_UPADTE.set(true);
                Preferences.userNodeForPackage(getClass()).putBoolean(CHECK_FOR_UPDATES_KEY, true);
                handleCheckForUpdates();
            } else if (btn == noButton) {
                Preferences.userNodeForPackage(getClass()).putBoolean(CHECK_FOR_UPDATES_KEY, false);
            }
        });
    }
}
