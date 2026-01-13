package com.github.giamgiammi.StructuredFileViewer.task;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Task implementation for parsing a string input into a data structure using a specified {@link DataModel}.
 * This class performs the parsing operation in a background thread, making it suitable for use in asynchronous
 * workflows with structured file viewer applications.
 *
 * The parsing logic leverages the {@link DataModel#parse(String)} method to convert the provided text into the
 * target data structure. An exception is thrown in case of errors during parsing, encapsulated within
 * a {@link ParsingFileException}.
 *
 * @param <DATA> the type of the data structure produced by the parsing operation
 */
@Slf4j
@RequiredArgsConstructor
public class ParseStringTask<DATA> extends Task<DATA> {
    @NonNull
    private final DataModel<?, DATA> model;
    @NonNull
    private final String text;

    @Override
    protected DATA call() throws Exception {
        log.info("Parsing text (length: {}): {}", text.length(), text.substring(0, Math.min(20, text.length())));
        try {
            return model.parse(text);
        } catch (Exception e) {
            log.error("Failed to parse text", e);
            throw new ParsingFileException("Failed to parse text", e);
        }
    }

    @Override
    public String toString() {
        return "ParseStringTask{model=%s, text=%s}".formatted(model, text.substring(0, Math.min(20, text.length())));
    }
}
