package com.github.giamgiammi.StructuredFileViewer.core;

import lombok.NonNull;

/**
 * Represents a factory interface for creating instances of {@link DataModel}.
 * This interface provides methods for retrieving a unique identifier for the
 * data model type, obtaining default settings, and creating a new data model
 * instance based on provided settings.
 *
 * @param <SETTINGS> the type of the settings associated with the data model
 * @param <DATA> the type of the data structure returned by the data model
 */
public interface DataModelFactory<SETTINGS, DATA> {
    /**
     * Retrieves the type of the data model associated with this factory.
     *
     * @return the {@link DataModelType} representing the type of the data model
     */
    @NonNull
    DataModelType getType();

    /**
     * Return the default settings for this data model type
     * @return The default {@link SETTINGS} for this data model type
     */
    @NonNull
    SETTINGS getDefaultSettings();
    
    /**
     * Creates a new data model from the given settings
     * @param settings the settings to use to create the data model
     * @return A newly constructed {@link DataModel} instance with the given settings.
     * @throws NullPointerException if settings is null
     */
    @NonNull
    DataModel<SETTINGS, DATA> create(@NonNull SETTINGS settings);

    /**
     * Creates a new {@link DataModel} instance using the given factory and settings.
     * This method provides a way to create a generic {@link DataModel} by explicitly
     * casting the provided factory to the required generic type at runtime.
     *
     * @param factory the factory used to create the data model
     * @param settings the settings to be used when creating the data model
     * @return a newly constructed {@link DataModel} instance based on the provided factory and settings
     * @throws NullPointerException if the factory or settings is null
     */
    static DataModel<Object, Object> create(DataModelFactory<?, ?> factory, Object settings) {
        return ((DataModelFactory<Object, Object>) factory).create(settings);
    }
}
