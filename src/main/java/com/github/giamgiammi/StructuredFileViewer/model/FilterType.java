package com.github.giamgiammi.StructuredFileViewer.model;

import java.util.Objects;

public enum FilterType {
    EQUALS {
        @Override
        public boolean test(String pattern, String value) {
            return Objects.equals(pattern, value);
        }
    },
    CONTAINS {
        @Override
        public boolean test(String pattern, String value) {
            if (value == null) return pattern == null;
            if (pattern == null) return false;
            return value.contains(pattern);
        }
    },
    DIFFERS {
        @Override
        public boolean test(String pattern, String value) {
            return !Objects.equals(pattern, value);
        }
    }
    ;

    public abstract boolean test(String pattern, String value);
}
