package com.github.giamgiammi.StructuredFileViewer.model;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.ui.inteface.DataController;
import lombok.Data;

@Data
public class TabData {
    private DataModel<?, ?> model;
    private DataController controller;
}
