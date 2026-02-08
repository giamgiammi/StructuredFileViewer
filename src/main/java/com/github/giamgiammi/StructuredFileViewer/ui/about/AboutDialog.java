package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.utils.PropertyUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    private static final int WRAPPING_WIDTH = 500;

    public AboutDialog(Window owner) {
        this(owner, false);
    }

    public AboutDialog(Window owner, boolean acceptLicense) {
        super(Alert.AlertType.INFORMATION);
        initOwner(owner);

        val bundle = App.getBundle();

        setTitle(bundle.getString("about.title"));
        setHeaderText(bundle.getString("title"));

        val grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        val contentStart = new Text(bundle.getString("about.content_start"));
        contentStart.setWrappingWidth(WRAPPING_WIDTH);
        grid.add(contentStart, 0, 0);

        val flow = new TextFlow();
        val contentText = new Text(bundle.getString("about.content_link"));
        contentText.setWrappingWidth(WRAPPING_WIDTH);
        flow.getChildren().add(contentText);

        val link = new Hyperlink(getSourceUrl(owner));
        link.setOnAction(evt -> {
            App.openLink(link.getText());
        });
        flow.getChildren().add(link);

        grid.add(flow, 0, 1);
        val notice = new Text(bundle.getString("about.notice"));
        notice.setWrappingWidth(WRAPPING_WIDTH);
        grid.add(notice, 0, 2);

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

    private String getSourceUrl(Window owner) {
        val url = PropertyUtils.APP_PROPERTIES.getProperty("url");
        if (url == null) {
            log.error("Missing url property in app.properties");
            new ExceptionAlert(owner, new IllegalStateException("Missing url property in app.properties")).showAndWait();
            return "";
        }
        return url;
    }
}
