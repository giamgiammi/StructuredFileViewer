package com.github.giamgiammi.StructuredFileViewer.utils;

public class TextUtils {
    /**
     * Escapes special characters in the given text, such as backslashes,
     * newlines, carriage returns, and tabs, by replacing them with their
     * respective escaped representations.
     *
     * @param text the input string to be processed
     * @return a string with the special characters replaced by their escaped representations
     */
    public static String quoteSpace(String text) {
        if (text == null) return null;
        return text
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Reverts escaped special characters in the given text back to their original representations.
     * This includes replacing escaped sequences for newlines, carriage returns, tabs, and backslashes
     * with their corresponding special characters.
     *
     * @param text the input string to be processed
     * @return a string with escaped sequences replaced by their original special characters
     */
    public static String unquoteSpace(String text) {
        if (text == null) return null;
        return text
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\\", "\\");
    }

    /**
     * Provides a list of commonly used character encodings.
     *
     * @return an array of strings representing common charset names,
     *         such as "UTF-8", "UTF-16", and "cp1252"
     */
    public static String[] commonCharsets() {
        return new String[]{
                "UTF-8",
                "UTF-16",
                "cp1252"
        };
    }
}
