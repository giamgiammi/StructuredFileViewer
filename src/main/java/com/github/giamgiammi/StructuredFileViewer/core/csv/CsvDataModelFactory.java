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
        val b = s.getBaseFormat().builder();

        if (s.getDelimiter() != null) b.setDelimiter(s.getDelimiter());
        if (s.getQuote() != null) b.setQuote(s.getQuote());
        if (s.getRecordSeparator() != null) b.setRecordSeparator(s.getRecordSeparator());
        if (s.getIgnoreEmptyLines() != null) b.setIgnoreEmptyLines(s.getIgnoreEmptyLines());
        if (s.getDuplicateHeaderMode() != null) b.setDuplicateHeaderMode(s.getDuplicateHeaderMode());
        if (s.getAllowMissingColumnNames() != null) b.setAllowMissingColumnNames(s.getAllowMissingColumnNames());
        if (s.getTrailingData() != null) b.setTrailingData(s.getTrailingData());
        if (s.getLenientEof() != null) b.setLenientEof(s.getLenientEof());
        if (s.getQuoteMode() != null) b.setQuoteMode(s.getQuoteMode());

        return b.get();
    }
}
