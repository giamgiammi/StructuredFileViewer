package com.github.giamgiammi.StructuredFileViewer.ui.table;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.model.Filter;
import com.github.giamgiammi.StructuredFileViewer.model.FilterType;
import com.github.giamgiammi.StructuredFileViewer.utils.TextUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class TableDataController {
    private final ResourceBundle bundle = App.getBundle();
    private DataModel<?, TableLikeData> model;
    private TableLikeData data;

    private List<Filter> filters;

    @FXML
    private TableView<TableLikeData.Record> tableView;

    /**
     * Sets the data and data model for the table, then refreshes the table view with the new data.
     *
     * @param data the table-like data structure to be displayed; must not be null
     * @param model the data model associated with the provided table-like data; must not be null
     */
    public void setData(@NonNull TableLikeData data, @NonNull DataModel<?, TableLikeData> model) {
        log.info("Loading table data: data={}, model={}", data, model);
        this.data = data;
        this.model = model;
        refreshData();
    }

    private void resetColumns() {
        val alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(tableView.getScene().getWindow());
        alert.setTitle(bundle.getString("table.reset_columns"));
        alert.setHeaderText(bundle.getString("table.reset_columns.header"));
        alert.setContentText(bundle.getString("table.reset_columns.content"));
        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == okButton) {
                filters = null;
                refreshData();
            }
        });
    }

    private void addFilter(int column, FilterType type, String pattern) {
        if (filters == null) filters = IntStream.range(0, data.getColumnNames().size()).mapToObj(i -> (Filter) null).collect(Collectors.toList());
        var filter = new Filter(type, pattern);
        filters.set(column, filter);
    }

    private ObservableList<TableLikeData.Record> getFilteredRecords() {
        if (filters == null) return FXCollections.observableArrayList(data.getRecords());
        var stream = data.getRecords().stream();
        for (int i = 0; i < filters.size(); i++) {
            val filter = filters.get(i);
            if (filter != null) {
                int finalI = i;
                stream = stream.filter(record -> filter.type().test(filter.pattern(), record.get(finalI)));
            }
        }
        return stream.collect(Collectors.toCollection(FXCollections::observableArrayList));
    }

    private void refreshData() {
        val columns = IntStream.range(0, data.getColumnNames().size())
                        .mapToObj(i -> {
                            var name = data.getColumnNames().get(i);
                            if (TextUtils.isEmpty(name)) name = new MessageFormat(bundle.getString("table.column_n")).format(new Object[]{i + 1});
                            val col = new TableColumn<TableLikeData.Record, String>(name);
                            col.setCellValueFactory(cell -> {
                                val value = cell.getValue().get(i);
                                return new SimpleStringProperty(value);
                            });
                            col.setCellFactory(param -> {
                                val cell = new TableCell<TableLikeData.Record, String>() {
                                    @Override
                                    protected void updateItem(String item, boolean empty) {
                                        setText(empty ? null : item);
                                    }
                                };

                                val menu = new ContextMenu();
                                val copy = new MenuItem(bundle.getString("label.copy"));
                                copy.setOnAction(evt -> {
                                    val clip = Clipboard.getSystemClipboard();
                                    val content = new ClipboardContent();
                                    content.putString(cell.getText());
                                    clip.setContent(content);
                                });
                                val filterEq = new MenuItem(bundle.getString("table.filter_eq"));
                                filterEq.setOnAction(evt -> {
                                    addFilter(i, FilterType.EQUALS, cell.getText());
                                    refreshData();
                                });
                                val filterContains = new MenuItem(bundle.getString("table.filter_contains"));
                                filterContains.setOnAction(evt -> {
                                    addFilter(i, FilterType.CONTAINS, cell.getText());
                                    refreshData();
                                });

                                val reset = new MenuItem(bundle.getString("table.reset_columns"));
                                reset.setOnAction(evt -> resetColumns());

                                menu.getItems().setAll(copy, new SeparatorMenuItem(), filterEq,
                                        filterContains, new SeparatorMenuItem(), reset);
                                cell.setContextMenu(menu);
                                return cell;
                            });

                            val menu = new ContextMenu();
                            val reset = new MenuItem(bundle.getString("table.reset_columns"));
                            reset.setOnAction(evt -> resetColumns());

                            menu.getItems().setAll(reset);

                            col.setContextMenu(menu);
                            return col;
                        }).toList();
        tableView.getColumns().setAll(columns);
        tableView.setItems(getFilteredRecords());
    }
}
