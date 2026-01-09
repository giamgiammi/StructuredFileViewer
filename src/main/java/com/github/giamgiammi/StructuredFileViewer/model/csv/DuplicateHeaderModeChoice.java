package com.github.giamgiammi.StructuredFileViewer.model.csv;

import org.apache.commons.csv.DuplicateHeaderMode;

public record DuplicateHeaderModeChoice(
        DuplicateHeaderMode mode
) {
    @Override
    public String toString() {
        if (mode == null) return "";
        return mode.name();
    }
}
