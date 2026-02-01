package com.github.giamgiammi.StructuredFileViewer.model.updater;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a version string in a dot-separated format (e.g., "1.2.3") and provides
 * methods for parsing, comparing, and accessing version information.
 * <p>
 * This class is immutable and thread-safe.
 * <p>
 * Key responsibilities:
 * - Parse version strings into numeric components.
 * - Provide access to the version as a dot-separated string or an array of numeric components.
 * - Implement comparison logic to compare versions based on their numeric components.
 */
@Slf4j
@EqualsAndHashCode
public final class Version implements Comparable<Version> {
    private final long[] numbers;

    /**
     * Constructs a new {@code Version} instance by parsing a version string into its numeric components.
     * The version string is expected to be in a dot-separated format (e.g., "1.2.3"), where each segment
     * represents a numeric value. If the parsing fails, an {@code IllegalArgumentException} is thrown.
     *
     * @param version the dot-separated version string to parse; must not be null or empty
     * @throws IllegalArgumentException if the version string cannot be parsed into numeric components
     * @throws NullPointerException if the version string is null
     */
    public Version(@NonNull String version) {
        if (version.isBlank()) throw new IllegalArgumentException("Version string cannot be blank");
        try {
            numbers = Arrays.stream(version.split("\\."))
                    .mapToLong(Long::parseLong)
                    .toArray();
        } catch (Exception e) {
            log.error("Failed to parse version {}", version, e);
            throw new IllegalArgumentException("Failed to parse version: " + version, e);
        }
    }

    /**
     * Returns the version as a dot-separated string representation (e.g., "1.2.3").
     *
     * @return the version string derived from the numeric components of this {@code Version} instance
     */
    public String getVersion() {
        return Arrays.stream(numbers)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining("."));
    }

    /**
     * Returns the version as an array of numeric components, where each component
     * corresponds to a segment of the version string parsed as a number.
     *
     * @return a copy of the internal array of numeric components representing the version
     */
    public long[] getVersionAsNumbers() {
        return Arrays.copyOf(numbers, numbers.length);
    }

    @Override
    public int compareTo(@NonNull Version v) {
        val len = Math.min(numbers.length, v.numbers.length);
        for (int i = 0; i < len; i++) {
            val diff = Long.compare(numbers[i], v.numbers[i]);
            if (diff != 0) return diff;
        }
        return Long.compare(numbers.length, v.numbers.length);
    }

    @Override
    public String toString() {
        return "Version[%s]".formatted(getVersion());
    }
}
