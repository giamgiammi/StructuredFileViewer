package com.github.giamgiammi.StructuredFileViewer.utils;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.function.Consumer;

/**
 * Utility class for JavaFX
 */
@Slf4j
public class FXUtils {

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
            if (btn == okButton) System.exit(0);
        });
    }
}
