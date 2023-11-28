package gov.uk.ets.registry.api.tal.domain.types;

public enum TrustedAccountStatus {
    /**
     * The trusted account has been proposed and its approval is pending.
     */
    PENDING_ADDITION_APPROVAL,
    /**
     * The trusted account has been approved and its activation is pending (delayed).
     */
    PENDING_ACTIVATION,
    /**
     * The trusted account has been approved and activated.
     */
    ACTIVE,
    /**
     * The trusted account has been rejected.
     */
    REJECTED,

    /**
     * The trusted account has been manually cancelled after when was in status PENDING_ACTIVATION
     */
    CANCELLED,
    /**
     * The trusted account has been marked for removal and its approval is pending.
     */
    PENDING_REMOVAL_APPROVAL
}
