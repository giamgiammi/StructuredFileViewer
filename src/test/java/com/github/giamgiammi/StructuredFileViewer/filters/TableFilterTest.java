package com.github.giamgiammi.StructuredFileViewer.filters;

import com.github.giamgiammi.StructuredFileViewer.model.SimpleTableData;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableFilterTest {
    @Test
    void expr1Test() {
        val query = "col1 = 'a' AND ($2 > 'h' OR $0 > '3')";

        val data = new SimpleTableData(List.of("col1", "col2"), List.of(
                new String[] {"a", "aaaa"},
                new String[] {"a", "iiiii"},
                new String[] {"a", "ccccc"},
                new String[] {"a", "dddd"},
                new String[] {"a", "eeeee"},
                new String[] {"b", "fffff"},
                new String[] {"b", "ggggg"}
        ));

        val expected = new SimpleTableData(data.getColumnNames(), List.of(
                new String[] {"a", "iiiii"},
                new String[] {"a", "dddd"},
                new String[] {"a", "eeeee"}
        ));

        val filter = TableFilter.parse(query, data.getColumnNames());

        val result = filter.filter(data.getRecords());
        assertEquals(expected.getRecords(), result);
    }
}