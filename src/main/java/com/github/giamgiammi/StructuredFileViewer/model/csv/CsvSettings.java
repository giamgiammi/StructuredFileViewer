package com.github.giamgiammi.StructuredFileViewer.model.csv;

import lombok.Builder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;

import java.nio.charset.Charset;

/**
 * Settings class for CSV-like files.
 * This is assumed to be used with apache-common csv library.
 */
@Builder(toBuilder = true)
public final record CsvSettings(
        CSVFormat baseFormat,
        String delimiter,
        Character quote,
        String recordSeparator,
        Boolean ignoreEmptyLines,
        DuplicateHeaderMode duplicateHeaderMode,
        Boolean allowMissingColumnNames,
        Boolean trailingData,
        Boolean lenientEof,
        QuoteMode quoteMode,
        Charset charset
) {}
