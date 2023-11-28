package gov.uk.ets.registry.api.allocation.type;

/**
 * Represents the various allocation job statuses.
 */
public enum AllocationJobStatus {

    /**
     * The job is scheduled.
     */
    SCHEDULED,

    /**
     * The job is being executed.
     */
    RUNNING,

    /**
     * The job failed.
     */
    FAILED,

    /**
     * The job runs successfully and all transactions completed successfully.
     */
    COMPLETED,

    /**
     * The job runs successfully but there is a failure in some transactions.
     */
    COMPLETED_WITH_FAILURES,

    /**
     * A scheduled job that has been manually cancelled
     */
    CANCELLED

}
