package gov.uk.ets.registry.api.file.upload.allocationtable.error;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(of = {"code", "componentId"})
public class AllocationTableActionError implements Serializable {

    @Serial
    private static final long serialVersionUID = 369920104387821403L;

    /**
     * Error code for the case that the allocation table file uploaded contains business errors.
     */
    public static final String ALLOCATION_TABLE_FILE_BUSINESS_ERRORS = "ALLOCATION_TABLE_FILE_BUSINESS_ERRORS";

    /**
     * The error code.
     */
    String code;
    /**
     * The error message.
     */
    String message;
    /**
     * The component Identifier.
     */
    String componentId;
    /**
     * The identifier of the error file.
     */
    Long errorFileId;
    /**
     * The name of the error file.
     */
    String errorFilename;
}
