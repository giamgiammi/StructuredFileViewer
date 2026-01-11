package com.github.giamgiammi.StructuredFileViewer.task;

import java.io.IOException;

/**
 * Represents an exception that is thrown when an error occurs during the parsing
 * of a file. This exception extends {@link IOException}, indicating that it is
 * related to input/output operations.
 *
 * ParsingFileException can be used to encapsulate additional context or errors
 * encountered while processing a file, such as issues related to file format,
 * content, or encoding mismatches.
 *
 * It provides constructors for creating an exception with a custom message and,
 * optionally, a cause, which can be another throwable that led to this exception.
 */
public class ParsingFileException extends IOException {
    public ParsingFileException(String message) {
        super(message);
    }

    public ParsingFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
