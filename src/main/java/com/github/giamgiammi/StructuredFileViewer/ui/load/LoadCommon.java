package com.github.giamgiammi.StructuredFileViewer.ui.load;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import com.github.giamgiammi.StructuredFileViewer.model.ModelChoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.val;

import java.io.File;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class LoadCommon {
    /**
     * Retrieves the initial directory for file selection based on the stored user preference.
     * If a previously saved directory path exists and is valid (i.e., the path exists and is a directory),
     * it returns the corresponding File instance. Otherwise, it returns null.
     *
     * @return The initial directory as a {@code File} object if a valid path exists, or {@code null} if no valid path is found.
     */
    public File getInitialDirectory() {
        val path = Preferences.userNodeForPackage(getClass()).get("last_dir", null);
        if (path != null) {
            val file =  new File(path);
            if (file.exists() && file.isDirectory()) return file;
        }
        return null;
    }

    /**
     * Sets the initial directory for file selection by storing the provided directory path
     * in the user's preferences node. This allows the directory to be remembered and reused
     * in future sessions of the application.
     *
     * @param dir The directory to set as the initial directory for file selection.
     *            Must be a valid {@code File} instance representing a directory.
     */
    public void setInitialDirectory(File dir) {
        Preferences.userNodeForPackage(getClass()).put("last_dir", dir.getAbsolutePath());
    }

    /**
     * Retrieves the initial directory for the model file selection based on the stored user preference.
     * If a previously saved directory path exists, is valid (i.e., the path exists and is a directory),
     * it returns the corresponding File instance. Otherwise, it returns null.
     *
     * @return The initial directory as a {@code File} object if a valid path exists, or {@code null} if no valid path is found.
     */
    public File getModelInitialDirectory() {
        val path = Preferences.userNodeForPackage(getClass()).get("model_last_dir", null);
        if (path != null) {
            val file = new File(path);
            if (file.exists() && file.isDirectory()) return file;
        };
        return null;
    }

    /**
     * Sets the initial directory for model file selection by storing the provided directory path
     * in the user's preferences node. This allows the directory to be remembered and reused
     * in future sessions of the application.
     *
     * @param dir The directory to set as the initial directory for model file selection.
     *            Must be a valid {@code File} instance representing a directory.
     */
    public void setModelInitialDirectory(File dir) {
        Preferences.userNodeForPackage(getClass()).put("model_last_dir", dir.getAbsolutePath());
    }

    /**
     * Loads settings from a file chosen by the user via a file chooser dialog. The method uses
     * the previously stored initial directory for the file chooser, if available, and updates
     * the initial directory based on the file selected by the user.
     *
     * @param owner    The owner window for the file chooser dialog. Typically, the primary
     *                 application window or parent window from which this dialog is invoked.
     * @param callback A {@code Consumer<File>} that will be invoked with the selected file as
     *                 an argument. This callback can be used to process the contents of the
     *                 selected file.
     */
    public void loadSettingsTofile(Window owner, Consumer<File> callback) {
        val fc = new FileChooser();
        fc.setInitialDirectory(getModelInitialDirectory());
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("JSON", "*.json"));
        val file = fc.showOpenDialog(owner);
        if (file != null) {
            setModelInitialDirectory(file.getParentFile());
            callback.accept(file);
        }
    }

    /**
     * Saves the application settings to a file selected by the user using a file chooser dialog.
     * The method uses the specified owner window to display the dialog and provides a callback
     * to process the selected file after saving. The initial directory of the file chooser
     * is set based on the stored user preference for the model file directory. If the user
     * specifies a file name without a `.json` extension, the extension is automatically appended.
     * The chosen file is then passed to the provided callback for further processing.
     *
     * @param owner    The owner window for the file chooser dialog. Typically, this is the parent
     *                 window from which the dialog is invoked.
     * @param callback A {@code Consumer<File>} that will be invoked with the selected file
     *                 after the save operation. This callback can be used to handle the file
     *                 as needed (e.g., saving data to the file).
     */
    public void saveSettingsTofile(Window owner, Consumer<File> callback) {
        val fc = new FileChooser();
        fc.setInitialDirectory(getModelInitialDirectory());
        fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("JSON", "*.json"));
        fc.setInitialFileName("model.json");
        var file = fc.showSaveDialog(owner);
        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".json")) file = new File(file.getPath() + ".json");
            setModelInitialDirectory(file.getParentFile());
            callback.accept(file);
        }
    }

    /**
     * Returns the list of supported data models for the selection ComboBox.
     */
    public ObservableList<ModelChoice> getModelChoices() {
        val bundle = App.getBundle();
        return FXCollections.observableArrayList(
                new ModelChoice(
                        DataModelType.CSV_LIKE,
                        bundle.getString("model.csv")
                ),
                new ModelChoice(
                        DataModelType.FIXED_WIDTH,
                        bundle.getString("model.fixed")
                )
        );
    }
}
