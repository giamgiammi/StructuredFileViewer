package com.github.giamgiammi.StructuredFileViewer.model;

import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;

public record ModelChoice(
        DataModelType type,
        String text
) {
    @Override
    public String toString() {
        return text;
    }
}
