package gov.uk.ets.registry.api.transaction.domain.type;

/**
 * Enumerates the various predefined external accounts.
 */
public enum ExternalPredefinedAccount {

    /**
     * CDM SOP account.
     */
    CDM_SOP_ACCOUNT,

    /**
     * CDM CCS net reversal cancellation account.
     */
    CCS_NET_REVERSAL_CANCELLATION_ACCOUNT,

    /**
     * CDM non submission of verification cancellation account.
     */
    CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT,

    /**
     * CDM excess issuance account for CP2.
     */
    CDM_EXCESS_ISSUANCE_ACCOUNT_FOR_CP2;

}
