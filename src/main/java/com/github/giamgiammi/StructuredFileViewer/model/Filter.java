package com.github.giamgiammi.StructuredFileViewer.model;

import lombok.NonNull;

public record Filter(
        @NonNull
        FilterType type,
        String pattern
        ) {
}
