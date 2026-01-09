package com.github.giamgiammi.StructuredFileViewer.model.csv;

import lombok.Data;
import lombok.NonNull;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Settings class for CSV-like files.
 * This is assumed to be used with apache-common csv library.
 */
@Data
public final class CsvSettings {
    @NonNull
    private final CSVFormat baseFormat = CSVFormat.DEFAULT;
    private final String delimiter;
    private final Character quote;
    private final String recordSeparator;
    private final Boolean ignoreEmptyLines;
    private final DuplicateHeaderMode duplicateHeaderMode;
    private final Boolean allowMissingColumnNames;
    private final Boolean trailingData;
    private final Boolean lenientEof;
    private final QuoteMode quoteMode;

    @NonNull
    private final Charset charset = StandardCharsets.UTF_8;
}
