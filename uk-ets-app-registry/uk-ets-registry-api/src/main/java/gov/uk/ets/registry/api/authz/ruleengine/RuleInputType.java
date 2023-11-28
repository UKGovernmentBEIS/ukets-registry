package gov.uk.ets.registry.api.authz.ruleengine;

/**
 * Input types that can be used.
 */
public enum RuleInputType {
    ACCOUNT_ID,
    REQUEST_ID,
    TRUSTED_ACCOUNT_ID,
    TRUSTED_ACCOUNT_FULL_ID,
    ACCOUNT_FULL_ID,
    ACCOUNT_OPERATOR_UPDATE,
    TASK_REQUEST_ID,
    TASK_FILE,
    TASK_REQUEST_IDS,
    TASK_ASSIGNEE_URID,
    TASK_OUTCOME,
    TASK_COMPLETE_COMMENT,
    /**
     * Transaction.
     */
    TRANSACTION,

    /**
     * Transaction identifier.
     */
    TRANSACTION_IDENTIFIER,
    /**
     * The AR candidate request.
     */
    AR_CANDIDATE,
    /**
     * The AR candidate urid request.
     */
    AR_CANDIDATE_URID,
    /**
     * The AR candidate and predecessor request.
     */
    AR_CANDIDATE_AND_PREDECESSOR,
    /**
     * The transaction type.
     */
    TRANSACTION_TYPE,
    /**
     * The requests to update the account holder details
     */
    ACCOUNT_HOLDER_ID,
    /**
     * The id of the user requested.
     */
    URID,
    /**
     * The ids of multiple requested users.
     */
    URIDS,
    /**
     * The new email that user requested to change to.
     */
    NEW_EMAIL,
    /**
     * The new status that user requested to change to.
     */
    NEW_STATUS,
    /***
     *  * The otp code
     */
    OTP;
}
