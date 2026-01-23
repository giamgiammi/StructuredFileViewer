package com.github.giamgiammi.StructuredFileViewer.utils;

public class OSUtils {

    /**
     * Determines if the operating system is macOS.
     *
     * @return {@code true} if the operating system is macOS, {@code false} otherwise
     */
    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }
}
