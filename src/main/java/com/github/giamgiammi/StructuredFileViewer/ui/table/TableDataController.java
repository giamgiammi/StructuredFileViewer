package com.github.giamgiammi.StructuredFileViewer.ui.table;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.TableLikeData;
import lombok.NonNull;

public class TableDataController {
    private DataModel<?, TableLikeData> model;

    public void setData(@NonNull TableLikeData data) {
        //todo
    }

    public void setModel(DataModel<?, TableLikeData> model) {
        this.model = model;
    }
}
