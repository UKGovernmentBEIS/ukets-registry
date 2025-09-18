package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.Transaction;
import gov.uk.ets.reports.generator.domain.TransactionSearchReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionSearchJdbcMapper
    implements ReportDataMapper<TransactionSearchReportData>, RowMapper<TransactionSearchReportData> {

    private final JdbcTemplate jdbcTemplate;

    private List<String> governmentAccountTypeLabels = new ArrayList<>();
    private List<String> userAccountsfullIdentifiers = new ArrayList<>();
    Map<String, String> trustedAccounts = new HashMap<>();

    @Override
    public List<TransactionSearchReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {

        if (reportQueryInfo.getRequestingRole() == null) {
            // Query for finding the type labels correspond to government accounts
            governmentAccountTypeLabels = jdbcTemplate.queryForList("select type_label\n" +
                    "from account\n" +
                    "where registry_account_type in\n" +
                    "      ('UK_TOTAL_QUANTITY_ACCOUNT', 'UK_AUCTION_ACCOUNT', 'UK_ALLOCATION_ACCOUNT', 'UK_NEW_ENTRANTS_RESERVE_ACCOUNT',\n" +
                    "       'UK_AVIATION_ALLOCATION_ACCOUNT', 'UK_DELETION_ACCOUNT', 'UK_SURRENDER_ACCOUNT',\n" +
                    "       'UK_MARKET_STABILITY_MECHANISM_ACCOUNT', 'UK_GENERAL_HOLDING_ACCOUNT', 'NATIONAL_HOLDING_ACCOUNT')",
                String.class);

            // Query for finding the accounts that user is appointed as AR
            userAccountsfullIdentifiers = jdbcTemplate.queryForList(
                "select full_identifier\n" +
                    "from account a\n" +
                    "         inner join account_access aa on a.id = aa.account_id and aa.access_right <> 'ROLE_BASED'\n" +
                    "         inner join users u on aa.user_id = u.id and urid = '" + reportQueryInfo.getUrid() + "'",
                String.class);

            /*
            The query returns the account full identifier, the trusted account full identifier and the description.
            We may have the same account added to the TAL of different accounts with a different description, so we
            need the combination of transferring and acquiring full identifiers to get the correct description.

            Additionally, there is a case where an account has a trusted account appear twice in the TAL, once manually and once
            automatically added (if the manually trusted account account holder was changed in the meanwhile).

            UK-100-10000233-0-60	UK-100-10000098-0-56	TEST
            UK-100-10000233-0-60	UK-100-10000098-0-56	UK-100-10000098-0-56

            As the query should return exactly one row per account-trusted account combination,
            we use max on description to select the first*/

            List<Map<String, Object>> trustedAccountsResult = jdbcTemplate.queryForList(
                "SELECT full_identifier, trusted_account_full_identifier, max(description) as description FROM (\n" +
                "select a1.full_identifier, a2.full_identifier as trusted_account_full_identifier, a2.account_name as description\n" +
                "from account a1\n" +
                "         inner join account a2 on a1.account_holder_id = a2.account_holder_id\n" +
                "where a1.id != a2.id\n" +
                "  and a1.id in (select account_id\n" +
                "                from account_access aa\n" +
                "                         inner join users u on aa.user_id = u.id and u.urid = '"+reportQueryInfo.getUrid()+"')\n" +
                "union\n" +
                "select a.full_identifier, trusted_account_full_identifier, description\n" +
                "     from trusted_account ta inner join account a on ta.account_id = a.id\n" +
                "     where account_id in (select account_id\n" +
                "                          from account_access aa\n" +
                "                                   inner join users u on aa.user_id = u.id and u.urid = '"+reportQueryInfo.getUrid()+"')\n" +
                "            and ta.status in ('ACTIVE', 'PENDING_ACTIVATION', 'PENDING_ADDITION_APPROVAL', 'PENDING_REMOVAL_APPROVAL')\n" +
                "order by 1\n" +
                ") t group by  full_identifier, trusted_account_full_identifier");

            trustedAccounts = trustedAccountsResult.stream()
                .collect(Collectors.toMap(k -> k.get("full_identifier") + (String) k.get("trusted_account_full_identifier"), k -> (String) k.get("description")));
        }

        List<TransactionSearchReportData> data = jdbcTemplate.query(reportQueryInfo.getQuery(), this);
        data.stream()
            .sorted(Comparator.comparing(t -> t.getTransaction().getLastUpdated()))
            .forEach(d -> hideDataForEnrolledNonAdminUsers(d, reportQueryInfo));
        return data;
    }

    @Override
    public TransactionSearchReportData mapRow(ResultSet rs, int i) throws SQLException {
        return
            TransactionSearchReportData.builder()
                .transaction(Transaction.builder()
                    .id(rs.getString(1))
                    .type(rs.getString(2))
                    .reversesIdentifier(rs.getString(24))
                    .reversedByIdentifier(rs.getString(25))
                    .status(rs.getString(3))
                    .lastUpdated(parseDate(rs.getString(4)))
                    .quantity(retrieveLong(rs, 5))
                    .unitType(rs.getString(6))
                    .transactionStart(parseDate(rs.getString(23)))
//                    .failureReasons() // TODO we do not have this info
                    .build())
                .transferringAccount(Account.builder()
                    .number(rs.getString(7))
                    .name(rs.getString(8))
                    .type(rs.getString(13))
                    .accountHolderName(rs.getString(11))
                    .build())
                .acquiringAccount(Account.builder()
                    .number(rs.getString(15))
                    .name(rs.getString(16))
                    .type(rs.getString(21))
                    .accountHolderName(rs.getString(19))
                    .build())
                .build();

    }


    // According to UKETS-6383 some report data should not appear for non admin or authority users
    private void hideDataForEnrolledNonAdminUsers(TransactionSearchReportData data, ReportQueryInfoWithMetadata info) {
        if (info.getRequestingRole() == null) {
            // For the government accounts the account number should not be included in the report
            if (governmentAccountTypeLabels.contains(data.getAcquiringAccount().getType())) {
                data.getAcquiringAccount().setNumber(null);
            }
            if (governmentAccountTypeLabels.contains(data.getTransferringAccount().getType())) {
                data.getTransferringAccount().setNumber(null);
            }

            // For non government accounts to which the user is not appointed as AR, the account name information should not be included
            if (!governmentAccountTypeLabels.contains(data.getAcquiringAccount().getType()) &&
                !userAccountsfullIdentifiers.contains(data.getAcquiringAccount().getNumber())) {
                data.getAcquiringAccount().setName(null);
            }
            if (!governmentAccountTypeLabels.contains(data.getTransferringAccount().getType()) &&
                !userAccountsfullIdentifiers.contains(data.getTransferringAccount().getNumber())) {
                data.getTransferringAccount().setName(null);
            }

            // For accounts included in the TAL (manual or automatic) of the accounts to which the user is appointed as AR the account description should be included
            String talDescription = trustedAccounts.get(data.getTransferringAccount().getNumber()+data.getAcquiringAccount().getNumber());
            if (talDescription != null) {
                data.getAcquiringAccount().setName(talDescription);
            }
        }
    }
}
