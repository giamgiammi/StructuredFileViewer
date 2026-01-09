package com.github.giamgiammi.StructuredFileViewer.model.csv;

import org.apache.commons.csv.CSVFormat;

public record BaseFormatChoice(
        CSVFormat format,
        String text
) {
    @Override
    public String toString() {
        return text;
    }
}
