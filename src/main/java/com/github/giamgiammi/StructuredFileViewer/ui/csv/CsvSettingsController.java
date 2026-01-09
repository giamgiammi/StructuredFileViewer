package com.github.giamgiammi.StructuredFileViewer.ui.csv;

import com.github.giamgiammi.StructuredFileViewer.core.csv.CsvDataModel;
import com.github.giamgiammi.StructuredFileViewer.core.csv.CsvDataModelFactory;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class CsvSettingsController implements Initializable {
    private final CsvDataModelFactory factory = new CsvDataModelFactory();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //
    }

    public CsvDataModel getModel() {
        return null;//todo
    }
}
