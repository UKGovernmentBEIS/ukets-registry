package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl;

/**
 * Keeps the SQL that is going to be executed on the database.
 *
 * @author gkountak
 */
public class RREG2Queries {

    private RREG2Queries() {}

    private static final String TRANSACTION_TYPES = " t.type in ('IssuanceDecoupling', 'IssuanceCP0', 'IssuanceOfFormerEUA', 'IssueOfAAUsAndRMUs'," +
            "   'ConversionCP1', 'ConversionA' , 'ConversionB', 'TransferToSOPForConversionOfERU', 'ConversionOfSurrenderedFormerEUA'," +
            "   'ExternalTransfer', 'ExternalTransferCP0', 'TransferToSOPforFirstExtTransferAAU'," +
            "   'CancellationKyotoUnits', 'MandatoryCancellation', 'Art37Cancellation', 'AmbitionIncreaseCancellation', 'CancellationCP0', 'CancellationAgainstDeletion'," +
            "   'Retirement', 'RetirementCP0', 'RetirementOfSurrenderedFormerEUA'," +
            "   'Replacement'," +
            "   'CarryOver_AAU', 'CarryOver_CER_ERU_FROM_AAU'," +
            "   'ExpiryDateChange'," +
            "   'InternalTransfer') ";

    private static final String TRANSACTION_STATUSES = " t.status in ('TERMINATED', 'CANCELLED', 'FAILED', 'COMPLETED') ";

    private static final String RESPONSE_CODES = " tr.error_code in (4003, 4004, 4010, 4011, 4012, 4014, 4015, 5008, " +
            "5009, 5018, 5053, 5054, 5056, 5061, 5101, 5102, 5103, 5104, 5106, 5156, 5209, 5211, 5212, 5213, 5214, 5215, " +
            "5220, 5255, 5256, 5301, 5304, 5305, 5306, 5307, 5312, 5313) ";

    /**
     * Determine GeneralReportedYearCodeCount, a map whose key is the response code and whose content
     * is how many times the response code has been triggered by any registry during the reported year
     */
    public static final String GET_GENERAL_REPORTED_YEAR_RESPONSE_COUNT = "select tr.error_code,count (distinct tr.id) " +
            " from transaction_response tr inner join transaction t " +
            " on tr.transaction_id = t.id " +
            " and " + TRANSACTION_STATUSES +
            " and transferring_account_registry_code = 'GB'" +
            " and " + RESPONSE_CODES +
            " and extract (YEAR from t.last_updated) = ? " +
            " and " + TRANSACTION_TYPES +
            " group by tr.error_code " +
            " order by tr.error_code";

    /**
     * Determine RegistryTxCountReportedYear, a map containing the amount of
     * proposed transactions for each registry during the reported year (registry code is the key)
     *
     */
    public static final String GET_REGISTRY_TX_COUNT_REPORTED_YEAR = "select t.transferring_account_registry_code, count(t.id) " +
            " from transaction t " +
            " where " + TRANSACTION_STATUSES +
            " and t.transferring_account_registry_code = 'GB' " +
            " and extract (YEAR from t.last_updated) = ? " +
            " and " + TRANSACTION_TYPES +
            " group by t.transferring_account_registry_code ";

    /**
     * Determine RegistryPreviousYear, the amount of transactions proposed by
     * the registry previous to the reported year
     */
    public static final String GET_REGISTRY_PREVIOUS_YEAR = "select count(distinct id)"
            + " from transaction t "
            + " where transferring_account_registry_code = ? "
            + "and  " + TRANSACTION_STATUSES
            + " and " + TRANSACTION_TYPES
            + "  and extract (YEAR from last_updated) < ? ";


    /**
     * Retrieve the list of raised response codes
     *  by the registry during the reported year
     */
    public static final String GET_REGISTRY_REPORTED_YEAR_CODE_COUNT = "select distinct tr.error_code " +
            " from transaction_response tr inner join transaction t " +
            " on tr.transaction_id = t.id " +
            " where " + TRANSACTION_STATUSES +
            " and t.transferring_account_registry_code = ? " +
            " and " + RESPONSE_CODES +
            " and " + TRANSACTION_TYPES +
            " and extract (YEAR from t.last_updated) = ? " +
            " order by tr.error_code";

    /**
     * Determine their RegistryPreviousYearsCodeCount
     */
    public static final String GET_REGISTRY_PREVIOUS_YEARS_CODE_COUNT = "select count(distinct t.id) " +
            " from transaction_response tr inner join transaction t " +
            " on tr.transaction_id = t.id " +
            " where " + TRANSACTION_STATUSES +
            " and " + TRANSACTION_TYPES +
            " and t.transferring_account_registry_code = 'GB' " +
            " and tr.error_code = ? " +
            " and extract (YEAR from t.last_updated) < ?";


    /**
     * Determine the list of transactions proposed by the registry during the
     * reported year
     */
    public static final String GET_LIST_OF_TRANSACTIONS_PROPOSED = "select t.identifier, min(tr.date_occurred)  as message_date, t.type,t.status " +
            " from transaction_response tr inner join transaction t " +
            " on tr.transaction_id=t.id " +
            " where t.transferring_account_registry_code = 'GB' " +
            " and extract (YEAR from t.last_updated) = ? " +
            " and tr.error_code = ? " +
            " and " + TRANSACTION_STATUSES +
            " and " + TRANSACTION_TYPES +
            " group by t.identifier, t.type, t.status " +
            " order by message_date";

    /**
     * Determine variable COUNT: how much times the registry in item.key has raised the response code in entry.key during the reported year
     */
    public static final String GET_COUNT = "select count(distinct t.id) " +
            " from transaction_response tr inner join transaction t " +
            " on tr.transaction_id = t.id " +
            " where " + TRANSACTION_STATUSES +
            " and " + TRANSACTION_TYPES +
            " and t.transferring_account_registry_code = ? " +
            " and tr.error_code = ? " +
            " and extract (YEAR from t.last_updated) = ?";

    public static final String GET_UNITS_INVOLVED = " select concat(tb.originating_country_code , '-',tb.start_block , '-' , tb.end_block) as serial_number, " +
            " tb.unit_type , " +
            " (tb.end_block - tb.start_block) + 1 as quantity, " +
            " t.identifier as transaction_identifier " +
            " from  transaction t " +
            " inner join transaction_block tb " +
            " on t.id = tb.transaction_id " +
            " where tb.unit_type in ('AAU', 'ERU_FROM_AAU', 'ERU_FROM_RMU', 'RMU' , 'CER', 'TCER', 'LCER', 'FORMER_EUA') " +
            " and t.transferring_account_registry_code = 'GB' " +
            " and extract (YEAR from t.last_updated) = ? " +
            " and " + TRANSACTION_TYPES +
            " and " + TRANSACTION_STATUSES +
            " order by t.identifier ";

}
