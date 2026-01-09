package com.github.giamgiammi.StructuredFileViewer.core.csv;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvData;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import javafx.scene.Node;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

@RequiredArgsConstructor
public class CsvDataModel implements DataModel<CsvSettings> {
    @NonNull
    private final CsvSettings settings;
    @NonNull
    private final CSVFormat format;

    @Override
    public Class<? extends DataModelFactory<CsvSettings>> getFactoryClass() {
        return CsvDataModelFactory.class;
    }

    @Override
    public CsvSettings getSettings() {
        return settings;
    }

    @Override
    public Node loadFile(Path file) throws IOException {
        final CsvData data;
        try (val reader = Files.newBufferedReader(file, settings.getCharset())) {
            data = parse(reader);
        }

        return createFxNode(data);
    }

    @Override
    public Node loadString(String string) throws IOException, UnsupportedOperationException {
        final CsvData data;
        try (val reader = new StringReader(string)) {
            data = parse(reader);
        }

        return createFxNode(data);

    }

    private CsvData parse(Reader reader) throws IOException {
        val parser = format.parse(reader);

        val columns = parser.getHeaderNames().toArray(String[]::new);
        val items = new ArrayList<String[]>();

        for (val record : parser) {
            val item = new String[columns.length];
            for (int i = 0; i < columns.length; i++) {
                item[i] = record.get(i);
            }
            items.add(item);
        }

        return new CsvData(columns, items.toArray(String[][]::new));
    }

    private Node createFxNode(CsvData data) {
        //todo implementation
        throw new UnsupportedOperationException("not implemented yet");
    }
}
