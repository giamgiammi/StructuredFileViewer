package com.github.giamgiammi.StructuredFileViewer.utils;

import javafx.application.HostServices;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

@Slf4j
public class DesktopUtils {
    @Setter
    private static HostServices hostServices;

    /**
     * Opens the specified URL in the default browser.
     * @param url the URL to open
     */
    public static void openLink(@NonNull String url) {
        try {
            hostServices.showDocument(url);
        } catch (Exception e) {
            log.error("Failed to open link: {}", url, e);
        }
    }

    /**
     * Opens the specified folder in the default file explorer.
     * @param path the path to the folder to open
     */
    public static void openFolder(@NonNull Path path) {
        try {
            if (OSUtils.isMac()) {
                // file:// does not work anymore in tahoe
                new ProcessBuilder("open", path.toAbsolutePath().toString()).start();
            } else {
                hostServices.showDocument("file://" + path.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Failed to open folder: {}", path, e);
        }
    }
}
