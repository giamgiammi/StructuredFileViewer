package com.github.giamgiammi.StructuredFileViewer.ui.inteface;

import lombok.NonNull;

/**
 * A generic controller interface for managing settings of a specified type.
 *
 * @param <SETTINGS> the type of the settings object that this controller manages
 */
public interface SettingsController<SETTINGS> {
    /**
     * Get the settings object associated with this controller.
     * @return the settings object
     */
    @NonNull
    SETTINGS getSettings();

    /**
     * Set the settings object associated with this controller.
     * @param settings the settings object, might be null
     */
    void setSettings(SETTINGS settings);
}
