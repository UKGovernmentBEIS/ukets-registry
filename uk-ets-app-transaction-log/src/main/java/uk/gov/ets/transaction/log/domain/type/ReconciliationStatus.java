package uk.gov.ets.transaction.log.domain.type;

/**
 * Represents the various reconciliation statuses.
 */
public enum ReconciliationStatus {

    /**
     * The reconciliation was just initiated.
     */
    INITIATED,

    /**
     * The reconciliation has moved to an inconsistent state.
     */
    INCONSISTENT,

    /**
     * The reconciliation was completed.
     */
    COMPLETED
}

