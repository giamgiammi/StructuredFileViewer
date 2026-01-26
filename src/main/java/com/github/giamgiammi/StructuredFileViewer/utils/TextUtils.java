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

    /**
     * Compares two strings in a null-safe manner.
     * If both strings are {@code null}, they are considered equal.
     * If only one string is {@code null}, it is considered less than the non-null string.
     * If both strings are non-null, their natural ordering is used for comparison.
     *
     * @param a the first string to compare, may be {@code null}
     * @param b the second string to compare, may be {@code null}
     * @return a negative integer if {@code a} is less than {@code b}, zero if {@code a} equals {@code b},
     *         and a positive integer if {@code a} is greater than {@code b}
     */
    public static int safeCompare(String a, String b) {
        if (a == null) {
            if (b == null) return 0;
            return -1;
        }
        if (b == null) return 1;
        return a.compareTo(b);
    }

    /**
     * Checks whether the specified pattern exists within the given value.
     * If the pattern is {@code null}, the method checks whether the value is also {@code null}.
     * If the pattern is not {@code null}, it checks if the value contains the pattern as a substring.
     *
     * @param pattern the substring to search for, may be {@code null}
     * @param value the string in which to search for the pattern, may be {@code null}
     * @return {@code true} if the pattern is found within the value, or if both are {@code null};
     *         otherwise {@code false}
     */
    public static boolean contains(String pattern, String value) {
        if (pattern == null) {
            return value == null;
        }
        return value.contains(pattern);
    }

    /**
     * Checks whether the specified pattern exists within the given value in a case-insensitive manner.
     * If the pattern is {@code null}, the method checks whether the value is also {@code null}.
     * If the pattern is not {@code null}, it checks if the value contains the pattern as a substring, ignoring case differences.
     *
     * @param pattern the substring to search for, may be {@code null}
     * @param value the string in which to search for the pattern, may be {@code null}
     * @return {@code true} if the pattern (case-insensitively) is found within the value,
     *         or if both are {@code null}; otherwise {@code false}
     */
    public static boolean containsIgnoreCase(String pattern, String value) {
        return contains(pattern == null ? null : pattern.toLowerCase(), value == null ? null : value.toLowerCase());
    }
}
