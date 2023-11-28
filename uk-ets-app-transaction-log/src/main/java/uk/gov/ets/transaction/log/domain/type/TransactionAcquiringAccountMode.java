package uk.gov.ets.transaction.log.domain.type;

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
    PREDEFINED_INSIDE_REGISTRY;

}
