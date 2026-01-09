package com.github.giamgiammi.StructuredFileViewer.core;

import javafx.scene.Node;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a data model interface for managing and visualizing structured data.
 * The data model provides methods for retrieving associated settings,
 * loading data from files or strings, and creating graphical components
 * to represent the data using JavaFX.
 *
 * @param <T> the type of the settings or data associated with the data model
 */
public interface DataModel<T> {
    /**
     * Get the factory class associated with this data model
     * @return the factory class
     */
    Class<DataModelFactory<T>> getFactoryClass();

    /**
     * Get the settings associated with this data model
     * @return the settings
     */
    T getSettings();

    /**
     * Load a file using this data model and create a JavaFx component to display it
     * @param file the file to load
     * @return A node to display the file content
     * @throws IOException If the file cannot be loaded
     */
    Node loadFile(Path file) throws IOException;

    /**
     * Load a string using this data model and create a JavaFx component to display it.
     * Implementations are allowed to ignore this method if they do not support loading strings
     * @param string the string to load
     * @return A node to display the string content
     * @throws IOException If the string cannot be loaded
     * @throws UnsupportedOperationException If this data model does not support loading strings
     */
    default Node loadString(String string) throws IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException(String.format("Data model %s does not support loading strings", getClass().getName()));
    }
}
