package com.github.giamgiammi.StructuredFileViewer.model.csv;

/**
 * Represents the structure of a CSV file, including its column headers
 * and row data.
 *
 * This class provides a simple way to model tabular data stored in
 * CSV format. It includes an array of column names and a two-dimensional
 * array for the rows of data.
 *
 * The `columns` field contains the names of the columns in the same order
 * as they appear in the CSV file. It is expected that the array length
 * corresponds to the number of columns.
 *
 * The `items` field contains the data rows where each sub-array represents
 * a single row in the CSV file. Each sub-array's length is expected to match
 * the length of the `columns` array.
 */
public record CsvData(
        String[] columns,
        String[][] items
) {
}
