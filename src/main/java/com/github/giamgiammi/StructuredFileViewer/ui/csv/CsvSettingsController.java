package com.github.giamgiammi.StructuredFileViewer.ui.csv;

import com.github.giamgiammi.StructuredFileViewer.model.csv.BaseFormatChoice;
import com.github.giamgiammi.StructuredFileViewer.model.csv.CsvSettings;
import com.github.giamgiammi.StructuredFileViewer.model.csv.DuplicateHeaderModeChoice;
import com.github.giamgiammi.StructuredFileViewer.model.csv.QuoteModeChoice;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.SettingsController;
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

public class CsvSettingsController implements Initializable, SettingsController<CsvSettings> {
    public static final String FX_FONT_WEIGHT_BOLD = "-fx-font-weight: bold;";

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
            var text = TextUtils.unquoteSpace(newValue);
            if (text.length() > 1) {
                text = text.substring(0, 1);
                quote.setText(TextUtils.quoteSpace(text));
            }
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
        charset.getItems().setAll(TextUtils.commonCharsets());
        charset.getSelectionModel().select(0);
    }

    @Override
    public CsvSettings getSettings() {
        return new CsvSettings(
                baseFormatChoice.getValue() == null ? null : baseFormatChoice.getValue().format(),
                delimiter.getText().isEmpty() ? null : TextUtils.unquoteSpace(delimiter.getText()),
                quote.getText().isEmpty() ? null : TextUtils.unquoteSpace(quote.getText()).charAt(0),
                recordSeparator.getText().isEmpty() ? null : TextUtils.unquoteSpace(recordSeparator.getText()),
                ignoreEmptyLines.isIndeterminate() ? null : ignoreEmptyLines.isSelected(),
                duplicateHeaderMode.getValue() == null ? null : duplicateHeaderMode.getValue().mode(),
                allowMissingColumnNames.isIndeterminate() ? null : allowMissingColumnNames.isSelected(),
                trailingData.isIndeterminate() ? null : trailingData.isSelected(),
                lenientEof.isIndeterminate() ? null : lenientEof.isSelected(),
                quoteMode.getValue() == null ? null : quoteMode.getValue().mode(),
                Charset.forName(charset.getValue())
        );
    }

    @Override
    public void setSettings(CsvSettings settings) {
        if (settings == null) {
            baseFormatChoice.setValue(null);
            delimiter.setText(null);
            quote.setText(null);
            recordSeparator.setText(null);
            ignoreEmptyLines.setIndeterminate(true);
            duplicateHeaderMode.setValue(null);
            allowMissingColumnNames.setIndeterminate(true);
            trailingData.setIndeterminate(true);
            lenientEof.setIndeterminate(true);
            quoteMode.setValue(null);
            charset.setValue("UTF-8");
        } else {
            baseFormatChoice.setValue(baseFormatChoice.getItems().stream().filter(choice -> choice.format().equals(settings.baseFormat())).findFirst().orElse(null));
            delimiter.setText(settings.delimiter());
            quote.setText(settings.quote() != null ? String.valueOf(settings.quote()) : null);
            recordSeparator.setText(settings.recordSeparator());
            ignoreEmptyLines.setSelected(settings.ignoreEmptyLines());
            duplicateHeaderMode.setValue(settings.duplicateHeaderMode() != null ? new DuplicateHeaderModeChoice(settings.duplicateHeaderMode()) : null);
            allowMissingColumnNames.setSelected(settings.allowMissingColumnNames());
            trailingData.setSelected(settings.trailingData());
            lenientEof.setSelected(settings.lenientEof());
            quoteMode.setValue(settings.quoteMode() != null ? new QuoteModeChoice(settings.quoteMode()) : null);
            charset.setValue(settings.charset().name());
        }
    }
}
