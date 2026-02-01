package com.github.giamgiammi.StructuredFileViewer.model.updater;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReleaseDto(
        @JsonProperty("html_url")
        String htmlUrl,
        @JsonProperty("tag_name")
        String tagName,
        String name
) {
}
