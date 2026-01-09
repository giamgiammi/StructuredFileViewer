package com.github.giamgiammi.StructuredFileViewer.ui.tab;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;
import lombok.val;

/**
 * A custom alert that prompts the user to confirm closing a tab in the application.
 *
 * The dialog displays a confirmation message to ensure the user intends to close the tab. It utilizes
 * internationalized strings through a resource bundle provided by the application. The alert includes the following:
 * - A title and header message indicating the action being confirmed.
 * - A formatted message that includes the name of the tab being closed.
 * - A warning message to notify the user of any implications of the action.
 * - Two buttons: one for confirmation (OK) and the other for cancellation (Cancel).
 *
 * This class extends the {@link Alert} class and is initialized with a specific {@link Window} as the owner and
 * the title of the target tab.
 */
public class CloseTabAlert extends Alert {
    public CloseTabAlert(Window owner, String tabTitle) {
        super(Alert.AlertType.CONFIRMATION);
        initOwner(owner);

        val bundle = App.getBundle();

        setTitle(bundle.getString("close_tab.title"));
        setHeaderText(bundle.getString("close_tab.header"));

        val text1 = new Text(bundle.getString("close_tab.content_1").strip());
        val text2 = new Text(String.format(" %s ", tabTitle));
        text2.setStyle("-fx-font-weight: bold;");
        val text3 = new Text(bundle.getString("close_tab.content_2").strip());

        val grid = new GridPane();
        grid.setVgap(5);
        grid.add(new TextFlow(text1, text2, text3), 0, 0);
        grid.add(new Text(bundle.getString("close_tab.content_warning")), 0, 1);

        getDialogPane().setContent(grid);

        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().setAll(okButton, cancelButton);
    }
}
