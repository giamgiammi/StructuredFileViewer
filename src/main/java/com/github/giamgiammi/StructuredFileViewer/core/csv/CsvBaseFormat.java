package com.github.giamgiammi.StructuredFileViewer.core.csv;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;

/**
 * Enumeration for representing common base CSV formats using the {@link CSVFormat} library.
 * Each enum constant corresponds to a predefined CSV format from the Apache Commons CSV library.
 *
 * The purpose of this enum is to provide a convenient abstraction for selecting a base
 * configuration suitable for parsing or writing CSV files. These formats can be further customized
 * if needed, allowing flexibility in handling various CSV-like data.
 *
 * Each enum constant wraps a specific {@link CSVFormat} instance that can be further accessed and
 * used for CSV operations. This design enables mapping between high-level enum constants and the
 * configurations of the Apache Commons CSV library.
 */
@RequiredArgsConstructor
@Getter
public enum CsvBaseFormat {
    DEFAULT(CSVFormat.DEFAULT),
    EXCEL(CSVFormat.EXCEL),
    INFORMIX_UNLOAD(CSVFormat.INFORMIX_UNLOAD),
    INFORMIX_UNLOAD_CSV(CSVFormat.INFORMIX_UNLOAD_CSV),
    MONGODB_CSV(CSVFormat.MONGODB_CSV),
    MONGODB_TSV(CSVFormat.MONGODB_TSV),
    MYSQL(CSVFormat.MYSQL),
    ORACLE(CSVFormat.ORACLE),
    POSTGRESQL_CSV(CSVFormat.POSTGRESQL_CSV),
    POSTGRESQL_TEXT(CSVFormat.POSTGRESQL_TEXT),
    RFC4180(CSVFormat.RFC4180),
    TDF(CSVFormat.TDF);

    private final CSVFormat format;
}
