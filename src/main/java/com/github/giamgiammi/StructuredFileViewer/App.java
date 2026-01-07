package com.github.giamgiammi.StructuredFileViewer;

import com.github.giamgiammi.StructuredFileViewer.ui.main.MainViewController;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.val;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * JavaFX App
 */
public class App extends Application {
    private static ResourceBundle bundle;

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
        startMainStage(stage);
    }

    private static void startMainStage(Stage stage) {
        val scene = new Scene(FXUtils.loadFXML(MainViewController.class, "main", null));
        stage.setScene(scene);
        stage.setTitle(getBundle().getString("title"));
        stage.setOnCloseRequest(event -> {
            event.consume();
            FXUtils.closeApp(stage);
        });
        stage.getIcons().add(new Image(App.class.getResourceAsStream("logo.png")));
        stage.show();
    }

    public static void main(String[] args) {
        val pref = Preferences.userNodeForPackage(App.class);
        if (pref.get("locale", null) != null) {
            val locale = Locale.of(pref.get("locale", null));
            Locale.setDefault(locale);
        }

        launch();
    }

}