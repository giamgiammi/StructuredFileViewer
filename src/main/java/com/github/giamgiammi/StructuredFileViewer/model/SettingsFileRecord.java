package com.github.giamgiammi.StructuredFileViewer.model;

import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import lombok.NonNull;

public record SettingsFileRecord(
        @NonNull
        DataModelType type,
        @NonNull
        Object settings
) {
}
