package com.github.giamgiammi.StructuredFileViewer.core.fixed;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.SimpleTableData;
import com.github.giamgiammi.StructuredFileViewer.model.fixed.FixedWidthColumn;
import com.github.giamgiammi.StructuredFileViewer.model.fixed.FixedWidthSettings;
import com.github.giamgiammi.StructuredFileViewer.utils.TextUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a data model for handling fixed-width formatted data. This implementation
 * uses {@link FixedWidthSettings} as its configuration metadata and produces an instance
 * of {@link SimpleTableData} as the parsed output format.
 *
 * The fixed-width data model assumes a set of columns with predefined widths. It processes
 * data either with or without a new line delimiter between records, depending on the settings.
 *
 * Thread safety:
 * - This class is immutable and inherently thread-safe.
 */
@RequiredArgsConstructor
public class FixedWidthDataModel implements DataModel<FixedWidthSettings, SimpleTableData> {
    private final FixedWidthSettings settings;

    @Override
    public @NonNull Class<? extends DataModelFactory<FixedWidthSettings, SimpleTableData>> getFactoryClass() {
        return FixedWidthDataModelFactory.class;
    }

    @Override
    public @NonNull FixedWidthSettings getSettings() {
        return settings;
    }

    @Override
    public @NonNull SimpleTableData parse(@NonNull InputStream stream) throws IOException {
        try (val reader = new InputStreamReader(stream, settings.charset())) {
            return parse(reader);
        }
    }

    @Override
    public @NonNull SimpleTableData parse(@NonNull String text) throws IOException, UnsupportedOperationException {
        try (val reader = new StringReader(text)) {
            return parse(reader);
        }
    }

    /**
     * Parses data from a given {@link Reader} into a {@link SimpleTableData} representation.
     * The parsing logic depends on whether records in the data end with a newline character.
     *
     * @param reader the {@link Reader} from which the data will be read
     * @return a {@link SimpleTableData} instance containing the parsed table data
     * @throws IOException if an I/O error occurs during reading
     */
    private SimpleTableData parse(Reader reader) throws IOException {
        if (settings.recordEndsWithNewLine()) {
            try (val scanner = new Scanner(reader)) {
                return parseNewLine(scanner);
            }
        } else {
            return parseNoNewLine(reader);
        }
    }

    /**
     * Parses input data from a {@link Scanner} and transforms it into a {@link SimpleTableData} object.
     * Each line of the input data is processed based on the fixed-width column definitions, and the resulting
     * records are stored as rows in the table data structure.
     *
     * @param sc the {@link Scanner} instance used to read and parse the input data line by line
     * @return a {@link SimpleTableData} instance containing the parsed table data
     * @throws IOException if an I/O error occurs during parsing
     */
    private SimpleTableData parseNewLine(Scanner sc) throws IOException {
        val list = new ArrayList<String[]>();

        while (sc.hasNextLine()) {
            val str = sc.nextLine();
            parseLine(list, str);
        }

        return new SimpleTableData(
                settings.columns().stream().map(FixedWidthColumn::name).toList(),
                list
        );
    }

    /**
     * Parses data from a given {@link Reader} into a {@link SimpleTableData} representation
     * where the input data does not contain newline characters. The method reads fixed-width
     * records based on the column definitions specified in the settings and transforms the
     * data into a tabular format.
     *
     * @param reader the {@link Reader} from which data will be read. It must supply the raw
     *               fixed-width input data without newline delimiters.
     * @return a {@link SimpleTableData} instance containing the parsed tabular data.
     * @throws IOException if an I/O error occurs during reading from the {@link Reader}.
     */
    private SimpleTableData parseNoNewLine(Reader reader) throws IOException {
        val lineLength = settings.columns().stream().mapToInt(FixedWidthColumn::length).sum();

        val list = new ArrayList<String[]>();
        while (true) {
            val buffer = new char[lineLength];
            val n = reader.read(buffer);
            val str = new String(buffer, 0, n);
            if (n == -1) break;

            parseLine(list, str);
        }

        return new SimpleTableData(
                settings.columns().stream().map(FixedWidthColumn::name).toList(),
                list
        );
    }

    /**
     * Parses a single line of fixed-width input data into an array of column values and adds it to the provided list.
     * The method uses column definitions from the settings to determine the length and trimming behavior for each column.
     *
     * @param columns the number of columns expected in the input line
     * @param list the list to which the parsed column values will be added; each entry in the list represents a record
     * @param str the input line containing fixed-width data to be parsed
     */
    private void parseLine(List<String[]> list, String str) {
        val size = settings.columns().size();
        val record = new String[size];
        int index = 0;
        for (int i = 0; i < size; i++) {
            val col = settings.columns().get(i);
            var data = TextUtils.substring(str, index, index + col.length());
            if (col.trim()) data = data.trim();
            record[i] = data;
            index += col.length();
        }
        list.add(record);
    }

    @Override
    public String toString() {
        return "%s{settings=%s}".formatted(getClass().getSimpleName(), settings);
    }
}
