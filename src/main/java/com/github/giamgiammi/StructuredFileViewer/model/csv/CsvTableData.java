package com.github.giamgiammi.StructuredFileViewer.model.csv;

import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import java.util.List;

/**
 * Represents a table-like data structure specifically designed for handling data in CSV format.
 * This class provides functionality to store and access column names and corresponding records
 * while ensuring immutability of the data. Records within this table-like structure are individual rows
 * of the CSV, where each cell corresponds to a value for a specific column.
 *
 * This implementation guarantees that the column names and record list are unmodifiable after creation.
 */
@Getter
@EqualsAndHashCode
public final class CsvTableData implements TableLikeData {
    private final List<String> columnNames;
    private final List<TableLikeData.Record> records;

    public CsvTableData(@NonNull List<String> columnNames, @NonNull List<String[]> records) {
        this.columnNames = List.copyOf(columnNames);
        this.records = records.stream()
                .map(CsvRecord::new)
                .map(TableLikeData.Record.class::cast)
                .toList();
    }

    @Override
    public String toString() {
        return String.format("%s{columnNames=%s, size=%d}", getClass().getSimpleName(), columnNames, records.size());
    }

    @Getter
    @EqualsAndHashCode
    private final class CsvRecord implements TableLikeData.Record {
        private final String[] values;

        public CsvRecord(@NonNull String[] values) {
            this.values = values;
        }

        @Override
        public String get(int column) {
            if (column < 0 || column >= values.length) return null;
            return values[column];
        }

        @Override
        public String get(@NonNull String column) {
            for (int i = 0; i < columnNames.size(); i++) {
                if (columnNames.get(i).equals(column)) return get(i);
            }
            return null;
        }

        @Override
        public String toString() {
            val b = new StringBuilder()
                    .append(getClass().getSimpleName())
                    .append('{');
            for (int i = 0; i < columnNames.size(); i++) {
                b.append(columnNames.get(i))
                        .append('=')
                        .append(values[i]);
                if (i < columnNames.size() - 1) b.append(", ");
            }
            return b.append('}').toString();
        }
    }
}
