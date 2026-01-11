package com.github.giamgiammi.StructuredFileViewer.core;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a generic interface for a data model capable of loading and parsing
 * data based on a specified settings type and producing a data structure type.
 * The implementation of this interface should provide behavior to retrieve the associated
 * factory class, access model settings, and parse data from a provided reader.
 *
 * @param <SETTINGS> the type of the settings associated with the data model
 * @param <DATA> the type of the data structure returned by the data model
 */
public interface DataModel<SETTINGS, DATA> {
    /**
     * Get the factory class associated with this data model
     * @return the factory class
     */
    @NonNull
    Class<? extends DataModelFactory<SETTINGS, DATA>> getFactoryClass();

    /**
     * Get the settings associated with this data model
     * @return the settings
     */
    @NonNull
    SETTINGS getSettings();

    /**
     * Parses data from the provided input stream and returns a data structure
     * of type {@code DATA}.
     *
     * @param stream the input stream containing the data to be parsed; must not be null
     * @return the parsed data structure of type {@code DATA}; never null
     * @throws IOException if an I/O error occurs while reading from the input stream
     * @throws NullPointerException if the input stream is null
     */
    @NonNull
    DATA parse(@NonNull InputStream stream) throws IOException;
}
