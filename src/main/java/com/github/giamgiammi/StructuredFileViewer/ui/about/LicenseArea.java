package com.github.giamgiammi.StructuredFileViewer.ui.about;

import com.github.giamgiammi.StructuredFileViewer.App;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.nio.charset.StandardCharsets;

@Slf4j
public class LicenseArea extends TabPane {
    public LicenseArea() {
        setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        setTabDragPolicy(TabDragPolicy.FIXED);
        val bundle = App.getBundle();

        getTabs().addAll(
                new Tab(bundle.getString("label.license"), getTextArea("LICENSE.txt")),
                new Tab(bundle.getString("label.third_party_license"), getTextArea("THIRD-PARTY.txt")),
                new Tab("MIT", getTextArea("the mit license - license.txt")),
                new Tab("APACHE 2.0", getTextArea("the apache software license, version 2.0 - license-2.0.txt"))
        );
    }

    private TextArea getTextArea(String fileName) {
        val area = new TextArea(getText(fileName));
        area.setEditable(false);
        area.setPrefRowCount(20);
        return area;
    }

    private String getText(String fileName) {
        try (val in = ClassLoader.getSystemResourceAsStream(fileName)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error reading %s file".formatted(fileName), e);
            return "Error reading %s file".formatted(fileName);
        }
    }
}
