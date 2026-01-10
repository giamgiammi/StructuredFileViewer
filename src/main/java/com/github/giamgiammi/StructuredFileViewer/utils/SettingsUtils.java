package com.github.giamgiammi.StructuredFileViewer.utils;

import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import com.github.giamgiammi.StructuredFileViewer.model.SettingsFileRecord;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class SettingsUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Saves the given settings associated with a specified data model type to the provided file path.
     * The settings are serialized and written in a structured format using a default pretty printer.
     *
     * @param type the type of the data model for which the settings are being saved; must not be null
     * @param settings the settings object to be serialized and saved; must not be null
     * @param file the file path where the settings will be saved; must not be null
     * @throws IOException if an I/O error occurs while writing to the file
     */
    public static void saveSettings(@NonNull DataModelType type, @NonNull Object settings, @NonNull Path file) throws IOException {
        try (val writer = Files.newBufferedWriter(file)) {
            MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(writer, new SettingsFileRecord(type, settings));
        }
    }

    /**
     * Loads settings from the specified file path and returns the settings wrapped
     * in a {@link SettingsFileRecord} object. The method reads, deserializes, and
     * converts the settings based on the type specified within the file.
     *
     * @param file the path to the file from which settings are to be loaded; must not be null
     * @return a {@link SettingsFileRecord} containing the type and deserialized settings
     * @throws IOException if an I/O error occurs while reading from the file
     */
    public static SettingsFileRecord loadSettings(@NonNull Path file) throws IOException {
        try (val reader = Files.newBufferedReader(file)) {
            val record = MAPPER.readValue(reader, SettingsFileRecord.class);
            val clazz = record.type().getSettingsClass();
            val settings = MAPPER.convertValue(record.settings(), clazz);
            return new SettingsFileRecord(record.type(), settings);
        }
    }
}
