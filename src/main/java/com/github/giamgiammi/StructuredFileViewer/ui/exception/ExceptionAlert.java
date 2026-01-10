package com.github.giamgiammi.StructuredFileViewer.ui.exception;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import lombok.val;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionAlert extends Alert {
    public ExceptionAlert(Window owner, Throwable throwable) {
        super(AlertType.ERROR);
        initOwner(owner);

        val bundle = App.getBundle();
        setTitle(bundle.getString("exception.title"));
        setHeaderText(bundle.getString("exception.header"));
        setContentText(bundle.getString("exception.content"));

        val area = new TextArea();
        area.setEditable(false);
        area.setText(getStackTrace(throwable));
        getDialogPane().setExpandableContent(area);

        val closeButton = new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(closeButton);
    }

    private String getStackTrace(Throwable throwable) {
        val sw = new StringWriter();
        val pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
}
