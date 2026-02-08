package com.github.giamgiammi.StructuredFileViewer.utils;

import lombok.val;

import java.util.Properties;

public class PropertyUtils {
    public static final Properties APP_PROPERTIES = loadAppProperties();

    private static Properties loadAppProperties() {
        try (val in = ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties")) {
            val props = new Properties();
            props.load(in);
            return props;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load app.properties", e);
        }
    }
}
