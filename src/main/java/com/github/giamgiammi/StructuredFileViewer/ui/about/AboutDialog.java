package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Window;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ResourceBundle;
import java.util.prefs.Preferences;

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
        this(owner, false);
    }

    public AboutDialog(Window owner, boolean acceptLicense) {
        super(Alert.AlertType.INFORMATION);
        initOwner(owner);

        val bundle = App.getBundle();

        setTitle(bundle.getString("about.title"));
        setHeaderText(bundle.getString("about.header"));

        val grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Text(bundle.getString("about.content")), 0, 0);

        val link = new Hyperlink(SOURCE_URL);
        link.setOnAction(evt -> {
            App.openLink(link.getText());
        });
        grid.add(link, 0, 1);
        grid.add(new Text(bundle.getString("about.notice")), 0, 2);

        getDialogPane().setContent(grid);
        getDialogPane().setExpandableContent(new LicenseArea());

        if (acceptLicense) {
            getDialogPane().setExpanded(true);
            getDialogPane().getButtonTypes().setAll(new ButtonType(bundle.getString("label.accept"), ButtonBar.ButtonData.OK_DONE), new ButtonType(bundle.getString("label.close"), ButtonBar.ButtonData.CANCEL_CLOSE));
        } else {
            getDialogPane().getButtonTypes().setAll(new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE));
        }

        setResultConverter(btn -> {
            if (acceptLicense) {
                if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    Preferences.userNodeForPackage(App.class).putBoolean(App.ACCEPTED_LICENSE_KEY, true);
                } else {
                    System.exit(0);
                }
            }
            return btn;
        });
    }
}
