package com.github.giamgiammi.StructuredFileViewer.ui.load;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import lombok.val;

public class PasteAreaDialog extends Dialog<String> {
    public PasteAreaDialog(Window owner) {
        initOwner(owner);

        val bundle = App.getBundle();

        setTitle(bundle.getString("load_file.paste.title"));
        setHeaderText(bundle.getString("load_file.paste.header"));

        val area = new TextArea();
        area.setEditable(true);
        area.setWrapText(false);

        getDialogPane().setContent(area);

        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        setResultConverter(btn -> btn == okButton ? area.getText() : null);
    }
}
