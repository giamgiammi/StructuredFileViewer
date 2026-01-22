package com.github.giamgiammi.StructuredFileViewer.model.fixed;

import lombok.Builder;

import java.nio.charset.Charset;
import java.util.List;

@Builder(toBuilder = true)
public record FixedWidthSettings(
        List<FixedWidthColumn> columns,
        boolean recordEndsWithNewLine,
        Charset charset
) {
}
