package gov.uk.ets.registry.api.transaction.checks;

/**
 * Enumerates the various business check groups.
 */
public enum BusinessCheckGroup {

    /**
     * Groups checks related to accounts.
     */
    ACCOUNT,

    /**
     * Groups checks related to units.
     */
    UNITS,

    /**
     * Groups checks related to transferring account.
     */
    TRANSFERRING_ACCOUNT;

}
