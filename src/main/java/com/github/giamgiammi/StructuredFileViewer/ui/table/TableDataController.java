package com.github.giamgiammi.StructuredFileViewer.ui.table;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.stream.IntStream;

@Slf4j
public class TableDataController {
    private DataModel<?, TableLikeData> model;
    private TableLikeData data;

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

    private void refreshData() {
        val columns = IntStream.range(0, data.getColumnNames().size())
                        .mapToObj(i -> {
                            val name = data.getColumnNames().get(i);
                            val col = new TableColumn<TableLikeData.Record, String>(name);
                            col.setCellValueFactory(cell -> {
                                val value = cell.getValue().get(i);
                                return new SimpleStringProperty(value);
                            });
                            return col;
                        }).toList();
        tableView.getColumns().setAll(columns);
        tableView.setItems(FXCollections.observableArrayList(data.getRecords()));
    }
}
