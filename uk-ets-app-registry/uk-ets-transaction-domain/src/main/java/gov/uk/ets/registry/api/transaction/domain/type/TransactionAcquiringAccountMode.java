package gov.uk.ets.registry.api.transaction.domain.type;

/**
 * Enumerated the various acquiring account modes.
 */
public enum TransactionAcquiringAccountMode {

    /**
     * The acquiring account is explicitly specified during the transaction proposal.
     */
    EXPLICIT,

    /**
     * The acquiring account is the same as the transferring account, e.g. during Carry Over CERs.
     */
    SAME_AS_TRANSFERRING,

    /**
     * The acquiring account is a predefined outside the registry, e.g. during Transfer to SOP for AAUs.
     */
    PREDEFINED_OUTSIDE_REGISTRY,

    /**
     * The acquiring account is a predefined inside the registry, e.g. during Mandatory Cancellation.
     */
    PREDEFINED_INSIDE_REGISTRY,

    /**
     * There are multiple predefined acquiring accounts inside the registry, e.g. during Return of Excess Allocations.
     */
    VARIABLE_INSIDE_REGISTRY,

    /**
     * The acquiring account is the transferring account of the reversed transaction.
     */
    REVERSED

}
