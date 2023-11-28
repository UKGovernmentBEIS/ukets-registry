package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r5itl;

/**
 * Keeps the SQL that is going to be executed on the database.
 *
 * @author gkountak
 */
public class REG5Queries {

    private REG5Queries() {}

    public static final String GET_INVALID_UNITS_LINE_QUERY =
            "select concat(tb.originating_country_code , '-',tb.start_block , '-' , tb.end_block) as serial_number, " +
                    " tb.unit_type, " +
                    " (tb.end_block - tb.start_block) + 1 as quantity, " +
                    " t.identifier as transaction_number " +
                    "from  transaction t " +
                    "left join transaction_block tb  " +
                    "on t.id  = tb.transaction_id  " +
                    "where t.status in ('STL_CHECKED_DISCREPANCY', 'CHECKED_DISCREPANCY', 'REJECTED') " +
                    "and t.transferring_account_registry_code = ?  " +
                    "and t.execution_date is not null  " +
                    "and EXTRACT(YEAR FROM t.execution_date) = ?   " +
                    "order by serial_number asc";

}
