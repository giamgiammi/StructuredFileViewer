package com.github.giamgiammi.StructuredFileViewer.model.csv;

import com.github.giamgiammi.StructuredFileViewer.core.csv.CsvBaseFormat;
import lombok.Builder;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Settings class for CSV-like files.
 * This is assumed to be used with apache-common csv library.
 */
@Builder(toBuilder = true)
public final record CsvSettings(
        CsvBaseFormat baseFormat,
        String delimiter,
        Character quote,
        String recordSeparator,
        Boolean ignoreEmptyLines,
        DuplicateHeaderMode duplicateHeaderMode,
        Boolean allowMissingColumnNames,
        Boolean trailingData,
        Boolean lenientEof,
        QuoteMode quoteMode,
        Boolean skipHeaderRecord,
        List<String> customHeaderNames,
        Charset charset
) {}
