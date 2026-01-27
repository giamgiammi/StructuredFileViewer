package com.github.giamgiammi.StructuredFileViewer.filters;

import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.filters.generated.TableFiltersBaseVisitor;
import com.github.giamgiammi.StructuredFileViewer.filters.generated.TableFiltersParser;
import com.github.giamgiammi.StructuredFileViewer.utils.TextUtils;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class TableFilterVisitor extends TableFiltersBaseVisitor<TableFilter> {
    private final Map<String, Integer> columns;

    public TableFilterVisitor(Iterable<String> columnsNames) {
        boolean canUseNames = true;
        val map = new HashMap<String, Integer>();

        if (columnsNames != null) {
            int i = 0;
            for (val name : columnsNames) {
                if (name == null || map.containsKey(name)) {
                    canUseNames = false;
                    break;
                }
                map.put(name, i++);
            }
            canUseNames = canUseNames && i > 0;
        } else {
            canUseNames = false;
        }

        columns = canUseNames ? map : null;
    }

    @Override
    public TableFilter visitExpr(TableFiltersParser.ExprContext ctx) {
        return visit(ctx.orExpr());
    }

    @Override
    public TableFilter visitOrExpr(TableFiltersParser.OrExprContext ctx) {
        val expressions = ctx.andExpr().stream().map(this::visit).toList();
        return (r, i) -> expressions.stream().anyMatch(f -> f.test(r, i));
    }

    @Override
    public TableFilter visitAndExpr(TableFiltersParser.AndExprContext ctx) {
        val expressions = ctx.notExpr().stream().map(this::visit).toList();
        return (r, i) -> expressions.stream().allMatch(f -> f.test(r, i));
    }

    @Override
    public TableFilter visitNotExpr(TableFiltersParser.NotExprContext ctx) {
        if (ctx.NOT() != null) {
            val filter = visit(ctx.notExpr());
            return (r, i) -> !filter.test(r, i);
        }
        return visit(ctx.primary());
    }

    @Override
    public TableFilter visitPrimary(TableFiltersParser.PrimaryContext ctx) {
        val expr = ctx.expr();
        if (expr != null) return visit(expr);
        return visit(ctx.comparison());
    }

    @Override
    public TableFilter visitComparison(TableFiltersParser.ComparisonContext ctx) {
        val value = extractValue(ctx.value());
        if (value != null) {
            val op = ctx.op().getText().toUpperCase();
            val col = ctx.column(0);
            val colIndex = extractColumn(col);

            final ColumnValueSupplier colValue;
            if (colIndex < 0) colValue = (r, i) -> String.valueOf(i + 1);
            else colValue = (r, i) -> Objects.toString(r.get(colIndex), null);

            return switch (op) {
                case "=" -> (r, i) -> Objects.equals(colValue.get(r, i), value);
                case "<>" -> (r, i) -> !Objects.equals(colValue.get(r, i), value);
                case ">" -> (r, i) -> Objects.compare(colValue.get(r, i), value, TextUtils::safeCompare) > 0;
                case ">=" -> (r, i) -> Objects.compare(colValue.get(r, i), value, TextUtils::safeCompare) >= 0;
                case "<" -> (r, i) -> Objects.compare(colValue.get(r, i), value, TextUtils::safeCompare) < 0;
                case "<=" -> (r, i) -> Objects.compare(colValue.get(r, i), value, TextUtils::safeCompare) <= 0;
                case "LIKE" -> (r, i) -> TextUtils.contains(value, colValue.get(r, i));
                case "ILIKE" -> (r, i) -> TextUtils.containsIgnoreCase(value, colValue.get(r, i));
                case "REGEX" -> {
                    val pattern = Pattern.compile(value);
                    yield (r, i) -> pattern.matcher(colValue.get(r, i)).matches();
                }
                default -> throw new IllegalArgumentException("Invalid operator: " + op);
            };
        } else {
            val op = ctx.op().getText().toUpperCase();

            val col1 = ctx.column(0);
            val colIndex1 = extractColumn(col1);

            val col2 = ctx.column(1);
            val colIndex2 = extractColumn(col2);

            final ColumnValueSupplier colValue1;
            if (colIndex1 < 0) colValue1 = (r, i) -> String.valueOf(i + 1);
            else colValue1 = (r, i) -> Objects.toString(r.get(colIndex1), null);

            final ColumnValueSupplier colValue2;
            if (colIndex2 < 0) colValue2 = (r, i) -> String.valueOf(i + 1);
            else colValue2 = (r, i) -> Objects.toString(r.get(colIndex2), null);

            return switch (op) {
                case "=" -> (r, i) -> Objects.equals(colValue1.get(r, i), colValue2.get(r, i));
                case "<>" -> (r, i) -> !Objects.equals(colValue1.get(r, i), colValue2.get(r, i));
                case ">" -> (r, i) -> Objects.compare(colValue1.get(r, i), colValue2.get(r, i), TextUtils::safeCompare) > 0;
                case ">=" -> (r, i) -> Objects.compare(colValue1.get(r, i), colValue2.get(r, i), TextUtils::safeCompare) >= 0;
                case "<" -> (r, i) -> Objects.compare(colValue1.get(r, i), colValue2.get(r, i), TextUtils::safeCompare) < 0;
                case "<=" -> (r, i) -> Objects.compare(colValue1.get(r, i), colValue2.get(r, i), TextUtils::safeCompare) <= 0;
                case "LIKE" -> (r, i) -> TextUtils.contains(colValue2.get(r, i), colValue1.get(r, i));
                case "ILIKE" -> (r, i) -> TextUtils.containsIgnoreCase(colValue2.get(r, i), colValue1.get(r, i));
                case "REGEX" -> (r, i) -> colValue1.get(r, i).matches(colValue2.get(r, i));
                default -> throw new IllegalArgumentException("Invalid operator: " + op);
            };
        }
    }

    private int extractColumn(TableFiltersParser.ColumnContext ctx) {
        if (ctx == null) throw new IllegalArgumentException("No column provided");
        if (ctx.DOLLAR_INDEX() != null) {
            val col = ctx.DOLLAR_INDEX().getText();
            try {
                return Integer.parseInt(col.substring(1)) - 1;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid column index: " + col);
            }
        }
        String name;
        if (ctx.QUOTED_IDENT() != null) name = ctx.QUOTED_IDENT()
                .getText()
                .substring(1, ctx.QUOTED_IDENT().getText().length() - 1)
                .replace("\\", "\"")
                .replace("\\\\", "\\");
        else if (ctx.IDENT() != null) name = ctx.IDENT().getText();
        else throw new IllegalArgumentException("Invalid column name: " + ctx.getText());
        val i = columns.get(name);
        if (i == null) throw new IllegalArgumentException("Unknown column: " + name);
        return i;
    }

    private String extractValue(TableFiltersParser.ValueContext ctx) {
        if (ctx == null) return null;
        var text = ctx.STRING().getText();
        text = text.substring(1, text.length() - 1);
        text = text.replace("\\'", "'");
        text = text.replace("\\\\", "\\");
        return text;
    }

    static void main() {
        val query = "\"col1\" = 'a' AND $2 = 'b'";
        val names = List.of("col1", "col2");

        System.out.println(TableFilter.parse(query, names));
    }

    private interface ColumnValueSupplier {
        String get(TableLikeData.Record record, int rowIndex);
    }
}
