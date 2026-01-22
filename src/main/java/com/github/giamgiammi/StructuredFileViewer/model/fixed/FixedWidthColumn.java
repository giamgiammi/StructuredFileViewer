package com.github.giamgiammi.StructuredFileViewer.model.fixed;

import lombok.Builder;

@Builder(toBuilder = true)
public record FixedWidthColumn(
        String name,
        int length,
        boolean trim
) {
}
