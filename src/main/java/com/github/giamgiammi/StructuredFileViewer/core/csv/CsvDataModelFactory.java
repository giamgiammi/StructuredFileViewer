package com.github.giamgiammi.StructuredFileViewer.core.csv;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvData;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.csv.CSVFormat;

import java.nio.charset.StandardCharsets;

public class CsvDataModelFactory implements DataModelFactory<CsvSettings, CsvData> {
    private static final CsvSettings DEFAULT_SETTINGS = CsvSettings.builder()
            .baseFormat(CSVFormat.DEFAULT)
            .charset(StandardCharsets.UTF_8)
            .build();

    @Override
    public @NonNull DataModelType getType() {
        return DataModelType.CSV_LIKE;
    }

    @Override
    public @NonNull CsvSettings getDefaultSettings() {
        return DEFAULT_SETTINGS;
    }

    @Override
    public @NonNull DataModel<CsvSettings, CsvData> create(@NonNull CsvSettings csvSettings) {
        return new CsvDataModel(csvSettings, createFormat(csvSettings));
    }

    /**
     * Creates a customized CSVFormat instance based on the provided CsvSettings.
     *
     * @param s the CsvSettings instance containing the configuration for the CSV format
     * @return a CSVFormat instance customized based on the provided CsvSettings
     */
    private CSVFormat createFormat(CsvSettings s) {
        val b = s.baseFormat().builder();

        if (s.delimiter() != null) b.setDelimiter(s.delimiter());
        if (s.quote() != null) b.setQuote(s.quote());
        if (s.recordSeparator() != null) b.setRecordSeparator(s.recordSeparator());
        if (s.ignoreEmptyLines() != null) b.setIgnoreEmptyLines(s.ignoreEmptyLines());
        if (s.duplicateHeaderMode() != null) b.setDuplicateHeaderMode(s.duplicateHeaderMode());
        if (s.allowMissingColumnNames() != null) b.setAllowMissingColumnNames(s.allowMissingColumnNames());
        if (s.trailingData() != null) b.setTrailingData(s.trailingData());
        if (s.lenientEof() != null) b.setLenientEof(s.lenientEof());
        if (s.quoteMode() != null) b.setQuoteMode(s.quoteMode());

        return b.get();
    }
}
