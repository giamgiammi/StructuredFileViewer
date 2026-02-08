package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.utils.PropertyUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import lombok.val;

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
        PropertyUtils.getAppPropertiesPairs().forEach(p ->
                txt.append(p.getKey()).append(": ").append(p.getValue()).append("\n"));
        txt.append("tmp app path: ");
        txt.append(App.getTmpAppPath());
        txt.append("\n");

        txt.append(sep);
        txt.append("\nSYSTEM PROPERTIES\n");
        txt.append(sep);
        PropertyUtils.toPairs(System.getProperties()).forEach(p ->
                txt.append(p.getKey()).append(": ").append(p.getValue()).append("\n"));

        area.setText(txt.toString());
        getDialogPane().setContent(area);

        val closeButton = new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(closeButton);
    }
}
