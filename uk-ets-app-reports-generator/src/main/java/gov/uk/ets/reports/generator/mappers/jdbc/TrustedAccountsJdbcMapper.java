package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.TrustedAccount;
import gov.uk.ets.reports.generator.domain.TrustedAccountsReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TrustedAccountsJdbcMapper
    implements ReportDataMapper<TrustedAccountsReportData>, RowMapper<TrustedAccountsReportData> {

    /**
     * We have a union query here to merge
     * 1) the manually added trusted accounts (present in the trusted_account table)
     * 2) the automatically added trusted accounts (all other accounts of the account's account holder).
     * <p>
     * For 2) we need a self join of the account table on the account holder id and then to filter out the record
     * that concerns the account itself (where the account id is equal to the "trusted account" id)
     */
    private static final String REPORT_QUERY = "" +
        "select a.full_identifier                  as account_number,\n" +
        "       a.account_status                   as account_status,\n" +
        "       ta.trusted_account_full_identifier as trusted_account_number,\n" +
        "       ta.status                          as trusted_account_status,\n" +
        "       ta.description                     as trusted_account_description,\n" +
        "       taa.account_name                   as trusted_account_name,\n" +
        "       ta.activation_date                 as date_of_activation,\n" +
        "       'MANUAL'                           as trusted_type\n" +
        "from account a\n" +
        "         left join trusted_account ta on a.id = ta.account_id\n" +
        "         inner join account taa on ta.trusted_account_full_identifier = taa.full_identifier\n" +
        "where a.account_status not in ('PROPOSED', 'CLOSED', 'REJECTED')\n" +
        "  and ta.status <> 'REJECTED'\n" +
        "union all\n" +
        "select a.full_identifier  as account_number,\n" +
        "       a.account_status   as account_status,\n" +
        "       ta.full_identifier as trusted_account_number,\n" +
        "       ''                 as trusted_account_status,\n" +
        "       null               as trusted_account_description,\n" +
        "       ta.account_name    as trusted_account_name,\n" +
        "       null               as date_of_activation,\n" +
        "       'AUTOMATIC'        as trusted_type\n" +
        "from account a\n" +
        "         inner join account ta on a.account_holder_id = ta.account_holder_id\n" +
        "where a.full_identifier <> ta.full_identifier\n" +
        "  and a.account_status not in ('PROPOSED', 'CLOSED', 'REJECTED')\n" +
        "order by account_number, trusted_type, trusted_account_number" +
        ";\n";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TrustedAccountsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public TrustedAccountsReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        String activationDate = rs.getString("date_of_activation");
        return TrustedAccountsReportData.builder()
            .account(Account.builder()
                .number(rs.getString("account_number"))
                .status(rs.getString("account_status"))
                .build())
            .trustedAccount(TrustedAccount.builder()
                .number(rs.getString("trusted_account_number"))
                .status(rs.getString("trusted_account_status"))
                .description(rs.getString("trusted_account_description"))
                .name(rs.getString("trusted_account_name"))
                .activationDate(activationDate != null ? LocalDateTime.parse(activationDate, inputFormatter) : null)
                .type(rs.getString("trusted_type"))
                .build())
            .build();
    }
}
