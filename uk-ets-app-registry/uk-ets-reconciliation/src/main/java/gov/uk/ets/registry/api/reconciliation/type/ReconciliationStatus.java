package gov.uk.ets.registry.api.reconciliation.type;

import java.util.List;

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
    COMPLETED;

    /**
     * Retrieves the statuses depicting a pending reconciliation.
     * @return some statuses.
     */
    public static final List<ReconciliationStatus> getPendingStatuses() {
        return List.of(INITIATED);
    }

    /**
     * Retrieves the statuses depicting a completed reconciliation.
     * @return some statuses.
     */
    public static final List<ReconciliationStatus> getFinalStatuses() {
        return List.of(INCONSISTENT, COMPLETED);
    }

}
