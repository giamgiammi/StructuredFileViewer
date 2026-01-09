package com.github.giamgiammi.StructuredFileViewer.ui.load;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import com.github.giamgiammi.StructuredFileViewer.model.LoadResult;
import com.github.giamgiammi.StructuredFileViewer.model.ModelChoice;
import com.github.giamgiammi.StructuredFileViewer.ui.csv.CsvSettingsController;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.val;

import java.io.File;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

public class LoadFileDialog extends Dialog<LoadResult<?>> {
    private final ResourceBundle bundle = App.getBundle();

    private Supplier<DataModel<?, ?>> modelSupplier;

    public LoadFileDialog(Window owner) {
        initOwner(owner);

        setResizable(true);
        setWidth(700);
        setHeight(700);

        setTitle(bundle.getString("load_file.title"));
        setHeaderText(bundle.getString("load_file.header"));

        val flow = new FlowPane(Orientation.HORIZONTAL);

        val modelCombo = new ComboBox<ModelChoice>(getModelChoices());
        flow.getChildren().add(modelCombo);

        val settingsPane = new FlowPane(Orientation.HORIZONTAL);
        flow.getChildren().add(settingsPane);

        getDialogPane().setContent(flow);

        val openFileBtn = new ButtonType(bundle.getString("label.open_file"), ButtonBar.ButtonData.OK_DONE);
        val pasteBtn = new ButtonType(bundle.getString("label.paste_text"), ButtonBar.ButtonData.OTHER);
        val cancelBtn = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(openFileBtn, pasteBtn, cancelBtn);

        getDialogPane().lookupButton(openFileBtn).setDisable(true);
        getDialogPane().lookupButton(pasteBtn).setDisable(true);

        modelCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            getDialogPane().lookupButton(openFileBtn).setDisable(false);
            getDialogPane().lookupButton(pasteBtn).setDisable(!newVal.type().isCanLoadStrings());

            switch (newVal.type()) {
                case CSV_LIKE -> {
                    val pane = FXUtils.loadFXML(CsvSettingsController.class, "csv_settings", controller -> {
                        this.modelSupplier = controller::getModel;
                    });
                    settingsPane.getChildren().add(pane);
                }
            }
        });


        setResultConverter(btn -> {
            if (btn == openFileBtn) {
                val fc = new FileChooser();
                fc.setInitialDirectory(getInitialDirectory());
                val file = fc.showOpenDialog(getDialogPane().getScene().getWindow());
                if (file != null) {
                    setInitialDirectory(file.getParentFile());
                    return new LoadResult<Object>(
                            modelCombo.getValue().type(),
                            (DataModel<?, Object>) modelSupplier.get(),
                            file.toPath(),
                            null
                    );
                }
            } else if (btn == pasteBtn) {
                val text = new PasteAreaDialog(getDialogPane().getScene().getWindow()).showAndWait().orElse(null);
                if (text != null) {
                    return new LoadResult<Object>(
                            modelCombo.getValue().type(),
                            (DataModel<?, Object>) modelSupplier.get(),
                            null,
                            text
                    );
                }
            }
            return null;
        });
    }

    private ObservableList<ModelChoice> getModelChoices() {
        return FXCollections.observableArrayList(
                new ModelChoice(
                        DataModelType.CSV_LIKE,
                        bundle.getString("model.csv")
                )
        );
    }

    private File getInitialDirectory() {
        val path = Preferences.userNodeForPackage(getClass()).get("last_dir", null);
        if (path != null) return new File(path);
        return null;
    }

    private void setInitialDirectory(File dir) {
        Preferences.userNodeForPackage(getClass()).put("last_dir", dir.getAbsolutePath());
    }
}
