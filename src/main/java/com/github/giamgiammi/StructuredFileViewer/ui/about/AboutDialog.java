package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ResourceBundle;

/**
 * A custom dialog for displaying application-related information such as title, header, license, and links.
 * <br>
 * This dialog extends the JavaFX {@link Alert} class with {@code AlertType.INFORMATION}.
 * It is designed to display an about dialog box in the application.
 * <br>
 * The content of the dialog includes localized strings for the title and header, and dynamically loaded
 * content such as a link and a license area. The localization is handled using a {@link ResourceBundle}.
 * <br>
 * The dialog also includes a clickable hyperlink that opens a URL from an external resource file
 * (`url.txt`) in the default web browser.
 * <br>
 * The expandable content of the dialog is used to display the content of the license using the
 * {@link LicenseArea} component.
 * <br>
 * This class is typically invoked by calling {@code handleAbout()} from a controller or similar handler.
 */
@Slf4j
public class AboutDialog extends Alert {
    private static final String SOURCE_URL = "https://github.com/giamgiammi/StructuredFileViewer";

    public AboutDialog(Window owner) {
        super(Alert.AlertType.INFORMATION);
        initOwner(owner);

        val bundle = App.getBundle();

        setTitle(bundle.getString("about.title"));
        setHeaderText(bundle.getString("about.header"));

        val grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label(bundle.getString("about.content")), 0, 0);

        val link = new Hyperlink(SOURCE_URL);
        link.setOnAction(evt -> {
            App.openLink(link.getText());
        });
        grid.add(link, 0, 1);
        grid.add(new Label(bundle.getString("about.notice")), 0, 2);

        getDialogPane().setContent(grid);

        getDialogPane().getButtonTypes().setAll(new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE));
    }
}
