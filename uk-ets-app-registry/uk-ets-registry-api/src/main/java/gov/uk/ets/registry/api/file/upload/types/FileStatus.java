package gov.uk.ets.registry.api.file.upload.types;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FileStatus {


    NOT_SUBMITTED("Not submitted"),

    SUBMITTED("Submitted");

    private final String fileStatus;
}
