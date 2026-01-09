package com.github.giamgiammi.StructuredFileViewer.model.csv;

import org.apache.commons.csv.QuoteMode;

public record QuoteModeChoice(
        QuoteMode mode
) {
    @Override
    public String toString() {
        if (mode == null) return "";
        return mode.name();
    }
}
