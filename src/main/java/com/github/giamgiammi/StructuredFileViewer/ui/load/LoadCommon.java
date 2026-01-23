package com.github.giamgiammi.StructuredFileViewer.ui.load;

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
}
