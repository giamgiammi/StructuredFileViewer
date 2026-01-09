package com.github.giamgiammi.StructuredFileViewer.ui.csv;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.csv.CsvDataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.model.csv.*;
import com.github.giamgiammi.StructuredFileViewer.utils.TextUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class CsvSettingsController implements Initializable {
    public static final String FX_FONT_WEIGHT_BOLD = "-fx-font-weight: bold;";
    private final CsvDataModelFactory factory = new CsvDataModelFactory();

    @FXML
    private ComboBox<BaseFormatChoice> baseFormatChoice;

    @FXML
    private TextField delimiter;

    @FXML
    private TextField quote;

    @FXML
    private TextField recordSeparator;

    @FXML
    private ComboBox<DuplicateHeaderModeChoice> duplicateHeaderMode;

    @FXML
    private ComboBox<QuoteModeChoice> quoteMode;

    @FXML
    private ComboBox<String> charset;

    @FXML
    private CheckBox ignoreEmptyLines;

    @FXML
    private CheckBox allowMissingColumnNames;

    @FXML
    private CheckBox trailingData;

    @FXML
    private CheckBox lenientEof;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        baseFormatChoice.getItems().setAll(
                new BaseFormatChoice(CSVFormat.DEFAULT, "DEFAULT"),
                new BaseFormatChoice(CSVFormat.EXCEL, "EXCEL"),
                new BaseFormatChoice(CSVFormat.INFORMIX_UNLOAD, "INFORMIX_UNLOAD"),
                new BaseFormatChoice(CSVFormat.INFORMIX_UNLOAD_CSV, "INFORMIX_UNLOAD_CSV"),
                new BaseFormatChoice(CSVFormat.MONGODB_CSV, "MONGODB_CSV"),
                new BaseFormatChoice(CSVFormat.MONGODB_TSV, "MONGODB_TSV"),
                new BaseFormatChoice(CSVFormat.MYSQL, "MYSQL"),
                new BaseFormatChoice(CSVFormat.ORACLE, "ORACLE"),
                new BaseFormatChoice(CSVFormat.POSTGRESQL_CSV, "POSTGRESQL_CSV"),
                new BaseFormatChoice(CSVFormat.POSTGRESQL_TEXT, "POSTGRESQL_TEXT"),
                new BaseFormatChoice(CSVFormat.RFC4180, "RFC4180"),
                new BaseFormatChoice(CSVFormat.TDF, "TDF")
        );
        baseFormatChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            delimiter.setPromptText(TextUtils.quoteSpace(newValue.format().getDelimiterString()));
            quote.setPromptText(TextUtils.quoteSpace(Optional.ofNullable(newValue.format().getQuoteCharacter()).map(String::valueOf).orElse("")));
            recordSeparator.setPromptText(TextUtils.quoteSpace(newValue.format().getRecordSeparator()));
            duplicateHeaderMode.setPromptText(Optional.ofNullable(newValue.format().getDuplicateHeaderMode()).map(DuplicateHeaderMode::name).orElse(""));
            quoteMode.setPromptText(Optional.ofNullable(newValue.format().getQuoteMode()).map(QuoteMode::name).orElse(""));

            if (newValue.format().getIgnoreEmptyLines())
                ignoreEmptyLines.setStyle(FX_FONT_WEIGHT_BOLD);
            else
                ignoreEmptyLines.setStyle("");
            if (newValue.format().getAllowMissingColumnNames())
                allowMissingColumnNames.setStyle(FX_FONT_WEIGHT_BOLD);
            else
                allowMissingColumnNames.setStyle("");
            if (newValue.format().getTrailingData())
                trailingData.setStyle(FX_FONT_WEIGHT_BOLD);
            else
                trailingData.setStyle("");
            if (newValue.format().getLenientEof())
                lenientEof.setStyle(FX_FONT_WEIGHT_BOLD);
            else
                lenientEof.setStyle("");
        });
        quote.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1) quote.setText(newValue.substring(0, 1));
        });
        duplicateHeaderMode.getItems().setAll(Stream.concat(Stream.of(new DuplicateHeaderModeChoice(null)), Arrays.stream(DuplicateHeaderMode.values()).map(DuplicateHeaderModeChoice::new)).toList());
        duplicateHeaderMode.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.mode() == null) duplicateHeaderMode.valueProperty().set(null);
        });
        quoteMode.getItems().setAll(Stream.concat(Stream.of(new QuoteModeChoice(null)), Arrays.stream(QuoteMode.values()).map(QuoteModeChoice::new)).toList());
        quoteMode.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.mode() == null) quoteMode.valueProperty().set(null);
        });

        charset.setEditable(true);
        charset.getItems().setAll(
                "UTF-8",
                "UTF-16",
                "cp1252"
        );
        charset.getSelectionModel().select(0);
    }

    private CsvSettings getSettings() {
        return new CsvSettings(
                baseFormatChoice.getValue() == null ? null : baseFormatChoice.getValue().format(),
                delimiter.getText().isEmpty() ? null : delimiter.getText(),
                quote.getText().isEmpty() ? null : quote.getText().charAt(0),
                recordSeparator.getText().isEmpty() ? null : recordSeparator.getText(),
                ignoreEmptyLines.isIndeterminate() ? null : ignoreEmptyLines.isSelected(),
                duplicateHeaderMode.getValue() == null ? null : duplicateHeaderMode.getValue().mode(),
                allowMissingColumnNames.isIndeterminate() ? null : allowMissingColumnNames.isSelected(),
                trailingData.isIndeterminate() ? null : trailingData.isSelected(),
                lenientEof.isIndeterminate() ? null : lenientEof.isSelected(),
                quoteMode.getValue() == null ? null : quoteMode.getValue().mode(),
                Charset.forName(charset.getValue())
        );
    }

    public DataModel<CsvSettings, CsvData> getModel() {
        return factory.create(getSettings());
    }
}
