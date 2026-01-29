package com.github.giamgiammi.StructuredFileViewer.ui.table;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import com.github.giamgiammi.StructuredFileViewer.filters.TableFilter;
import com.github.giamgiammi.StructuredFileViewer.model.FilterType;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.DataController;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import com.github.giamgiammi.StructuredFileViewer.utils.ListUtils;
import com.github.giamgiammi.StructuredFileViewer.utils.QueryHistory;
import com.github.giamgiammi.StructuredFileViewer.utils.TextUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

@Slf4j
public class TableDataController implements DataController, Initializable {
    private final ResourceBundle bundle = App.getBundle();
    private final QueryHistory queryHistory = new QueryHistory();

    private TableLikeData data;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Button runQueryButton;

    @FXML
    private TextArea queryTextArea;

    @FXML
    private TableView<TableLikeData.Record> tableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        queryTextArea.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
            queryHistory.setUpdating(true);
            try {
                if (evt.isControlDown() && evt.getCode() == KeyCode.ENTER) {
                    updateByFilter();
                } else if (evt.isControlDown() && evt.getCode() == KeyCode.Z) {
                    queryTextArea.setText(queryHistory.getPrevious());
                } else if (evt.isControlDown() && evt.getCode() == KeyCode.Y) {
                    queryTextArea.setText(queryHistory.getNext());
                }
            } finally {
                queryHistory.setUpdating(false);
            }
        });
        queryTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (queryHistory.isUpdating()) return;
            queryHistory.add(newVal);
        });
        runQueryButton.setTooltip(new Tooltip(bundle.getString("label.run_query")));
    }

    public void setData(@NonNull TableLikeData data) {
        log.info("Loading table data: data={}", data);
        this.data = data;
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
                refreshData();
            }
        });
    }

    private void addFilter(int column, FilterType type, String pattern) {
        val comp = "$%d %s '%s'".formatted(
                column + 1,
                type.getCode(),
                pattern.replace("\\", "\\\\")
                        .replace("'", "\\'")
        );
        if (TextUtils.isBlank(queryTextArea.getText())) {
            queryTextArea.setText(comp);
        } else {
            var query = queryTextArea.getText();
            if (query.contains("(") || query.contains(")") || query.toLowerCase().contains("or")) {
                query = "(" + query + ")" + " AND " + comp;
            } else {
                query += " AND " + comp;
            }
            queryTextArea.setText(query);
        }
    }

    private void clearFilters() {
        val alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(tableView.getScene().getWindow());
        alert.setTitle(bundle.getString("table.reset_filters"));
        alert.setHeaderText(bundle.getString("table.reset_filters.header"));
        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getDialogPane().getButtonTypes().setAll(okButton, cancelButton);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == okButton) {
                queryTextArea.setText(null);
                updateByFilter();
            }
        });
    }

    private void updateByFilter() {
        rootPane.setDisable(true);
        val oldContent = runQueryButton.getGraphic();
        runQueryButton.setGraphic(new ProgressIndicator(ProgressIndicator.INDETERMINATE_PROGRESS));
        val task = new Task<ObservableList<TableLikeData.Record>>() {
            @Override
            protected ObservableList<TableLikeData.Record> call() throws Exception {
                return getFilteredRecords();
            }
        };
        task.setOnSucceeded(evt -> {
            tableView.setItems(task.getValue());
            rootPane.setDisable(false);
            runQueryButton.setGraphic(oldContent);
        });
        task.setOnFailed(evt -> {
            log.error("Failed to update table data by filter", task.getException());
            new ExceptionAlert(tableView.getScene().getWindow(), task.getException()).showAndWait();
            rootPane.setDisable(false);
            runQueryButton.setGraphic(oldContent);
        });
        FXUtils.start(task);
    }

    private ObservableList<TableLikeData.Record> getFilteredRecords() {
        val query = queryTextArea.getText();
        if (TextUtils.isBlank(query)) return FXCollections.observableArrayList(data.getRecords());

        val filter = TableFilter.parse(query, data.getColumnNames());
        return FXCollections.observableArrayList(filter.filter(data.getRecords()));
    }

    private void refreshData() {
        rootPane.setDisable(true);
        record TaskResult(List<TableColumn<TableLikeData.Record, Object>> columns, ObservableList<TableLikeData.Record> records) {}
        val task = new Task<TaskResult>() {
            @Override
            protected TaskResult call() throws Exception {
                queryTextArea.setText(null);
                val columns = IntStream.range(0, data.getColumnNames().size())
                        .mapToObj(columnIndex -> {
                            return getTableColumn(columnIndex);
                        }).toList();
                val indexColumn = new TableColumn<TableLikeData.Record, Object>("");
                indexColumn.setCellFactory(param -> new IndexCell());
                val records = getFilteredRecords();
                return new TaskResult(ListUtils.concat(List.of(indexColumn), columns), records);
            }
        };
        task.setOnSucceeded(evt -> {
            tableView.getColumns().setAll(task.getValue().columns);
            tableView.setItems(task.getValue().records);
            rootPane.setDisable(false);
        });
        task.setOnFailed(evt -> {
            log.error("Failed to load table data", task.getException());
            new ExceptionAlert(tableView.getScene().getWindow(), task.getException()).showAndWait();
            rootPane.setDisable(false);
        });
        FXUtils.start(task);
    }

    private MenuItem[] getDefaultMenuItems() {
        val clearFilters = new MenuItem(bundle.getString("table.reset_filters"));
        clearFilters.setOnAction(evt -> {
            clearFilters();
        });
        val reset = new MenuItem(bundle.getString("table.reset_columns"));
        reset.setOnAction(evt -> resetColumns());

        return new MenuItem[]{clearFilters, reset};
    }

    private TableColumn<TableLikeData.Record, Object> getTableColumn(int columnIndex) {
        var name = data.getColumnNames().get(columnIndex);
        if (TextUtils.isEmpty(name)) name = new MessageFormat(bundle.getString("table.column_n")).format(new Object[]{columnIndex + 1});
        val col = new TableColumn<TableLikeData.Record, Object>(name);
        col.setCellValueFactory(cell -> {
            val value = cell.getValue().get(columnIndex);
            return new SimpleObjectProperty<>(value);
        });
        col.setCellFactory(param -> new CustomCell(columnIndex));

        val copy = new MenuItem(bundle.getString("label.copy"));
        copy.setOnAction(evt -> {
            val clip = Clipboard.getSystemClipboard();
            val content = new ClipboardContent();
            content.putString(col.getText());
            clip.setContent(content);
        });

        val menu = new ContextMenu();
        menu.getItems().add(copy);
        menu.getItems().addAll(getDefaultMenuItems());

        col.setContextMenu(menu);
        return col;
    }

    public void handleRunQuery() {
        updateByFilter();
    }

    @RequiredArgsConstructor
    private class CustomCell extends TableCell<TableLikeData.Record, Object> {
        private final int columnIndex;

        @Override
        protected void updateItem(Object obj, boolean empty) {
            if (empty) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else if (obj == null) {
                setText(null);
                setGraphic(null);

                val filterNull = new MenuItem(bundle.getString("table.filter_null"));
                filterNull.setOnAction(evt -> {
                    addFilter(columnIndex, FilterType.EQUALS, null);
                    updateByFilter();
                });

                val menu = new ContextMenu();
                menu.getItems().add(filterNull);
                menu.getItems().addAll(getDefaultMenuItems());
                setContextMenu(menu);
            } else if (obj instanceof String value) {
                setText(null);

                val field = new TextField(value);
                field.setEditable(false);
                field.setPrefColumnCount(Math.min(value.length(), 100));

                val copy = new MenuItem(bundle.getString("label.copy"));
                copy.setOnAction(evt -> {
                    val clip = Clipboard.getSystemClipboard();
                    val content = new ClipboardContent();
                    content.putString(value);
                    clip.setContent(content);
                });
                val filterEq = new MenuItem(bundle.getString("table.filter_eq"));
                filterEq.setOnAction(evt -> {
                    addFilter(columnIndex, FilterType.EQUALS, value);
                    updateByFilter();
                });
                val filterContains = new MenuItem(bundle.getString("table.filter_contains"));
                filterContains.setOnAction(evt -> {
                    addFilter(columnIndex, FilterType.CONTAINS, value);
                    updateByFilter();
                });
                val filterDiff = new MenuItem(bundle.getString("table.filter_diff"));
                filterDiff.setOnAction(evt -> {
                    addFilter(columnIndex, FilterType.DIFFERS, value);
                    updateByFilter();
                });

                val clearFilters = new MenuItem(bundle.getString("table.reset_filters"));
                clearFilters.setOnAction(evt -> {
                    clearFilters();
                });
                val reset = new MenuItem(bundle.getString("table.reset_columns"));
                reset.setOnAction(evt -> resetColumns());

                val menu = new ContextMenu();
                menu.getItems().setAll(copy, new SeparatorMenuItem(), filterEq,
                        filterContains, filterDiff, new SeparatorMenuItem(),
                        clearFilters, reset);
                field.setContextMenu(menu);

                setGraphic(field);
            } else {
                // type-specific view not implemented
                setText(obj.toString());
            }
        }
    }

    private static class IndexCell extends TableCell<TableLikeData.Record, Object> {
        @Override
        protected void updateItem(Object o, boolean empty) {
            //Note: o will always be null
            if (empty) {
                setText(null);
            } else {
                setText(String.valueOf(getTableRow().getIndex() + 1));
            }
        }
    }
}
