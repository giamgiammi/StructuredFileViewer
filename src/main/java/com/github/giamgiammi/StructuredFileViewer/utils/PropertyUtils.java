package com.github.giamgiammi.StructuredFileViewer.utils;

import lombok.val;

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
}
