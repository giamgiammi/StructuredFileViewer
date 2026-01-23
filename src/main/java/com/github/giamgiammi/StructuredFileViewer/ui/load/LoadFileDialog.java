package com.github.giamgiammi.StructuredFileViewer.ui.load;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import com.github.giamgiammi.StructuredFileViewer.core.csv.CsvDataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.LoadResult;
import com.github.giamgiammi.StructuredFileViewer.model.ModelChoice;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.SettingsController;
import com.github.giamgiammi.StructuredFileViewer.utils.SettingsUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * LoadFileDialog is a custom dialog window for loading or importing data files
 * and managing related settings. This dialog allows the interactive selection of
 * a data model type, loading settings from files, saving settings to files, and
 * specifying the source of the data (local file or pasted text).
 *
 * The class interacts with a variety of helper components, including settings
 * controllers, file choosers, and data model factories, to facilitate these operations.
 * It also provides options to handle settings for different data model types.
 *
 * Thread Safety:
 * This class is designed to be used within a JavaFX application thread
 * and is not thread-safe.
 */
@Slf4j
public class LoadFileDialog extends Dialog<LoadResult<?>> {
    private final ResourceBundle bundle = App.getBundle();
    private final LoadCommon common = new LoadCommon();

    /** Container for the dynamic settings UI specific to the selected data model */
    private Node settingsNode;

    private DataModelFactory<?, ?> factory;
    private SettingsController<?> settingsController;
    private Path file;
    private String fileContent;

    /**
     * Constructs a new LoadFileDialog.
     * Initializes the layout, combo boxes for model selection, and action buttons.
     * 
     * @param owner The parent window for this dialog
     */
    public LoadFileDialog(Window owner) {
        initOwner(owner);

        setResizable(true);
        setWidth(700);
        setHeight(700);

        setTitle(bundle.getString("load_file.title"));
        setHeaderText(bundle.getString("load_file.header"));

        val grid = new GridPane(5, 5);

        val modelCombo = new ComboBox<>(getModelChoices());
        grid.add(modelCombo, 0, 0);

        // Button to import model settings (delimiters, charsets, etc.) from a JSON file
        val loadSettingsFromFile = new Button(bundle.getString("label.load_from_file"));
        loadSettingsFromFile.setOnAction(evt -> {
            common.loadSettingsTofile(getDialogPane().getScene().getWindow(), file -> {
                try {
                    val settings = SettingsUtils.loadSettings(file.toPath());
                    // Update UI to match the loaded settings type
                    modelCombo.valueProperty().set(modelCombo.getItems().stream().filter(m -> m.type() == settings.type()).findFirst().orElseThrow());
                    ((SettingsController<Object>) settingsController).setSettings(settings.settings());
                } catch (Exception e) {
                    log.error("Failed to load settings from file", e);
                    new ExceptionAlert(getDialogPane().getScene().getWindow(), e).showAndWait();
                }
            });
        });
        grid.add(loadSettingsFromFile, 1, 0);

        // Button to export current configuration settings to a JSON file
        val saveSettingsToFile = new Button(bundle.getString("label.save"));
        saveSettingsToFile.setOnAction(evt -> {
            common.saveSettingsTofile(getDialogPane().getScene().getWindow(), file -> {
                try {
                    SettingsUtils.saveSettings(factory.getType(), settingsController.getSettings(), file.toPath());
                } catch (Exception e) {
                    log.error("Failed to save settings to file", e);
                    new ExceptionAlert(getDialogPane().getScene().getWindow(), e).showAndWait();
                }
            });
        });
        saveSettingsToFile.setDisable(true);
        grid.add(saveSettingsToFile, 2, 0);

        getDialogPane().setContent(grid);

        // Define the primary interactions: Open a physical file or process clipboard text
        val openFileBtn = new ButtonType(bundle.getString("label.open_file"), ButtonBar.ButtonData.OK_DONE);
        val pasteBtn = new ButtonType(bundle.getString("label.paste_text"), ButtonBar.ButtonData.OTHER);
        val cancelBtn = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(openFileBtn, pasteBtn, cancelBtn);

        getDialogPane().lookupButton(openFileBtn).setDisable(true);
        getDialogPane().lookupButton(pasteBtn).setDisable(true);

        // Listener to swap the settings UI when the data model type changes (e.g., switching to CSV)
        modelCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            getDialogPane().lookupButton(openFileBtn).setDisable(false);
            getDialogPane().lookupButton(pasteBtn).setDisable(!newVal.type().canLoadStrings());
            saveSettingsToFile.setDisable(false);

            factory = null;
            settingsController = null;
            file = null;
            fileContent = null;

            setSettingsNode(grid, getSettingsNodeByType(newVal.type()));
            ((SettingsController<Object>) settingsController).setSettings(factory.getDefaultSettings());
        });

        // Action for selecting a file from the local filesystem
        getDialogPane().lookupButton(openFileBtn).addEventFilter(ActionEvent.ACTION, evt -> {
            val fc = new FileChooser();
            fc.setInitialDirectory(common.getInitialDirectory());
            val file = fc.showOpenDialog(getDialogPane().getScene().getWindow());
            if (file != null) {
                common.setInitialDirectory(file.getParentFile());
                this.file = file.toPath();
            } else {
                evt.consume();
            }
        });

        // Action for pasting raw text into a sub-dialog
        getDialogPane().lookupButton(pasteBtn).addEventFilter(ActionEvent.ACTION, evt -> {
            val text = new PasteAreaDialog(getDialogPane().getScene().getWindow()).showAndWait().orElse(null);
            if (text != null) {
                fileContent = text;
            } else {
                evt.consume();
            }
        });


        // Transform the dialog button clicks into a LoadResult object
        setResultConverter(btn -> {
            if (btn == openFileBtn) {
                return new LoadResult<>(
                        modelCombo.getValue().type(),
                        DataModelFactory.create(factory, settingsController.getSettings()),
                        file,
                        null
                );
            } else if (btn == pasteBtn) {
                return new LoadResult<>(
                        modelCombo.getValue().type(),
                        DataModelFactory.create(factory, settingsController.getSettings()),
                        null,
                        fileContent
                );
            }
            return null;
        });
    }

    /**
     * Swaps the current settings UI component in the grid layout.
     */
    private void setSettingsNode(GridPane grid, Node settingsNode) {
        if (this.settingsNode != null) grid.getChildren().remove(this.settingsNode);
        this.settingsNode = settingsNode;
        grid.add(settingsNode, 0, 4, 4, 4);
    }

    /**
     * Loads the appropriate FXML and controller based on the selected DataModelType.
     * 
     * @param type The type of data model selected
     * @return The UI Node for the settings
     */
    private Node getSettingsNodeByType(DataModelType type) {
        return type.loadSettingsNode(controller -> {
            this.settingsController = controller;
            this.factory = new CsvDataModelFactory();
        });
    }

    /**
     * Returns the list of supported data models for the selection ComboBox.
     */
    private ObservableList<ModelChoice> getModelChoices() {
        return FXCollections.observableArrayList(
                new ModelChoice(
                        DataModelType.CSV_LIKE,
                        bundle.getString("model.csv")
                )
        );
    }
}
