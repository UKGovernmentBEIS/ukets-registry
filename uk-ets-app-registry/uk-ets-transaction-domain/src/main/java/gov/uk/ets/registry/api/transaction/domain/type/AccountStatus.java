package gov.uk.ets.registry.api.transaction.domain.type;

import lombok.Getter;

/**
 * Enumerates the various account statuses.
 */
@Getter
public enum AccountStatus {

    /**
     * The open status.
     */
    OPEN("Open"),

    /**
     * All transactions restricted.
     * Account cannot send or receive units.
     */
    ALL_TRANSACTIONS_RESTRICTED("All transactions restricted'"),

    /**
     * The status in which the account is allowed to send or receive units for some transactions
     * (Surrender/Return of excess allocation /Reversal of allocation).
     */
    SOME_TRANSACTIONS_RESTRICTED("Some transactions restricted"),

    /**
     * The partially suspended status.
     * Account may receive units but cannot send units
     */
    SUSPENDED_PARTIALLY("Suspended partially"),

    /**
     * The fully suspended status.
     * Account cannot send or receive units
     */
    SUSPENDED("Suspended"),

    /**
     * The transfer pending status.
     * Account may receive units but cannot send units.
     */
    TRANSFER_PENDING("Transfer pending"),

    /**
     * The closure pending status.
     */
    CLOSURE_PENDING("Closure pending"),
    
    /**
     * The closed status.
     */
    CLOSED("Closed"),

    /**
     * The proposed status.
     */
    @Deprecated
    PROPOSED("Proposed"),

    /**
     * The rejected status.
     *
     * @deprecated since
     */
    @Deprecated
    REJECTED("Rejected");

    private final String description;

    AccountStatus(String description) {
        this.description = description;
    }

    public static boolean isClosedOrHasClosureRequests(AccountStatus status) {
        return AccountStatus.CLOSURE_PENDING.equals(status) ||
               AccountStatus.CLOSED.equals(status);
    };

    public boolean isTransferPending() {
        return this == AccountStatus.TRANSFER_PENDING;
    }

    public boolean someTransactionsRestricted() {
        return this == AccountStatus.SOME_TRANSACTIONS_RESTRICTED;
    }
    public boolean allTransactionsRestricted() {
        return this == AccountStatus.ALL_TRANSACTIONS_RESTRICTED;
    }

    public boolean isSuspendedOrPartiallySuspended() {
        return this == AccountStatus.SUSPENDED || this == AccountStatus.SUSPENDED_PARTIALLY;
    }
}
