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

    /**
     * Checks if the provided string is null or empty.
     *
     * @param text the string to check for nullity or emptiness
     * @return {@code true} if the string is null or its length is zero, otherwise {@code false}
     */
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * Extracts a substring from the specified string, starting at the given
     * start index and ending at the given end index. If the input string is
     * {@code null}, returns {@code null}. If the start index is greater than
     * the length of the input string, an empty string is returned. If the end
     * index exceeds the length of the input string, it is adjusted to the
     * string's length.
     *
     * @param string the input string from which to extract the substring
     * @param start the starting index (inclusive) of the substring
     * @param end the ending index (exclusive) of the substring
     * @return the extracted substring, or {@code null} if the input string is {@code null}
     */
    public static String substring(String string, int start, int end) {
        if (string == null) return null;
        if (start > string.length()) return "";
        if (end > string.length()) end = string.length();
        return string.substring(start, end);
    }
}
