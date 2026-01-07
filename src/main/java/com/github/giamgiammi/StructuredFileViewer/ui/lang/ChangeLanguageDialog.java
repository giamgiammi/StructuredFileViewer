package com.github.giamgiammi.StructuredFileViewer.ui.lang;

import com.github.giamgiammi.StructuredFileViewer.App;
import com.github.giamgiammi.StructuredFileViewer.model.ChoiceWrapper;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import lombok.val;

import java.util.Arrays;
import java.util.Locale;

/**
 * Dialog for selecting and changing the application's language.
 * This dialog allows users to choose a language from a predefined list of supported locales.
 * The selected choice is then used to update the application's locale.
 */
public class ChangeLanguageDialog extends ChoiceDialog<ChoiceWrapper<Locale>> {
    private static final Locale[] LOCALES = new Locale[]{Locale.ENGLISH, Locale.ITALIAN};

    public ChangeLanguageDialog(Window owner) {
        super(currentLocale(), Arrays.stream(LOCALES).map(ChangeLanguageDialog::getLocale).toList());
        initOwner(owner);

        val bundle = App.getBundle();
        setTitle(bundle.getString("change_language.title"));
        setHeaderText(bundle.getString("change_language.header"));

        val okButton = new ButtonType(bundle.getString("label.ok"), ButtonBar.ButtonData.OK_DONE);
        val cancelButton = new ButtonType(bundle.getString("label.cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        getDialogPane().getButtonTypes().setAll(okButton, cancelButton);

        val oldContent = getDialogPane().getContent();
        val grid = new GridPane();
        grid.setVgap(10);
        grid.add(new Label(bundle.getString("change_language.content")), 0, 0);
        grid.add(oldContent, 0, 1);
        getDialogPane().setContent(grid);

        setResultConverter(btn -> {
            if (btn == okButton) return getSelectedItem();
            return null;
        });
    }

    /**
     * Retrieve the current locale
     */
    private static ChoiceWrapper<Locale> currentLocale() {
        val def = Locale.getDefault();

        // search exact match
        for (val locale : LOCALES) {
            if (locale.equals(def)) return getLocale(locale);
        }

        // search language-only match
        for (val locale : LOCALES) {
            if (locale.getLanguage().equals(def.getLanguage())) return getLocale(locale);
        }

        // default to English
        return getLocale(Locale.ENGLISH);
    }

    /**
     * Wrap a locale in a ChoiceWrapper
     * @param locale the locale to wrap
     * @return the wrapped locale
     */
    private static ChoiceWrapper<Locale> getLocale(Locale locale) {
        return new ChoiceWrapper<>(locale, locale.getDisplayLanguage(locale));
    }
}
