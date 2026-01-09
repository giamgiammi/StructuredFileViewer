package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A text area component specifically designed to display the content of a license file in a read-only format.
 * <br>
 * The content of the license is loaded from a resource file named "license.txt" located in the same package.
 * If the file cannot be read, a runtime exception will be thrown.
 * The text is displayed in a monospaced font for consistent formatting.
 * <br>
 * This component is not editable by the user.
 */
public class LicenseArea extends TextArea {
    public LicenseArea() {
        setText(loadText());
        setEditable(false);
        setFont(new Font("Monospaced", 12));
    }

    private String loadText() {
        try {
            val file = App.class.getResource("license.txt");
            return Files.readString(Path.of(file.getPath()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error reading license.txt", e);
        }
    }
}
