package com.github.giamgiammi.StructuredFileViewer.utils;

import com.github.giamgiammi.StructuredFileViewer.model.updater.ReleaseDto;
import com.github.giamgiammi.StructuredFileViewer.model.updater.Version;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Slf4j
public class UpdateNotifier {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    private ReleaseDto getLatestRelease() throws IOException, InterruptedException {
        log.info("Retrieving latest release from GitHub API");
        val req = HttpRequest
                .newBuilder()
                .uri(URI.create("https://api.github.com/repos/giamgiammi/StructuredFileViewer/releases/latest"))
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .timeout(Duration.ofSeconds(30))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        val resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        log.info("Github responded with status code: {}", resp.statusCode());

        if (resp.statusCode() == 200) {
            val dto =  mapper.readValue(resp.body(), ReleaseDto.class);
            log.info("Latest release: {}", dto);
            return dto;
        }
        log.error("Failed to retrieve latest release");
        throw new IOException("Failed to retrieve latest release");
    }

    public Version getAppVersion() {
        val props = PropertyUtils.APP_PROPERTIES;

        val version = props.getProperty("version");
        if (version == null) throw new IllegalStateException("Missing version property in app.properties");
        return new Version(version);
    }

    public Optional<String> checkForUpdates() throws IOException, InterruptedException {
        log.info("Checking for updates");

        val version = getAppVersion();
        log.info("Current version: {}", version);

        val release = getLatestRelease();
        final Version releaseVersion;
        try {
            releaseVersion = new Version(release.tagName());
        } catch (Exception e) {
            throw new IOException("Failed to parse release version", e);
        }

        if (version.compareTo(releaseVersion) < 0) {
            log.info("New version available: {}", releaseVersion);
            return Optional.of(release.htmlUrl());
        }
        log.info("No updates available");
        return Optional.empty();
    }

    static void main() {
        try {
            val url = new UpdateNotifier().checkForUpdates();
            if (url.isPresent()) System.out.println(url.get());
            else System.out.println("No updates available");
        } catch (Exception e) {
            log.error("Failed to check for updates", e);
        }
    }
}
