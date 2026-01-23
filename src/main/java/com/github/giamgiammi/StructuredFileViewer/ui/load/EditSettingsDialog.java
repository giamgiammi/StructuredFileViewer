package com.github.giamgiammi.StructuredFileViewer.ui.load;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.LoadResult;
import com.github.giamgiammi.StructuredFileViewer.model.TabData;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.SettingsController;
import com.github.giamgiammi.StructuredFileViewer.utils.SettingsUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class EditSettingsDialog extends Dialog<LoadResult<?>> {
    private final LoadCommon common = new LoadCommon();
    private SettingsController<?> settingsController;
    private DataModelFactory<?, ?> factory;

    public EditSettingsDialog(Window owner, TabData context) {
        initOwner(owner);

        if (context.getModel() == null) throw new IllegalArgumentException("Cannot edit settings for unloaded file");
        if (context.getFile() == null && context.getFileContent() == null) throw new IllegalArgumentException("No file provided");

        val bundle = App.getBundle();
        setTitle(bundle.getString("edit_settings.title"));
        setHeaderText(bundle.getString("edit_settings.header"));

        val settingsNode = getSettingsNode(context.getModel());
        val content = new GridPane();
        content.add(settingsNode, 0, 0);
        getDialogPane().setContent(content);

        val reloadButton = new ButtonType(bundle.getString("label.reload_file"), ButtonBar.ButtonData.OK_DONE);
        val closeButton = new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().setAll(reloadButton, closeButton);

        val saveButton = new Button(bundle.getString("label.save"));
        saveButton.setOnAction(evt -> {
            common.saveSettingsTofile(owner, file -> {
                try {
                    SettingsUtils.saveSettings(factory.getType(), settingsController.getSettings(), file.toPath());
                } catch (Exception e) {
                    log.error("Failed to save settings to file", e);
                    new ExceptionAlert(owner, e).showAndWait();
                }
            });
        });
        content.add(saveButton, 0, 1);

        setResultConverter(btn -> {
            if (btn == reloadButton) {
                return new LoadResult<Object>(
                        factory.getType(),
                        DataModelFactory.create(factory, settingsController.getSettings()),
                        context.getFile(),
                        context.getFileContent()
                );
            }
            return null;
        });
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
