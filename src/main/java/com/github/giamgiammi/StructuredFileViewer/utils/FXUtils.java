package com.github.giamgiammi.StructuredFileViewer.utils;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * Utility class for JavaFX
 */
@Slf4j
public class FXUtils {
    private static final ThreadFactory THREAD_FACTORY = Thread.ofVirtual()
            .name("task-runner-", 0)
            .factory();

    /**
     * Loads an FXML file and returns the root element of the loaded scene graph.
     * Allows setting a callback to interact with the controller associated with the loaded FXML file.
     *
     * @param <T> the type of the controller class
     * @param controllerClass the class of the controller associated with the FXML file; must not be null
     * @param fxmlPath the path to the FXML file, relative to the controller class's package; must not be null
     * @param controllerCallback an optional callback to handle the controller after loading; can be null
     * @return the root {@code Parent} element of the loaded FXML scene graph, or {@code null} if loading fails
     */
    public static <T> Parent loadFXML(@NonNull Class<T> controllerClass, @NonNull String fxmlPath, Consumer<T> controllerCallback) {
        try {
            val loader = new FXMLLoader();
            loader.setResources(App.getBundle());

            if (!fxmlPath.endsWith(".fxml")) fxmlPath += ".fxml";
            loader.setLocation(controllerClass.getResource(fxmlPath));

            val parent = (Parent) loader.load();

            if (controllerCallback != null) {
                controllerCallback.accept(loader.getController());
            }
            return parent;
        } catch (Exception e) {
            log.error("Failed to load FXML", e);
            return null;
        }
    }

    /**
     * Displays a confirmation dialog to the user for closing the application.
     * If the user confirms the action, this method attempts to close the provided window.
     * If the provided window is not a stage, an error is logged, and an exception alert is shown.
     *
     * @param owner the owner {@code Window} of the confirmation dialog; should ideally be an instance of {@code Stage}
     */
    public static void closeApp(Window owner) {
        val bundle = App.getBundle();
        val alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);

        alert.setTitle(bundle.getString("close.title"));
        alert.setHeaderText(bundle.getString("close.header"));
        alert.setContentText(bundle.getString("close.content"));

        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, cancelButton);

        alert.showAndWait().ifPresent(btn -> {
            if (btn == okButton) {
                if (owner instanceof Stage stage) stage.close();
                else {
                    log.error("Tried to close non-stage window");
                    new ExceptionAlert(owner, new IllegalStateException("Tried to close non-stage window")).showAndWait();
                }
            }
        });
    }

    /**
     * Start a task with the default thread factory.
     * @param task the task to start
     * @throws NullPointerException if task is null
     */
    public static void start(@NonNull Task<?> task) {
        log.info("Starting task {}", task);
        THREAD_FACTORY.newThread(task).start();
    }

    /**
     * Schedules the specified {@code Runnable} to run after a delay of the specified number of milliseconds.
     *
     * @param runnable the task to execute; must not be null
     * @param milliseconds the delay in milliseconds before the runnable is executed
     * @throws NullPointerException if the {@code runnable} is null
     */
    public static void runLater(@NonNull Runnable runnable, long milliseconds) {
        val task = new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
                Thread.sleep(milliseconds);
                return null;
            }
        };
        task.setOnSucceeded(e -> runnable.run());
        start(task);
    }
}
