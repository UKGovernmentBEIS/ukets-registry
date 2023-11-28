package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r4itl;

/**
 * Keeps the SQL that is going to be executed on the database.
 *
 */
public class RREG4Queries {

    private RREG4Queries() {}

    public static final String GET_UNITS_WHEN_TYPE_CODE_IS_3 =
            "select concat(originating_registry_code , '-', unit_serial_block_start, '-' , unit_serial_block_end) as serial_number, " +
            " 'TCER or LCER' as unit_type , " +
            " (unit_serial_block_end - unit_serial_block_start) + 1 as quantity " +
            "from itl_notification_block " +
            "where notice_log_id = ?  " +
            " order by serial_number asc";

    public static final String GET_UNITS_WHEN_TYPE_CODE_IS_NOT_3 =
            "select concat(originating_country_code , '-', start_block, '-' , end_block) as serial_number, " +
            " unit_type , " +
            " (end_block - start_block) + 1 as quantity " +
            " from unit_block " +
            " where project_number = ? " +
            " order by serial_number asc";
}
