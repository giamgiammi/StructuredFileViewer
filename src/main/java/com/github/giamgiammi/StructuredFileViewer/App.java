package com.github.giamgiammi.StructuredFileViewer;

import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.main.MainViewController;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;

/**
 * JavaFX App
 */
@Slf4j
public class App extends Application {
    public static final String ACCEPTED_LICENSE_KEY = "accepted_license";
    private static final String MAXIMIZED_KEY = "last_maximized";
    private static ResourceBundle bundle;
    private static HostServices hostServices;
    private static Image logo;

    /**
     * Open a link in the default browser
     * @param url the URL to open
     */
    public static void openLink(String url) {
        hostServices.showDocument(url);
    }

    /**
     * Get the resource bundle associated with this app
     */
    public static ResourceBundle getBundle() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(App.class.getPackageName() + ".messages");
        }
        return bundle;
    }

    /**
     * Change the locale for this app. The settings is persisted and requires a restart to be fully effective
     * @param locale the new locale
     */
    public static void changeLocale(Locale locale) {
        Locale.setDefault(locale);
        bundle = null;
        Preferences.userNodeForPackage(App.class).put("locale", locale.toString());
    }

    /**
     * Opens a new window with the main view
     */
    public static void openNewWindow() throws IOException {
        val stage = new Stage();
        startMainStage(stage);
    }

    @Override
    public void start(Stage stage) throws IOException {
        //val acceptedLicense = Preferences.userNodeForPackage(App.class).getBoolean(ACCEPTED_LICENSE_KEY, false);
        //if (!acceptedLicense) {
        //    startAcceptLicenseStage(stage);
        //} else {
            startMainStage(stage);
        //}
        hostServices = getHostServices();
    }

    /*private static void startAcceptLicenseStage(Stage stage) {
        stage.setScene(new Scene(new Label(), 500, 400));
        stage.getIcons().add(getLogo(stage));
        stage.setTitle(getBundle().getString("title"));
        stage.show();
        FXUtils.runLater(() ->  {
            new AboutDialog(stage, true).showAndWait().ifPresent(btn -> {
                if (btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    try {
                        openNewWindow();
                        stage.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }, 500);
    }*/

    private static void startMainStage(Stage stage) {
        log.info("Starting main stage");
        val scene = new Scene(FXUtils.loadFXML(MainViewController.class, "main", null));
        stage.setScene(scene);
        stage.setTitle(getBundle().getString("title"));
        stage.setOnCloseRequest(event -> {
            event.consume();
            FXUtils.closeApp(stage);
        });
        stage.getIcons().add(getLogo(stage));
        stage.show();

        if (Preferences.userNodeForPackage(App.class).getBoolean(MAXIMIZED_KEY, false)) {
            stage.setMaximized(true);
        }

        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> Preferences.userNodeForPackage(App.class).putBoolean(MAXIMIZED_KEY, newVal));
    }

    private static Image getLogo(Stage stage) {
        if (logo == null) {
            try (val in = App.class.getResourceAsStream("logo.png")) {
                logo = new Image(in);
            } catch (Exception e) {
                new ExceptionAlert(stage, e).showAndWait();
            }
        }
        return logo;
    }

    private static void loadLoggingProperties() {
        try (val in = App.class.getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(in);
        } catch (Exception e) {
            log.error("Failed to load logging.properties", e);
        }
    }

    public static void main(String[] args) {
        loadLoggingProperties();
        val pref = Preferences.userNodeForPackage(App.class);
        if (pref.get("locale", null) != null) {
            val locale = Locale.of(pref.get("locale", null));
            Locale.setDefault(locale);
        }

        launch();
    }

}