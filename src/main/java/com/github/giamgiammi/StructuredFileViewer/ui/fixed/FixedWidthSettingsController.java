package com.github.giamgiammi.StructuredFileViewer.ui.fixed;

import com.github.giamgiammi.StructuredFileViewer.model.fixed.FixedWidthColumn;
import com.github.giamgiammi.StructuredFileViewer.model.fixed.FixedWidthSettings;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.SettingsController;
import com.github.giamgiammi.StructuredFileViewer.utils.TextUtils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import lombok.NonNull;
import lombok.val;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

public class FixedWidthSettingsController implements SettingsController<FixedWidthSettings>, Initializable {
    @FXML
    private GridPane rootPane;

    @FXML
    private CheckBox recordEndsWithNewLine;

    @FXML
    private ComboBox<String> charset;

    @FXML
    private TableView<FixedWidthColumn> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        val nameCol = (TableColumn<FixedWidthColumn, String>) table.getColumns().get(0);
        nameCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().name()));

        val lengthCol = (TableColumn<FixedWidthColumn, Integer>) table.getColumns().get(1);
        lengthCol.setCellValueFactory(param -> new SimpleObjectProperty(param.getValue().length()));

        val trimCol = (TableColumn<FixedWidthColumn, Boolean>) table.getColumns().get(2);
        trimCol.setCellValueFactory(param -> new SimpleBooleanProperty(param.getValue().trim()));
        trimCol.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    val checkbox = new CheckBox();
                    checkbox.setSelected(item);
                    checkbox.setMouseTransparent(true);
                    setGraphic(checkbox);
                }
            }
        });

        charset.getItems().setAll(TextUtils.commonCharsets());
        charset.getSelectionModel().select(0);
    }

    public void handleAddCol() {
        new EditFixedColumnDialog(rootPane.getScene().getWindow(), null)
                .showAndWait()
                .ifPresent(table.getItems()::add);
    }

    public void handleEditCol() {
        val item = table.getSelectionModel().getSelectedItem();
        if (item != null) {
            new EditFixedColumnDialog(rootPane.getScene().getWindow(), item)
                    .showAndWait()
                    .ifPresent(col -> table.getItems()
                            .set(table.getSelectionModel().getSelectedIndex(), col));
        }
    }

    public void handleDeleteCol() {
        val index = table.getSelectionModel().getSelectedIndex();
        if (index >= 0) table.getItems().remove(index);
    }

    @Override
    public @NonNull FixedWidthSettings getSettings() {
        val columns = table.getItems().stream().toList();
        val recordEndsWithNewLine = this.recordEndsWithNewLine.isSelected();
        val charset = Charset.forName(this.charset.getSelectionModel().getSelectedItem());
        return new FixedWidthSettings(columns, recordEndsWithNewLine, charset);
    }

    @Override
    public void setSettings(FixedWidthSettings fixedWidthSettings) {
        table.getItems().setAll(fixedWidthSettings.columns());
        recordEndsWithNewLine.setSelected(fixedWidthSettings.recordEndsWithNewLine());
        charset.getSelectionModel().select(fixedWidthSettings.charset().name());
    }
}
