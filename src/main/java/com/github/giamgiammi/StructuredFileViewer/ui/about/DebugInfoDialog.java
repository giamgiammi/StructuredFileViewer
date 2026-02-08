package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.utils.PropertyUtils;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class DebugInfoDialog extends Alert {
    public DebugInfoDialog(Window owner) {
        super(AlertType.INFORMATION);
        initOwner(owner);

        val bundle = App.getBundle();

        setTitle(bundle.getString("about.debug.title"));
        setHeaderText(bundle.getString("about.debug.header"));

        val area = new TextArea();
        area.setEditable(false);
        area.setPrefRowCount(20);

        val txt = new StringBuilder();
        val sep = "-".repeat(80) + "\n";
        txt.append("DEBUG INFO\n");
        txt.append(sep);
        txt.append("\nAPP PROPERTIES\n");
        txt.append(sep);
        PropertyUtils.getAppPropertiesPairs().forEach(p ->
                txt.append(p.getKey()).append(": ").append(p.getValue()).append("\n"));

        txt.append(sep);
        txt.append("\nPATHS\n");
        txt.append(sep);
        txt.append("path to app socket: ");
        txt.append(App.getTmpAppPath().toAbsolutePath());
        txt.append("\n");
        txt.append("path to logs: ");
        txt.append(App.getLogsPath().toAbsolutePath());
        txt.append("\n");

        txt.append(sep);
        txt.append("\nSYSTEM PROPERTIES\n");
        txt.append(sep);
        PropertyUtils.toPairs(System.getProperties()).forEach(p ->
                txt.append(p.getKey()).append(": ").append(p.getValue()).append("\n"));

        area.setText(txt.toString());
        getDialogPane().setContent(area);

        val closeButton = new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        val copyButton = new ButtonType(bundle.getString("label.copy"), ButtonBar.ButtonData.OTHER);
        val saveButton = new ButtonType(bundle.getString("label.save"), ButtonBar.ButtonData.OTHER);
        getDialogPane().getButtonTypes().setAll(closeButton, copyButton, saveButton);

        ((Button) getDialogPane().lookupButton(copyButton)).setOnAction(evt -> {
            val clipboard = Clipboard.getSystemClipboard();
            val content = new ClipboardContent();
            content.putString(area.getText());
            clipboard.setContent(content);
        });

        ((Button) getDialogPane().lookupButton(saveButton)).setOnAction(evt -> {
            val fc = new FileChooser();
            fc.setTitle(bundle.getString("about.debug.save_title"));
            fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text File", "*.txt"));
            val file = fc.showSaveDialog(owner);
            if (file != null) saveText(owner, area.getText(), file);
        });
    }

    private void saveText(Window owner, String text, File file) {
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getPath() + ".txt");
        }
        val path = file.toPath();
        try (val writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(text);
        } catch (Exception e) {
            log.error("Failed to save debug info to file", e);
            new ExceptionAlert(owner, e).show();
        }
    }
}
