package com.github.giamgiammi.StructuredFileViewer.model;

import java.nio.file.Path;

/**
 * Represents a message used for inter-instance communication in a single-instance application.
 */
public record InstanceMessage(
        Path[] filesToOpen
) {
}
