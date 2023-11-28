package gov.uk.ets.registry.api.file.upload.allocationtable.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllocationTableError {

    INVALID_TABLE_EMPTY(4015,"Allocation table is empty"),

    INVALID_COLUMNS(4003,"Invalid columns - Installation ID or Operator ID or Year columns missing"),

    INVALID_COLUMNS_INSTALLATION(4003,"Invalid column - Installation ID is missing"),

    INVALID_COLUMNS_OPERATOR(4003,"Invalid column - Operator ID is missing"),

    INVALID_COLUMNS_YEAR_MISSING(4003,"Invalid column - Year column/s missing"),

    INVALID_COLUMNS_YEAR(4009,"Invalid year - All years must be within the period specified in the file name"),

    INVALID_YEAR_PHASE(4010,"Invalid year - all years must be within the current phase (2021-2030) Year(s) %s"),

    INVALID_QUANTITY(4004,"Invalid quantity - only zero or positive values are permitted Year %s"),

    EMPTY_ID(4005,"Invalid Installation or Operator ID - the ID must not be empty"),

    ID_NOT_IN_REGISTRY(4006,"Invalid Installation or Operator ID - the ID does not exist in the Registry"),

    DUPLICATE_ID_ENTRIES(4007,"Invalid Installation or Operator ID - duplicate entries"),

    MISSING_ID(4008,"Missing Installation or Operator ID(s) %s - " +
        "allocation records have been deleted from the allocation " +
        "table even though deletions are not permitted"),

    PENDING_ALLOCATION_TABLE_TASK_APPROVAL(4013,"You cannot upload a new allocation table," +
        " while another allocation table of the same category is pending approval"),

    NON_ZERO_QUANTITY_FOR_INACTIVE_ENTITY(4010,
        "Invalid quantity - Installation or Operator should operate the corresponding" +
            " year of the specified non zero quantity"),

    PENDING_ALLOCATION_REQUEST_TASK_OR_ALLOCATION_JOB(4014,
        "You cannot upload a new allocation table, while an allocation allowances task for the same category " +
            "is pending, or while an allocation job for the same category is scheduled or running.");

    private final int code;
    private final String message;
}
