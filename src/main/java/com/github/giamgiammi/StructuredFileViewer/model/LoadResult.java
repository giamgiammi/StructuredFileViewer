package com.github.giamgiammi.StructuredFileViewer.model;

import com.github.giamgiammi.StructuredFileViewer.core.DataModel;
import com.github.giamgiammi.StructuredFileViewer.core.DataModelType;
import lombok.Builder;
import lombok.NonNull;

import java.nio.file.Path;

/**
 * Represents the result of loading data into a specific data model. This immutable
 * record encapsulates metadata and references for a load operation.
 *
 * @param <DATA> the type of the data structure returned by the associated data model
 */
@Builder
public record LoadResult<DATA>(
        @NonNull
        DataModelType type,
        @NonNull
        DataModel<?, DATA> model,
        Path file,
        String fileContent
) {
}
