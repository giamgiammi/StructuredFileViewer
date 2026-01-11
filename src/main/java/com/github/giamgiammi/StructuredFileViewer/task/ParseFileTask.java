package com.github.giamgiammi.StructuredFileViewer.task;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A task that parses the content of a file or a string using a specified {@link DataModel}.
 * This class extends {@link Task} and encapsulates the core parsing functionality
 * for structured file viewers or data loaders.
 *
 * @param <DATA> the type of the parsed data returned by this task
 */
@Slf4j
public class ParseFileTask<DATA> extends Task<DATA> {
    private final DataModel<?, DATA> model;
    private final Path file;
    private final String fileContent;

    public ParseFileTask(@NonNull DataModel<?, DATA> model, @NonNull Path file) {
        this.model = model;
        this.file = file;
        this.fileContent = null;
    }

    public ParseFileTask(@NonNull DataModel<?, DATA> model, @NonNull String fileContent) {
        this.model = model;
        this.file = null;
        this.fileContent = fileContent;
    }

    @Override
    protected DATA call() throws Exception {
        log.info("Parsing file (file={}, charset={})", getFileRepresentation(), model.getCharset());
        try (val reader = getReader()) {
            return model.parse(reader);
        } catch (Exception e) {
            log.error("Failed to parse file", e);
            throw new ParsingFileException("Failed to parse file", e);
        }
    }

    /**
     * Creates and returns a {@link Reader} for reading the content based on the available data source.
     * If a file is provided, a buffered reader is created using the file's path and the character set
     * from the associated data model. If the file content is provided as a string, a {@link StringReader}
     * is returned.
     *
     * @return a {@link Reader} instance for reading the content of the file or string
     * @throws IOException if an I/O error occurs while creating the reader for a file
     */
    private Reader getReader() throws IOException {
        if (file != null) return Files.newBufferedReader(file, model.getCharset());
        if (fileContent != null) return new java.io.StringReader(fileContent);
        throw new IllegalStateException("both file and fileContent are null; this should never happen");
    }

    /**
     * Generates a string representation of the current file or file content associated with this task.
     * If a file is available, the file's path is returned. If file content is provided as a string,
     * a string indicating the content length is returned.
     *
     * @return a string representing the file path, file content length, or a placeholder indicating no data
     */
    private String getFileRepresentation() {
        if (file != null) return file.toString();
        if (fileContent != null) return "[string(length=%d)]".formatted(fileContent.length());
        return "<none>";
    }
}
