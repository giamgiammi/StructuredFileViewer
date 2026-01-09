package com.github.giamgiammi.StructuredFileViewer.core.csv;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import javafx.scene.control.Dialog;
import lombok.val;
import org.apache.commons.csv.CSVFormat;

public class CsvDataModelFactory implements DataModelFactory<CsvSettings> {
    @Override
    public Dialog<CsvSettings> getSettingsDialog(CsvSettings settings) {
        //todo implementation
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Override
    public DataModel<CsvSettings> create(CsvSettings settings) {
        return new CsvDataModel(settings, createFormat(settings));
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
