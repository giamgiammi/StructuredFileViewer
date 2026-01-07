package com.github.giamgiammi.StructuredFileViewer.model;

/**
 * A generic wrapper class that associates a value of type T with a displayable text.
 *
 * @param <T> the type of the value being wrapped
 * @param value the value being wrapped
 * @param text the text representation associated with the value
 */
public record ChoiceWrapper<T>(
        T value,
        String text
) {

    @Override
    public String toString() {
        return text;
    }
}
