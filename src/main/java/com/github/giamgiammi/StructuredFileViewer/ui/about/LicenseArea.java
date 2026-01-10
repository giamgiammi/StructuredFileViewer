package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.charset.StandardCharsets;

/**
 * A text area component specifically designed to display the content of a license file in a read-only format.
 * <br>
 * The content of the license is loaded from a resource file named "license.txt" located in the same package.
 * If the file cannot be read, a runtime exception will be thrown.
 * The text is displayed in a monospaced font for consistent formatting.
 * <br>
 * This component is not editable by the user.
 */
@Slf4j
public class LicenseArea extends TextArea {
    private final Window owner;

    public LicenseArea(Window owner) {
        this.owner = owner;
        setText(loadText());
        setEditable(false);
        setFont(new Font("Monospaced", 12));
        setPrefColumnCount(20);
        setPrefRowCount(20);
    }

    private String loadText() {
        try (val in = App.class.getResourceAsStream("license.txt")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error reading license.txt", e);
            new ExceptionAlert(owner, e).showAndWait();
            return "";
        }
    }
}
