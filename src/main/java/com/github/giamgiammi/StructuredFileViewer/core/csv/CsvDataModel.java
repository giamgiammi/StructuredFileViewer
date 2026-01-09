package com.github.giamgiammi.StructuredFileViewer.core.csv;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvData;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

@RequiredArgsConstructor
public class CsvDataModel implements DataModel<CsvSettings, CsvData> {
    @NonNull
    private final CsvSettings settings;
    @NonNull
    private final CSVFormat format;

    @Override
    public @NonNull Class<? extends DataModelFactory<CsvSettings, CsvData>> getFactoryClass() {
        return CsvDataModelFactory.class;
    }

    @Override
    public @NonNull CsvSettings getSettings() {
        return settings;
    }

    @Override
    public @NonNull CsvData parse(@NonNull Reader reader) throws IOException {
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
}
