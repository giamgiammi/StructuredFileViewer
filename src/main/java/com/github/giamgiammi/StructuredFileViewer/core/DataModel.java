package com.github.giamgiammi.StructuredFileViewer.core;

import lombok.NonNull;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

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
     * Retrieves the character set associated with this data model.
     * The returned {@link Charset} is used for interpreting and encoding text data
     * processed by the data model.
     *
     * @return the character set used by this data model
     */
    @NonNull
    Charset getCharset();

    /**
     * Load data using this data model from the given reader
     * @param reader the reader to read from
     * @return the parsed data
     * @throws IOException if an error occurs while reading from the reader
     */
    @NonNull
    DATA parse(@NonNull Reader reader) throws IOException;
}
