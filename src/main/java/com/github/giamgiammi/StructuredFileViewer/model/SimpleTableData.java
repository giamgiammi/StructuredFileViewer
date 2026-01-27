package com.github.giamgiammi.StructuredFileViewer.model;

import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import java.util.List;

/**
 * Represents a simple table-like data structure that supports column-based and record-based data manipulation.
 * This implementation is immutable and ensures that the column names and records are preserved as read-only lists.
 * Each record in the table is represented as an instance of the inner CsvRecord class, which implements the
 * {@link TableLikeData.Record} interface.
 *
 * The table consists of a fixed list of column names and a corresponding list of data records. Each record contains
 * an array of values that align with the specified column names.
 *
 * Immutable properties:
 * - {@code columnNames}: The list of column names in the table.
 * - {@code records}: The list of records in the table, with each record corresponding to a row of data.
 *
 * Thread safety:
 * - The class is thread-safe as it is immutable.
 *
 * Responsibility:
 * - Provide access to column names and table records.
 * - Ensure data integrity and immutability across the table.
 */
@Getter
@EqualsAndHashCode
public final class SimpleTableData implements TableLikeData {
    private final List<String> columnNames;
    private final List<TableLikeData.Record> records;

    public SimpleTableData(@NonNull List<String> columnNames, @NonNull List<String[]> records) {
        this.columnNames = List.copyOf(columnNames);
        this.records = records.stream()
                .map(SimpleRecord::new)
                .map(TableLikeData.Record.class::cast)
                .toList();
    }

    @Override
    public String toString() {
        return String.format("%s{columnNames=%s, size=%d}", getClass().getSimpleName(), columnNames, records.size());
    }

    @Getter
    @EqualsAndHashCode
    private final class SimpleRecord implements TableLikeData.Record {
        private final String[] values;

        public SimpleRecord(@NonNull String[] values) {
            this.values = values;
        }

        @Override
        public String get(int column) {
            if (column < 0 || column >= values.length) return null;
            return values[column];
        }

        @Override
        public String toString() {
            val b = new StringBuilder()
                    .append(getClass().getSimpleName())
                    .append('{');
            if (!columnNames.isEmpty()) {
                for (int i = 0; i < columnNames.size(); i++) {
                    b.append(columnNames.get(i))
                            .append('=')
                            .append(values[i]);
                    if (i < columnNames.size() - 1) b.append(", ");
                }
            } else {
                for (int i = 0; i < values.length; i++) {
                    b.append(values[i]);
                    if (i < values.length - 1) b.append(", ");
                }
            }
            return b.append('}').toString();
        }
    }
}
