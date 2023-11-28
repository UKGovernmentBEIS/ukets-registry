package gov.uk.ets.publication.api.file.upload.types;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileTypes {

    REPORT_PUBLICATION(Collections.singletonList(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                         "The selected file must be an XLSX");

    private final List<String> mimeTypes;

    private final String error;

    public boolean isTheTypeOf(String mimeTypeInput) {
        return this.mimeTypes.contains(mimeTypeInput);
    }

    public String getError() {
        return error;
    }
}
