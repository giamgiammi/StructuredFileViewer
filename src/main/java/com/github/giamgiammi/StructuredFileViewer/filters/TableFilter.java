package com.github.giamgiammi.StructuredFileViewer.filters;

import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.filters.generated.TableFiltersLexer;
import com.github.giamgiammi.StructuredFileViewer.filters.generated.TableFiltersParser;
import lombok.val;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DiagnosticErrorListener;

import java.util.List;

/**
 * Represents a functional interface that defines conditions for filtering records
 * in a table-like data structure. Implementations of this interface can specify
 * custom logic to evaluate and filter records by overriding the {@code test} method.
 */
public interface TableFilter {
    /**
     * Evaluates a condition on the given record and returns a boolean
     * indicating whether the record satisfies the condition.
     *
     * @param record the record to evaluate; must not be null
     * @return {@code true} if the record satisfies the condition, {@code false} otherwise
     */
    boolean test(TableLikeData.Record record);

    /**
     * Filters a list of {@code TableLikeData.Record} objects based on the condition
     * defined by the {@code test} method.
     *
     * @param records the list of {@code TableLikeData.Record} objects to be filtered; must not be null
     * @return a new list containing only the records that satisfy the condition defined by the {@code test} method
     */
    default List<TableLikeData.Record> filter(List<TableLikeData.Record> records) {
        return records.stream().filter(this::test).toList();
    }

    /**
     * Returns a filter that always returns {@code true}.
     * @return a filter that always returns {@code true}
     */
    static TableFilter empty() {
        return record -> true;
    }

    static TableFilter parse(String query, Iterable<String> columnNames) {
        val stream = CharStreams.fromString(query);
        val lexer = new TableFiltersLexer(stream);
        val tokens = new CommonTokenStream(lexer);
        val parser = new TableFiltersParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new DiagnosticErrorListener());

        val tree = parser.expr();
        //todo read tree, probably using a visitor

        return empty();
    }
}
