package com.github.giamgiammi.StructuredFileViewer.core.fixed;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelFactory;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import com.github.giamgiammi.StructuredFileViewer.model.SimpleTableData;
import com.github.giamgiammi.StructuredFileViewer.model.fixed.FixedWidthSettings;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * A factory for creating data models of type {@link DataModelType#FIXED_WIDTH}.
 * This implementation of {@link DataModelFactory} is responsible for generating
 * instances of {@link FixedWidthDataModel}, using {@link FixedWidthSettings} as
 * its configuration and {@link SimpleTableData} as its data structure.
 *
 * This factory provides the following functionalities:
 * - Identifying its associated data model type as {@code FIXED_WIDTH}.
 * - Providing default settings for fixed-width data models.
 * - Creating new instances of {@link FixedWidthDataModel} using the provided settings.
 *
 * Responsibility:
 * - Encapsulates the logic for the setup of fixed-width data models.
 * - Provides default values and mechanisms to configure and instantiate fixed-width data parsing models.
 */
public class FixedWidthDataModelFactory implements DataModelFactory<FixedWidthSettings, SimpleTableData> {
    @Override
    public @NonNull DataModelType getType() {
        return DataModelType.FIXED_WIDTH;
    }

    @Override
    public @NonNull FixedWidthSettings getDefaultSettings() {
        return new FixedWidthSettings(
                List.of(),
                true,
                StandardCharsets.UTF_8
        );
    }

    @Override
    public @NonNull DataModel<FixedWidthSettings, SimpleTableData> create(@NonNull FixedWidthSettings fixedWidthSettings) {
        return new FixedWidthDataModel(fixedWidthSettings);
    }
}
