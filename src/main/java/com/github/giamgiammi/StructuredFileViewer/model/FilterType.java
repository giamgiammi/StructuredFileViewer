package com.github.giamgiammi.StructuredFileViewer.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FilterType {
    EQUALS("="),
    CONTAINS("LIKE"),
    DIFFERS("<>")
    ;

    private final String code;
}
