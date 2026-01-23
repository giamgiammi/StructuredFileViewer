package com.github.giamgiammi.StructuredFileViewer.ui.editsettings;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.LoadResult;
import com.github.giamgiammi.StructuredFileViewer.model.TabData;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.SettingsController;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class EditSettingsDialog extends Dialog<LoadResult<?>> {
    private SettingsController<?> settingsController;
    private DataModelFactory<?, ?> factory;

    public EditSettingsDialog(Window owner, TabData context) {
        initOwner(owner);

        if (context.getModel() == null) throw new IllegalArgumentException("Cannot edit settings for unloaded file");
        if (context.getFile() == null && context.getFileContent() == null) throw new IllegalArgumentException("No file provided");

        val bundle = App.getBundle();
        setTitle(bundle.getString("edit_settings.title"));
        setHeaderText(bundle.getString("edit_settings.header"));

        val content = getSettingsNode(context.getModel());
        getDialogPane().setContent(content);

        val reloadButton = new ButtonType(bundle.getString("label.reload_file"), ButtonBar.ButtonData.OK_DONE);
        val closeButton = new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        val saveButton = new ButtonType(bundle.getString("label.save"), ButtonBar.ButtonData.OTHER);

        getDialogPane().getButtonTypes().setAll(reloadButton, closeButton, saveButton);
    }

    private Node getSettingsNode(DataModel<?, ?> model) {
        try {
            factory = model.getFactoryClass().getConstructor().newInstance();
            val type = factory.getType();
            return type.loadSettingsNode(controller -> {
                settingsController = controller;
                ((SettingsController<Object>) settingsController).setSettings(model.getSettings());
            });
        } catch (Exception e) {
            log.error("Failed to load settings node", e);
            new ExceptionAlert(getDialogPane().getScene().getWindow(), e).showAndWait();
            return null;
        }
    }

}
