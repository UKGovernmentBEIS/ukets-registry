package gov.uk.ets.registry.api.itl.notice.domain.type;

/**
 * Groups the ITL notifications, to assist retrieving the appropriate acquiring account type.
 */
public enum NoticeAcquiringAccountMode {

    /**
     * These ITL notifications are fulfilled by a transaction that targets the UK registry,
     * and apply to a single type of acquiring account.
     *
     * Applies to the following ITL notifications:
     * <ul>
     *     <li>Net source cancellation (1)</li>
     *     <li>Non-compliance cancellation (2)</li>
     * </ul>
     */
    SPECIFIC_INTERNAL,

    /**
     * These ITL notifications are fulfilled by a transaction whose acquiring account depends on the unit type.
     *
     * Applies to the following ITL notification:
     * <ul>
     *     <li>Impending tCER/lCER expiry (3)</li>
     * </ul>
     */
    VARIABLE_BY_UNIT_TYPE,

    /**
     * These ITL notifications are fulfilled by more than one transaction type. Thus, the acquiring account depends
     * on the transaction type.
     *
     * Applies to the following ITL notifications:
     * <ul>
     *     <li>Reversal of storage for CDM project (4)</li>
     *     <li>Non-submission of certification report for CDM project (5)</li>
     * </ul>
     */
    VARIABLE_BY_TRANSACTION,

    /**
     * These ITL notifications are fulfilled by a transaction that targets the CDM registry.
     *
     * Applies to the following ITL notifications:
     * <ul>
     *   <li>Excess issuance for CDM project (6)</li>
     *   <li>Net reversal of storage of a CDM CCS project (12)</li>
     *   <li>Non-submission of verification report for a CDM CCS project (13)</li>
     * </ul>
     */
    CDM,

    /**
     * These ITL notifications either do not require a transaction, or their acquiring account is determined by the
     * main transaction flow, as any other transaction.
     *
     * Applies to the following ITL notifications:
     * <ul>
     *     <li>Commitment Period reserve (7) - No transaction needed.</li>
     *     <li>Unit carry-over (8) - Out of scope, since it applies only during CP1.</li>
     *     <li>Expiry date change (9) - The transferring account is the same as the acquiring.</li>
     *     <li>Notification update (10) - No transaction needed.</li>
     *     <li>EU15 Commitment Period reserve (11) - Out of scope, since it applies only during CP1.</li>
     * </ul>
     */
    NONE

}
