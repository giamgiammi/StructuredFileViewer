package com.github.giamgiammi.StructuredFileViewer.model.csv;

import com.github.giamgiammi.StructuredFileViewer.core.csv.CsvBaseFormat;

public record BaseFormatChoice(
        CsvBaseFormat format,
        String text
) {
    @Override
    public String toString() {
        return text;
    }
}
