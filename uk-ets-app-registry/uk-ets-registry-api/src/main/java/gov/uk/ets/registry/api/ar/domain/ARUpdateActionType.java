package gov.uk.ets.registry.api.ar.domain;

import gov.uk.ets.registry.api.task.domain.types.RequestType;

/**
 * Enumeration for AR update action types.
 */
public enum ARUpdateActionType {
    /**
     * Type for the action of adding a new AR to to the ARs of the account.
     */
    ADD(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
    /**
     * Type for the action of removing an AR from the ARs of the account.
     */
    REMOVE(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST),
    /**
     * Type for the action of replacing an AR with another.
     */
    REPLACE(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST),
    /**
     * Type for the action of changing the access right of an AR of the account.
     */
    CHANGE_ACCESS_RIGHTS(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST),
    /**
     * Type for the action of suspending an AR of the account.
     */
    SUSPEND(RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST),
    /**
     * Type for the action of making a suspended AR active.
     */
    RESTORE(RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST);

    private RequestType taskType;

    ARUpdateActionType(RequestType taskType) {
        this.taskType = taskType;
    }

    public RequestType getTaskType() {
        return taskType;
    }
}
