package com.github.giamgiammi.StructuredFileViewer.core;

import lombok.NonNull;

import java.util.List;

/**
 * Represents a table-like data structure with rows and columns.
 * This interface provides methods to access the column names and the data records stored
 * in this structure. Each record contains values corresponding to all the columns.
 */
public interface TableLikeData {
    /**
     * Retrieves the names of all columns in the table-like data structure.
     * This method provides a list of column names in the order they are stored.
     *
     * @return a list of non-null column names; never null
     */
    @NonNull
    List<String> getColumnNames();

    /**
     * Retrieves the records from the table-like data structure.
     * Each record contains values for all columns in the structure.
     *
     * @return a list of non-null {@code Record} instances; never null
     */
    @NonNull
    List<Record> getRecords();

    /**
     * Represents a single record in a table-like data structure.
     * A record contains values for all columns in the structure and
     * provides methods to retrieve these values either by their column index
     * or by their column name.
     */
    interface Record {
        /**
         * Retrieves the value of the record at the specified column index.
         *
         * @param column the zero-based index of the column for which the value is to be retrieved
         * @return the value of the record at the specified column index
         * @throws IndexOutOfBoundsException if the specified column index is outside the valid range
         */
        Object get(int column);
    }
}
