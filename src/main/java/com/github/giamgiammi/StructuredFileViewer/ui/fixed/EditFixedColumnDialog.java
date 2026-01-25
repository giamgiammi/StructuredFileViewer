package com.github.giamgiammi.StructuredFileViewer.ui.fixed;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.model.fixed.FixedWidthColumn;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;
import lombok.val;

public class EditFixedColumnDialog extends Dialog<FixedWidthColumn> {
    public EditFixedColumnDialog(Window window, FixedWidthColumn column) {
        initOwner(window);

        val bundle = App.getBundle();
        setTitle(bundle.getString("fixed.settings.edit_column.title"));
        setHeaderText(bundle.getString("fixed.settings.edit_column.header"));

        val grid = new GridPane(5, 5);

        grid.add(new Label(bundle.getString("fixed.settings.column.name")), 0, 0);
        val name = new TextField(column != null ? column.name() : "");
        grid.add(name, 1, 0);

        grid.add(new Label(bundle.getString("fixed.settings.column.length")), 0, 1);
        val length = new TextField();
        length.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        length.setText(column != null ? Integer.toString(column.length()) : "");
        grid.add(length, 1, 1);

        val trim = new CheckBox(bundle.getString("fixed.settings.column.trim"));
        trim.setSelected(column != null && column.trim());
        grid.add(trim, 0, 2, 2, 1);

        getDialogPane().setContent(grid);

        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        setResultConverter(btn -> {
            if (btn == okButton)
                return new FixedWidthColumn(name.getText(), Integer.parseInt(length.getText()), trim.isSelected());
            return null;
        });
    }
}
