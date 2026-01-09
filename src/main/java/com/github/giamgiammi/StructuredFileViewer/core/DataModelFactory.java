package com.github.giamgiammi.StructuredFileViewer.core;

import javafx.scene.control.Dialog;

/**
 * Factory interface for creating and configuring data models of a specific type.
 * Provides methods to construct a dialog for editing model settings and to create
 * a new data model instance based on specified settings.
 *
 * @param <T> the type of the settings or data associated with the data model
 */
public interface DataModelFactory<T> {
    /**
     * Returns a JavaFx dialog that allows the user to edit the settings of the data model.
     * Accept an optional settings object to edit existing settings
     * @param settings the settings to edit, might be null
     * @return A dialog that returns the settings
     */
    Dialog<T> getSettingsDialog(T settings);

    /**
     * Creates a new data model from the given settings
     * @param settings the settings to use to create the data model, might be null
     * @return A newly constructed data model
     */
    DataModel<T> create(T settings);
}
