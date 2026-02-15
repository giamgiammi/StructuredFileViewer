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
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
public class UpdateNotifier {
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newHttpClient();

    private String getApiUrl() {
        val url = Objects.requireNonNull(System.getProperty(AppProperty.URL), "app url system property not set");
        val matcher = Pattern.compile("https?://github\\.com/(?<username>[^/]+)/(?<reponame>[^/#?]+)")
                .matcher(url);
        if (!matcher.matches()) throw new IllegalStateException("Invalid app url: " + url);
        val username = matcher.group("username");
        if (username == null || username.isEmpty()) throw new IllegalStateException("Cannot extract username from app url");
        val repoName = matcher.group("reponame");
        if (repoName == null || repoName.isEmpty()) throw new IllegalStateException("Cannot extract repo name from app url");
        return String.format("https://api.github.com/repos/%s/%s/releases/latest", username, repoName);
    }

    private ReleaseDto getLatestRelease() throws IOException, InterruptedException {
        log.info("Retrieving latest release from GitHub API");
        val url = getApiUrl();
        log.info("Requesting {}", url);
        val req = HttpRequest
                .newBuilder()
                .uri(URI.create(url))
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
        val version = Objects.requireNonNull(System.getProperty(AppProperty.VERSION), "app version system property not set");
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
        log.info("Latest release version: {}", releaseVersion);

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
