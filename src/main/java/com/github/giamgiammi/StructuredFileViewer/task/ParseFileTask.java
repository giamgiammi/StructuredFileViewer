package com.github.giamgiammi.StructuredFileViewer.task;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Task implementation for parsing the content of a file using a specified {@link DataModel}.
 * This class is designed to handle file parsing in a background thread and allows the result
 * of the parsing to be used once the task completes.
 *
 * @param <DATA> the type of the data structure produced by the parsing operation
 */
@Slf4j
@RequiredArgsConstructor
public class ParseFileTask<DATA> extends Task<DATA> {
    @NonNull
    private final DataModel<?, DATA> model;
    @NonNull
    private final Path file;

    @Override
    protected DATA call() throws Exception {
        log.info("Parsing file {}", file);
        try (val stream = getInputStream()) {
            return model.parse(stream);
        } catch (Exception e) {
            log.error("Failed to parse file", e);
            throw new ParsingFileException("Failed to parse file", e);
        }
    }

    /**
     * Creates and returns an {@link InputStream} to read the content of the file
     * associated with this task. The stream is wrapped with a {@link BufferedInputStream}
     * to improve performance by reducing the number of I/O operations.
     *
     * @return an {@link InputStream} for reading the file content
     * @throws IOException if an I/O error occurs while opening the file
     */
    private InputStream getInputStream() throws IOException {
        return new BufferedInputStream(Files.newInputStream(file));
    }

    @Override
    public String toString() {
        return "ParseFileTask{model=%s, file=%s}".formatted(model, file);
    }
}
