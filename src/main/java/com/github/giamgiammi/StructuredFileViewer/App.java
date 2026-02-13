package com.github.giamgiammi.StructuredFileViewer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import com.github.giamgiammi.StructuredFileViewer.model.InstanceMessage;
import com.github.giamgiammi.StructuredFileViewer.service.SingleInstanceService;
import com.github.giamgiammi.StructuredFileViewer.ui.exception.ExceptionAlert;
import com.github.giamgiammi.StructuredFileViewer.ui.main.MainViewController;
import com.github.giamgiammi.StructuredFileViewer.utils.AppProperty;
import com.github.giamgiammi.StructuredFileViewer.utils.DesktopUtils;
import com.github.giamgiammi.StructuredFileViewer.utils.FXUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.prefs.Preferences;

/**
 * JavaFX App
 */
@Slf4j
public class App extends Application {
    public static final String ACCEPTED_LICENSE_KEY = "accepted_license";
    private static final String MAXIMIZED_KEY = "last_maximized";
    private static final List<MainViewController> controllers = new ArrayList<>();
    private static ResourceBundle bundle;
    private static Image logo;
    private static Path[] filesToOpen;
    @SuppressWarnings("FieldCanBeLocal")//must stay open
    private static SingleInstanceService singleInstanceService;


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
        DesktopUtils.setHostServices(getHostServices());
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
        val scene = new Scene(FXUtils.loadFXML(MainViewController.class, "main", ctrl -> {
            controllers.forEach(c -> c.setIsMainView(false));
            controllers.add(ctrl);
            stage.setOnHidden(evt -> {
                if (ctrl.isMainView() && controllers.size() > 1) controllers.getLast().setIsMainView(true);
                controllers.remove(ctrl);
            });
            ctrl.setControllers(controllers);

            if (filesToOpen == null) {
                FXUtils.runLater(ctrl::handleNewTab, 500);
            } else {
                val localFiles = filesToOpen;
                filesToOpen = null;
                FXUtils.runLater(() -> ctrl.openFiles(localFiles), 500);
            }
        }));
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

    private static void prepareLogging() {
        //create folders
        try {
            val path = Path.of(System.getProperty(AppProperty.LOG_DIR));
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("Failed to create logs folder", e);
        }
    }

    private static void resetLoggers() {
        try {
            val context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.reset();
            val ci = new ContextInitializer(context);
            ci.autoConfig();
        } catch (Exception e) {
            log.error("Failed to reset loggers", e);
        }
    }

    private static void initSingleInstanceService() {
        try {
            singleInstanceService = new SingleInstanceService();
            try {
                if (singleInstanceService.isClient()) {
                    log.info("Another instance of the app is already running, sending message");
                    val message = new InstanceMessage(filesToOpen);
                    singleInstanceService.sendMessage(message);
                    System.exit(0);
                } else {
                    log.info("This is the first instance of the app, starting as server and reloading logging configuration");
                    System.setProperty(AppProperty.IS_MAIN, "true");
                    resetLoggers();
                    singleInstanceService.setMessageHandler(message -> {
                        val mainCtrl = controllers.stream()
                                .filter(MainViewController::isMainView)
                                .findFirst()
                                .orElse(controllers.get(0));
                        if (message.filesToOpen() != null) Platform.runLater(() -> mainCtrl.openFiles(message.filesToOpen()));
                        else Platform.runLater(mainCtrl::handleNewTab);
                    });
                }
            } catch (Exception e) {
                log.error("Failed to send message to other instance", e);
            }
        } catch (Exception e) {
            log.error("Failed to start single instance service", e);
        }
    }

    private static void findFilesToOpen(String[] args) {
        if (args.length > 0) {
            log.info("Found command line arguments: {}", Arrays.toString(args));
            filesToOpen = Arrays.stream(args)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .map(Path::of)
                    .map(Path::toAbsolutePath)
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .toArray(Path[]::new);
            log.info("Found files to open: {}", Arrays.toString(filesToOpen));
        }
    }

    private static void setLocale() {
        val pref = Preferences.userNodeForPackage(App.class);
        if (pref.get("locale", null) != null) {
            val locale = Locale.of(pref.get("locale", null));
            Locale.setDefault(locale);
        }
    }

    private static void loadAppProperties() {
        try (val in = ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties")) {
            System.getProperties().load(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load app.properties", e);
        }
    }

    private static void setAppProperties() {
        val tmp = Objects.requireNonNull(System.getProperty("java.io.tmpdir"), "java.io.tmpdir system property not set");
        final Path tmpDir;
        if (System.getProperty(AppProperty.TMP_DIR) == null) {
            tmpDir = Path.of(tmp).resolve(App.class.getPackageName());
            System.setProperty(AppProperty.TMP_DIR, tmpDir.toString());
            log.info("Setting tmp dir to {}", tmpDir);
        } else {
            tmpDir = Path.of(System.getProperty(AppProperty.TMP_DIR));
            log.info("Using tmp dir {}", tmpDir);
        }

        if (System.getProperty(AppProperty.LOG_DIR) == null) {
            val logDir = tmpDir.resolve("logs");
            System.setProperty(AppProperty.LOG_DIR, logDir.toString());
            log.info("Setting log dir to {}", logDir);
        } else {
            val logDir = Path.of(System.getProperty(AppProperty.LOG_DIR));
            log.info("Using log dir {}", logDir);
        }
    }

    public static void main(String[] args) {
        loadAppProperties();
        setAppProperties();
        prepareLogging();
        setLocale();
        findFilesToOpen(args);
        initSingleInstanceService();

        log.info("Starting app");
        launch();
    }

}