package gov.uk.ets.registry.api.file.upload.types;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileTypes {

    ALLOCATION_TABLE(Collections.singletonList(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                     "The selected file must be an XLSX"),
    EMISSIONS_TABLE(Collections.singletonList(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                         "The selected file must be an XLSX"),
    BULK_AR_TABLE(Collections.singletonList(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                  "The selected file must be an XLSX"),

    REQUESTED_DOCUMENT(Arrays.asList("application/pdf", "image/jpeg", "image/png"),
        "The selected file must be PDF, PNG or JPG"),

    AD_HOC_EMAIL_RECIPIENTS(Arrays.asList("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
        "Invalid file type. File should be in .xlsx");

    private final List<String> mimeTypes;

    private final String error;

    public boolean isTheTypeOf(String mimeTypeInput) {
        return this.mimeTypes.contains(mimeTypeInput);
    }

    public String getError() {
        return error;
    }
}
