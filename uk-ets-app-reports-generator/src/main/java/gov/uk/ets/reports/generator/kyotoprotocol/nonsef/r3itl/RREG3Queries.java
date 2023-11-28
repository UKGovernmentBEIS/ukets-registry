package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl;

/**
 * Keeps the SQL that is going to be executed on the database.
 *
 */
public class RREG3Queries {

    private RREG3Queries() {}

    /**
     * Determine the list of notification which were sent to
     * the registry during the reported year
     */
    public static final String GET_NOTIFICATION_LIST_DURING_REPORT_YEAR =
            "select n.type, n.id as notification_id, nh.message_date, nh.target_value, nh.action_due_date, n.identifier, nh.project_number " +
            "from itl_notification n " +
            "left outer join itl_notification_history nh " +
            "on n.id = nh.notice_log_id " +
            "where n.type in ('IMPENDING_EXPIRY_OF_TCER_AND_LCER', 'REVERSAL_OF_STORAGE_FOR_CDM_PROJECT', 'NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT') " +
            "and extract (year from nh.message_date) = ? " +
            "order by n.type,nh.message_date ";

    /**
     * If notification type code is 3 (impending expiry) then set Column 4 with the following
     */
    public static final String GET_SUM_WHEN_TYPE_CODE_IS_3 =
            "select sum(unit_serial_block_end - unit_serial_block_start + 1) " +
            "from itl_notification_block " +
            "where notice_log_id = ? ";

    /**
     * Determine the related transferred quantities by executing the following SQL and then for each entry
     */
    public static final String DETERMINE_THE_RELATED_TRANSFERRED_QUANTITIES =
            "select tl.type, sum (tb.end_block - tb.start_block + 1) as qty " +
            "from transaction tl, transaction_block tb, itl_notification tn " +
            "where tl.id = tb.transaction_id " +
            "and tl.notification_identifier = tn.identifier " +
            "and (tb.block_role is null or tb.block_role ='') " +
            "and tl.status in  ('COMPLETED') " +
            "and tl.transferring_account_registry_code = 'GB' " +
            "and tn.id = ? " +
            "group by tl.type ";

    /**
     * Determine the Target Date Column 9
     */
    public static final String RESULT_AT_TARGET_DATE =
            "select coalesce(sum (tb.end_block - tb.start_block + 1),0) " +
            "from transaction tl, transaction_block tb, itl_notification tn, itl_notification_history n " +
            "where tl.id = tb.transaction_id " +
            "and tl.notification_identifier = tn.identifier " +
            "and tn.id = n.notice_log_id " +
            "and( tb.block_role is null or tb.block_role = '') " +
            "and tl.status in  ('COMPLETED') " +
            "and tl.transferring_account_registry_code = 'GB' " +
            "and tn.id = ? " +
            "and tl.last_updated <= n.action_due_date ";

}
