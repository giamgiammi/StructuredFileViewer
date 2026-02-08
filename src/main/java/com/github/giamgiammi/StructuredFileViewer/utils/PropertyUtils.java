package com.github.giamgiammi.StructuredFileViewer.utils;

import javafx.util.Pair;
import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class PropertyUtils {
    private static final Properties APP_PROPERTIES = loadAppProperties();

    private static Properties loadAppProperties() {
        try (val in = ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties")) {
            val props = new Properties();
            props.load(in);
            return props;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load app.properties", e);
        }
    }

    /**
     * Retrieves a property value from the application's properties based on the provided key.
     *
     * @param key the key for which the property value is to be retrieved; must not be null
     * @return the property value associated with the given key, or {@code null} if the key does not exist
     */
    public static String getAppProperty(String key) {
        return APP_PROPERTIES.getProperty(key);
    }

    /**
     * Converts the provided {@code Properties} object into a sorted list of key-value pairs.
     * Each property in the {@code Properties} object is transformed into a {@code Pair<String, String>},
     * and the resulting list is sorted in ascending order based on the property keys.
     *
     * @param properties the {@code Properties} object to be converted; must not be null
     * @return a sorted list of key-value pairs represented as {@code Pair<String, String>}
     */
    public static List<Pair<String, String>> toPairs(@NonNull Properties properties) {
        val list = new ArrayList<Pair<String, String>>(properties.size());
        properties.forEach((k, v) -> list.add(new Pair<>(k.toString(), v.toString())));
        list.sort(Comparator.comparing(Pair::getKey));
        return list;
    }

    /**
     * Retrieves the application's properties as a sorted list of key-value pairs.
     * Each property is represented as a {@code Pair<String, String>}, where the key and value
     * are strings. The resulting list is sorted in ascending order by property keys.
     *
     * @return a sorted list of key-value pairs extracted from the application's properties
     */
    public static List<Pair<String, String>> getAppPropertiesPairs() {
        return toPairs(APP_PROPERTIES);
    }
}
