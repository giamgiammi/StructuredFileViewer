package com.github.giamgiammi.StructuredFileViewer.core.csv;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvTableData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.csv.CSVFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * A concrete implementation of the {@link DataModel} interface designed
 * for parsing and managing CSV (Comma-Separated Values) files. This class
 * uses {@link CsvSettings} for configuration and produces table-like data
 * represented by {@link TableLikeData}.
 *
 * The parsing logic leverages the Apache Commons CSV library to read and
 * process CSV data streams or string inputs while adhering to the settings
 * provided. The resulting data is structured into a table format with
 * column names and rows.
 *
 * This class encapsulates:
 * - CSV format and processing rules defined by {@link CsvSettings} and {@link CSVFormat}.
 * - An implementation to map parsed data into table-like structures.
 */
@RequiredArgsConstructor
public class CsvDataModel implements DataModel<CsvSettings, TableLikeData> {
    @NonNull
    private final CsvSettings settings;
    @NonNull
    private final CSVFormat format;

    @Override
    public @NonNull Class<? extends DataModelFactory<CsvSettings, TableLikeData>> getFactoryClass() {
        return CsvDataModelFactory.class;
    }

    @Override
    public @NonNull CsvSettings getSettings() {
        return settings;
    }

    @Override
    public @NonNull TableLikeData parse(@NonNull InputStream stream) throws IOException {
        try (val reader = new InputStreamReader(stream, settings.charset())) {
            return parse(reader);
        }
    }

    @Override
    public @NonNull TableLikeData parse(@NonNull String text) throws IOException, UnsupportedOperationException {
        try (val reader = new StringReader(text)) {
            return parse(reader);
        }
    }


    /**
     * Parse data from the provided reader
     * @param reader the reader to read from
     * @return the parsed data
     * @throws IOException if an I/O error occurs while reading
     */
    private TableLikeData parse(Reader reader) throws IOException {
        val parser = format.parse(reader);

        val columns = parser.getHeaderNames();
        val items = new ArrayList<String[]>();

        for (val record : parser) {
            if (!columns.isEmpty()) {
                val item = new String[columns.size()];
                for (int i = 0; i < columns.size(); i++) {
                    item[i] = record.get(i);
                }
                items.add(item);
            } else {
                val item = new ArrayList<String>();
                for (val value : record) {
                    item.add(value);
                }
                items.add(item.toArray(String[]::new));
            }
        }

        if (columns.isEmpty()) {
            val n = items.stream().mapToInt(a -> a.length).max().orElse(0);
            return new CsvTableData(IntStream.range(0, n).mapToObj(i -> "").toList(), items);
        }

        return new CsvTableData(columns, items);
    }

    @Override
    public String toString() {
        return "CsvDataModel{settings=%s}".formatted(settings);
    }
}
