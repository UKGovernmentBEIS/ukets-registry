package gov.uk.ets.registry.api.allocation.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AllocationError {

    INVALID_ALLOCATION_YEAR(
        "You can submit an allocation request for current or previous years of the existing ETS phase"),

    PENDING_ALLOCATION_TASK_APPROVAL(
        "You cannot submit an allocation request while another allocation request is pending approval for the same year and category."),

    PENDING_ALLOCATION_TRANSACTIONS(
        "You cannot submit an allocation request while there are allocation transactions pending execution for the same year and category."),

    PENDING_UPLOAD_ALLOCATION_TABLE(
        "Allocation request cannot be submitted, if there is a pending 'Upload allocation table' task for the same category.");

    private final String message;
}
