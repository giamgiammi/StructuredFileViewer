package com.github.giamgiammi.StructuredFileViewer.core;

import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the types of data models supported by the application.
 * Data model types are used to uniquely identify specific kinds of
 * data model implementations and their associated factory classes.
 *
 * This enumeration can be used in conjunction with {@link DataModelFactory}
 * to identify and create specific types of data models and their settings.
 */
@RequiredArgsConstructor
@Getter
public enum DataModelType {
    /**
     * Represent a CSV-LIKE structured data.
     * CSV, TSV, and other character-separated formats can be parsed with this
     */
    CSV_LIKE(CsvSettings.class, true);

    private final Class<?> settingsClass;
    private final boolean canLoadStrings;
}
