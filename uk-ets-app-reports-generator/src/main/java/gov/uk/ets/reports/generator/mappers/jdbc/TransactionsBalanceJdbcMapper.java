package gov.uk.ets.reports.generator.mappers.jdbc;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.reports.generator.domain.TransactionsBalanceReportData;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionsBalanceJdbcMapper
    implements ReportDataMapper<TransactionsBalanceReportData>, RowMapper<TransactionsBalanceReportData> {

    private static final String GROUP_A_LABEL = "A";
    private static final String GROUP_B_LABEL = "B";

    //language=PostgreSQL
    /**
     * Get the balance for every ets account (group A)  for transactions where the account is acquiring
     * and where the requesting user has active access to.
     */
    private static final String REPORT_QUERY_GROUP_A_ACQUIRING_SUM =
        "select 'A' as subReportType, a.type_label as accountType, sum(t.quantity) as balance\n" +
            "from account a\n" +
            "         inner join transaction t on a.identifier = t.acquiring_account_identifier\n" +
            "         INNER JOIN account_access as aa\n" +
            "                    on a.id = aa.account_id\n" +
            "         INNER JOIN users as u\n" +
            "                    on aa.user_id = u.id\n" +
            "where a.type_label in  ('ETS - Operator holding account',\n" +
            "                                        'ETS - Aircraft operator holding account',\n" +
            "                                        'ETS - Maritime operator holding account',\n" +
            "                                        'ETS - Trading account')\n" +
            "  and t.status = 'COMPLETED'\n" +
            "  and t.execution_date between ? AND ?\n" +
            "  and u.urid = ?\n" +
            "  and aa.state = 'ACTIVE'\n" +
            "group by a.type_label\n" +
            "order by balance desc";

    //language=PostgreSQL
    /**
     * Get the balance for every ets account (group A)  for transactions where the account is transferring
     * and where the requesting user has active access to.
     * <p>
     * NOTE: for PROPOSED, DELAYED and AWAITING_APPROVAL transaction
     * statuses the quantities are considered still in possession of the transferring account, so they must be added to
     * the balance (thus the multiplication with -1).
     * </p>
     * <p>
     * NOTE 2: for PROPOSED and DELAYED transactions there is no execution date so we must check the task's completion
     * date. Also for AWAITING_APPROVAL transactions the task is not complete so we check the tasks' initiation date.
     * </p>
     */
    private static final String REPORT_QUERY_GROUP_A_TRANSFERRING_SUM =
        "select 'A'          as subReportType,\n" +
            "       a.type_label as accountType,\n" +
            "       sum(\n" +
            "               case\n" +
            "                   when tx.status in ('COMPLETED') then tx.quantity\n" +
            "                   when tx.status in ('PROPOSED', 'DELAYED', 'AWAITING_APPROVAL') then tx.quantity * -1\n" +
            "                   end\n" +
            "           )        as balance\n" +
            "from account a\n" +
            "         inner join transaction tx on a.identifier = tx.transferring_account_identifier\n" +
            "         left join task_transaction tt on tt.transaction_identifier = tx.identifier\n" +
            "         left join task t on tt.task_id  = t.id\n" +
            "         INNER JOIN account_access as aa\n" +
            "                    on a.id = aa.account_id\n" +
            "         INNER JOIN users as u\n" +
            "                    on aa.user_id = u.id\n" +
            "where a.type_label in ('ETS - Operator holding account',\n" +
            "                       'ETS - Aircraft operator holding account',\n" +
            "                       'ETS - Maritime operator holding account',\n" +
            "                       'ETS - Trading account')\n" +
            "  and (\n" +
            "        (tx.status in ('COMPLETED') and tx.execution_date between ? AND ?) or\n" +
            "        (tx.status in ('PROPOSED', 'DELAYED') and t.completed_date between ? AND ?) or\n" +
            "        (tx.status in ('AWAITING_APPROVAL') and t.initiated_date between ? AND ?)\n" +
            "    )\n" +
            "  and u.urid = ?\n" +
            "  and aa.state = 'ACTIVE'\n" +
            "group by a.type_label\n" +
            "order by balance desc";

    //language=PostgreSQL
    /**
     * Get the balance for every central account (group B) for transactions where the account is acquiring
     * and where the requesting user has active access to.
     */
    private static final String REPORT_QUERY_GROUP_B_ACQUIRING_SUM =
        "select 'B' as subReportType, a.type_label as accountType, sum(t.quantity) as balance\n" +
            "from account a\n" +
            "         inner join transaction t on a.identifier = t.acquiring_account_identifier\n" +
            "         INNER JOIN account_access as aa\n" +
            "                    on a.id = aa.account_id\n" +
            "         INNER JOIN users as u\n" +
            "                    on aa.user_id = u.id\n" +
            "where a.type_label in ('ETS - UK Auction delivery account',\n" +
            "                           'ETS - UK Total Quantity Account',\n" +
            "                           'ETS - UK Auction Account',\n" +
            "                           'ETS - UK Allocation Account',\n" +
            "                           'ETS - UK Surrender Account',\n" +
            "                           'ETS - UK New Entrants Reserve Account',\n" +
            "                           'ETS - UK Market Stability Mechanism Account',\n" +
            "                           'ETS - UK Deletion Account',\n" +
            "                           'ETS - UK General Holding Account')\n" +
            "  and t.status = 'COMPLETED'\n" +
            "  and t.execution_date between ? AND ?\n" +
            "  and u.urid = ?\n" +
            "  and aa.state = 'ACTIVE'\n" +
            "group by a.type_label\n" +
            "order by balance desc";

    //language=PostgreSQL
    /**
     * Get the balance for every central account (group B) for transactions where the account is transferring
     * and where the requesting user has access to. This will be subtracted from the corresponding acquiring sum.
     * <p>
     * NOTE: for PROPOSED, DELAYED and AWAITING_APPROVAL transaction
     * statuses the quantities are considered still in possession of the transferring account, so they must be added to
     * the balance (thus the multiplication with -1).
     * </p>
     * <p>
     * * NOTE 2: for PROPOSED and DELAYED transactions there is no execution date so we must check the task's completion
     * * date. Also for AWAITING_APPROVAL transactions the task is not complete so we check the tasks' initiation date.
     * * </p>
     */
    private static final String REPORT_QUERY_GROUP_B_TRANSFERRING_SUM =
        "select 'B'          as subReportType,\n" +
            "       a.type_label as accountType,\n" +
            "       sum(\n" +
            "               case\n" +
            "                   when t.status in ('COMPLETED') then t.quantity\n" +
            "                   when t.status in ('PROPOSED', 'DELAYED', 'AWAITING_APPROVAL') then t.quantity * -1\n" +
            "                   end\n" +
            "           )        as balance\n" +
            "from account a\n" +
            "         inner join transaction t on a.identifier = t.transferring_account_identifier\n" +
            "         left join task_transaction tt on tt.transaction_identifier = t.identifier\n" +
            "         left join task on tt.task_id  = task.id\n" +
            "         INNER JOIN account_access as aa\n" +
            "                    on a.id = aa.account_id\n" +
            "         INNER JOIN users as u\n" +
            "                    on aa.user_id = u.id\n" +
            "where a.type_label in ('ETS - UK Auction delivery account',\n" +
            "                       'ETS - UK Total Quantity Account',\n" +
            "                       'ETS - UK Auction Account',\n" +
            "                       'ETS - UK Allocation Account',\n" +
            "                       'ETS - UK Surrender Account',\n" +
            "                       'ETS - UK New Entrants Reserve Account',\n" +
            "                       'ETS - UK Market Stability Mechanism Account',\n" +
            "                       'ETS - UK Deletion Account',\n" +
            "                       'ETS - UK General Holding Account')\n" +
            "  and (\n" +
            "        (t.status in ('COMPLETED') and t.execution_date between ? AND ?) or\n" +
            "        (t.status in ('PROPOSED', 'DELAYED') and task.completed_date between ? AND ?) or\n" +
            "        (t.status in ('AWAITING_APPROVAL') and task.initiated_date between ? AND ?)\n" +
            "    )\n" +
            "  and u.urid = ?\n" +
            "  and aa.state = 'ACTIVE'\n" +
            "group by a.type_label\n" +
            "order by balance desc";


    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TransactionsBalanceReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {

        List<TransactionsBalanceReportData> groupARows =
            getRowsByGroup(reportQueryInfo, REPORT_QUERY_GROUP_A_ACQUIRING_SUM, REPORT_QUERY_GROUP_A_TRANSFERRING_SUM,
                GROUP_A_LABEL);

        List<TransactionsBalanceReportData> groupBRows =
            getRowsByGroup(reportQueryInfo, REPORT_QUERY_GROUP_B_ACQUIRING_SUM, REPORT_QUERY_GROUP_B_TRANSFERRING_SUM,
                GROUP_B_LABEL);


        TransactionsBalanceReportData firstGroupingRow = TransactionsBalanceReportData.builder()
            .accountType(GROUP_A_LABEL)
            .numberOfUnits(null)
            .build();

        TransactionsBalanceReportData firstSubTotalRow = TransactionsBalanceReportData.builder()
            .accountType("Total")
            .numberOfUnits(groupARows.stream().mapToLong(TransactionsBalanceReportData::getNumberOfUnits).sum())
            .build();

        TransactionsBalanceReportData secondGroupingRow = TransactionsBalanceReportData.builder()
            .accountType(GROUP_B_LABEL)
            .numberOfUnits(null)
            .build();

        TransactionsBalanceReportData secondSubTotalRow = TransactionsBalanceReportData.builder()
            .accountType("Total")
            .numberOfUnits(groupBRows.stream().mapToLong(TransactionsBalanceReportData::getNumberOfUnits).sum())
            .build();

        TransactionsBalanceReportData grandTotalRow = TransactionsBalanceReportData.builder()
            .accountType("Grand Total (A+B)\n")
            .numberOfUnits(firstSubTotalRow.getNumberOfUnits() + secondSubTotalRow.getNumberOfUnits())
            .build();

        return Stream.of(
                // The "A" row
                List.of(firstGroupingRow),
                // The ETS accounts rows
                groupARows,
                // The "Total" row for ETS accounts
                List.of(firstSubTotalRow),
                // The "B" row of the report
                List.of(secondGroupingRow),
                // The central accounts rows
                groupBRows,
                // THe "Total" row for central accounts
                List.of(secondSubTotalRow),
                // the "Grand Total (A+B)" row
                List.of(grandTotalRow)
            )
            .flatMap(Collection::stream)
            .collect(toList());
    }

    @Override
    public TransactionsBalanceReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
            TransactionsBalanceReportData.builder()
                .groupName(resultSet.getString(1))
                .accountType(resultSet.getString(2))
                .numberOfUnits(resultSet.getObject(3) != null ? resultSet.getLong(3) : null)
                .build();
    }

    /**
     * Retrieves the acquiring and transferring sums separately for a specific group
     * and for specific date range (if provided).
     *
     * <p>
     * Then merges the two lists int one by subtracting for each account type
     * the transferring sum from the acquiring sum.
     */
    private List<TransactionsBalanceReportData> getRowsByGroup(ReportQueryInfoWithMetadata reportQueryInfo,
                                                               String acquiringSumQuery, String transferringSumQuery,
                                                               String groupName) {

        Date from = DateRangeUtil.getFrom(reportQueryInfo);
        Date to = DateRangeUtil.getTo(reportQueryInfo);

        List<TransactionsBalanceReportData> acquiringSumRows =
            jdbcTemplate.query(acquiringSumQuery, this, from, to, reportQueryInfo.getUrid());

        List<TransactionsBalanceReportData> transferringSumRows =
            jdbcTemplate.query(transferringSumQuery, this, from, to, from, to, from, to, reportQueryInfo.getUrid());

        return acquiringSumRows.stream()
            .map(row -> TransactionsBalanceReportData.builder()
                .groupName(groupName)
                .numberOfUnits(row.getNumberOfUnits() - getUnitsByType(transferringSumRows, row.getAccountType()))
                .accountType(row.getAccountType())
                .build())
            .collect(toList());
    }

    private Long getUnitsByType(List<TransactionsBalanceReportData> rows, String accountType) {
        return rows.stream()
            .filter(r -> r.getAccountType().equals(accountType))
            .map(TransactionsBalanceReportData::getNumberOfUnits)
            .findFirst()
            .orElse(0L);
    }

}
