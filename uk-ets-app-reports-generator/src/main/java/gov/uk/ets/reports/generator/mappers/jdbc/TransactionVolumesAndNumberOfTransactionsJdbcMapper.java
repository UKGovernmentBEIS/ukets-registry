package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.TransactionVolumesAndNumberOfTransactionsReportData;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionVolumesAndNumberOfTransactionsJdbcMapper
    implements ReportDataMapper<TransactionVolumesAndNumberOfTransactionsReportData>, RowMapper<TransactionVolumesAndNumberOfTransactionsReportData> {

    /**
     *  Calculates the number of transactions and the sum of units based on specific groupings between
     *  transferring account type and acquiring account type
     */
    private static final String REPORT_QUERY =
    "select sum(coalesce(transact.quantity, 0))                                                as number_of_units,\n" +
        "             case\n" +
        "                 when tra.type_label = 'ETS - Trading account' and aqa.type_label = 'ETS - Trading account'\n" +
        "                     then 'Trading account to Trading Account'\n" +
        "                 when tra.type_label = 'ETS - Trading account' and aqa.type_label = 'ETS - Operator holding account'\n" +
        "                     then 'Trading account to Operator Holding Account (OHA)'\n" +
        "                 when tra.type_label = 'ETS - Trading account' and aqa.type_label in ('ETS - UK Total Quantity Account',\n" +
        "                                                                                      'ETS - UK Deletion Account',\n" +
        "                                                                                      'ETS - UK Auction Account',\n" +
        "                                                                                      'ETS - UK Allocation Account',\n" +
        "                                                                                      'ETS - UK Surrender Account',\n" +
        "                                                                                      'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                                      'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                                      'ETS - UK General Holding Account')\n" +
        "                     then 'Trading account to Central Account'\n" +
        "                 when tra.type_label = 'ETS - UK Auction delivery account' and aqa.type_label = 'ETS - Trading account'\n" +
        "                     then 'Auction Delivery Account to Trader Account'\n" +
        "                 when tra.type_label = 'ETS - UK Auction delivery account' and\n" +
        "                      aqa.type_label = 'ETS - Operator holding account' then 'Auction Delivery Account to OHA'\n" +
        "                 when tra.type_label = 'ETS - UK Auction delivery account' and aqa.type_label in\n" +
        "                                                                               ('ETS - UK Total Quantity Account',\n" +
        "                                                                                'ETS - UK Deletion Account',\n" +
        "                                                                                'ETS - UK Auction Account',\n" +
        "                                                                                'ETS - UK Allocation Account',\n" +
        "                                                                                'ETS - UK Surrender Account',\n" +
        "                                                                                'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                                'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                                'ETS - UK General Holding Account')\n" +
        "                     then 'Auction Delivery Account to Central Account'\n" +
        "                 when tra.type_label = 'ETS - Operator holding account' and aqa.type_label = 'ETS - Trading account'\n" +
        "                     then 'Operator Holding Account (OHA) to Trading Account'\n" +
        "                 when tra.type_label = 'ETS - Operator holding account' and\n" +
        "                      aqa.type_label = 'ETS - Operator holding account'\n" +
        "                     then 'Operator Holding Account (OHA) to Operator Holding Account (OHA)'\n" +
        "                 when tra.type_label = 'ETS - Operator holding account' and aqa.type_label in\n" +
        "                                                                            ('ETS - UK Total Quantity Account',\n" +
        "                                                                             'ETS - UK Deletion Account',\n" +
        "                                                                             'ETS - UK Auction Account',\n" +
        "                                                                             'ETS - UK Allocation Account',\n" +
        "                                                                             'ETS - UK Surrender Account',\n" +
        "                                                                             'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                             'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                             'ETS - UK General Holding Account')\n" +
        "                     then 'Operator Holding Account (OHA) to Central Account'\n" +
        "                 when tra.type_label = 'ETS - Aircraft operator holding account' and\n" +
        "                      aqa.type_label = 'ETS - Trading account'\n" +
        "                     then 'Aircraft Operator Holding Account (AOHA) to Trading Account'\n" +
        "                 when tra.type_label = 'ETS - Aircraft operator holding account' and aqa.type_label in\n" +
        "                                                                                     ('ETS - UK Total Quantity Account',\n" +
        "                                                                                      'ETS - UK Deletion Account',\n" +
        "                                                                                      'ETS - UK Auction Account',\n" +
        "                                                                                      'ETS - UK Allocation Account',\n" +
        "                                                                                      'ETS - UK Surrender Account',\n" +
        "                                                                                      'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                                      'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                                      'ETS - UK General Holding Account')\n" +
        "                     then 'Aircraft Operator Holding Account (AOHA) to Central Account'\n" +
        "                 when tra.type_label = 'ETS - Aircraft operator holding account' and\n" +
        "                      aqa.type_label = 'ETS - Aircraft operator holding account'\n" +
        "                     then 'Aircraft Operator Holding Account (AOHA) to Aircraft Operator Holding Account to (AOHA)'\n" +
        "                 when tra.type_label = 'ETS - UK Auction delivery account' and\n" +
        "                      aqa.type_label = 'ETS - Aircraft operator holding account'\n" +
        "                     then 'Auction Delivery Account to Aircraft Operator Holding Account (AOHA)'\n" +
        "                 when tra.type_label = 'ETS - Aircraft operator holding account' and\n" +
        "                      aqa.type_label = 'ETS - Operator holding account'\n" +
        "                     then 'Aircraft Operator Holding Account (AOHA) to Operator Holding Account to (OHA)'\n" +
        "                 when tra.type_label = 'ETS - Operator holding account' and\n" +
        "                      aqa.type_label = 'ETS - Aircraft operator holding account'\n" +
        "                     then 'Operator Holding Account (OHA) to Aircraft Operator Holding Account (AOHA)'\n" +
        "                 when tra.type_label = 'ETS - Trading account' and\n" +
        "                      aqa.type_label = 'ETS - Aircraft operator holding account'\n" +
        "                     then 'Trading account to Aircraft Operator Holding Account (AOHA)' end as transaction_type,\n" +
        "       " +
        "count(transact.id)                                                                    as number_of_transactions\n" +
        "      from \"transaction\" transact\n" +
        "               left join account aqa on aqa.identifier = transact.acquiring_account_identifier\n" +
        "               left join account tra on tra.identifier = transact.transferring_account_identifier\n" +
        "      where transact.status = 'COMPLETED'\n" +
        "        and transact.acquiring_account_registry_code = 'UK'\n" +
        "        and transact.transferring_account_registry_code = 'UK'\n" +
        "        and transact.execution_date >= ? and transact.execution_date <= ?\n" +
        "        and ((tra.type_label = 'ETS - Trading account' and aqa.type_label = 'ETS - Trading account') or\n" +
        "             (tra.type_label = 'ETS - Trading account' and aqa.type_label = 'ETS - Operator holding account') or\n" +
        "             (tra.type_label = 'ETS - Trading account' and aqa.type_label in ('ETS - UK Total Quantity Account',\n" +
        "                                                                              'ETS - UK Deletion Account',\n" +        
        "                                                                              'ETS - UK Auction Account',\n" +
        "                                                                              'ETS - UK Allocation Account',\n" +
        "                                                                              'ETS - UK Surrender Account',\n" +
        "                                                                              'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                              'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                              'ETS - UK General Holding Account')) or\n" +
        "             (tra.type_label = 'ETS - UK Auction delivery account' and aqa.type_label = 'ETS - Trading account') or\n" +
        "             (tra.type_label = 'ETS - UK Auction delivery account' and\n" +
        "              aqa.type_label = 'ETS - Operator holding account') or\n" +
        "             (tra.type_label = 'ETS - UK Auction delivery account' and aqa.type_label in\n" +
        "                                                                       ('ETS - UK Total Quantity Account',\n" +
        "                                                                        'ETS - UK Deletion Account',\n" +
        "                                                                        'ETS - UK Auction Account',\n" +
        "                                                                        'ETS - UK Allocation Account',\n" +
        "                                                                        'ETS - UK Surrender Account',\n" +
        "                                                                        'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                        'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                        'ETS - UK General Holding Account')) or\n" +
        "             (tra.type_label = 'ETS - Operator holding account' and aqa.type_label = 'ETS - Trading account') or\n" +
        "             (tra.type_label = 'ETS - Operator holding account' and aqa.type_label in\n" +
        "                                                                    ('ETS - UK Total Quantity Account',\n" +
        "                                                                     'ETS - UK Deletion Account',\n" +
        "                                                                     'ETS - UK Auction Account',\n" +
        "                                                                     'ETS - UK Allocation Account',\n" +
        "                                                                     'ETS - UK Surrender Account',\n" +
        "                                                                     'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                     'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                     'ETS - UK General Holding Account')) or\n" +
        "             (tra.type_label = 'ETS - Operator holding account' and\n" +
        "              aqa.type_label = 'ETS - Operator holding account') or\n" +
        "             (tra.type_label = 'ETS - Aircraft operator holding account' and\n" +
        "              aqa.type_label = 'ETS - Trading account') or\n" +
        "             (tra.type_label = 'ETS - Aircraft operator holding account' and aqa.type_label in\n" +
        "                                                                             ('ETS - UK Total Quantity Account',\n" +
        "                                                                              'ETS - UK Deletion Account',\n" +
        "                                                                              'ETS - UK Auction Account',\n" +
        "                                                                              'ETS - UK Allocation Account',\n" +
        "                                                                              'ETS - UK Surrender Account',\n" +
        "                                                                              'ETS - UK New Entrants Reserve Account',\n" +
        "                                                                              'ETS - UK Market Stability Mechanism Account',\n" +
        "                                                                              'ETS - UK General Holding Account')) or\n" +
        "             (tra.type_label = 'ETS - Aircraft operator holding account' and\n" +
        "              aqa.type_label = 'ETS - Aircraft operator holding account') or\n" +
        "             (tra.type_label = 'ETS - UK Auction delivery account' and\n" +
        "              aqa.type_label = 'ETS - Aircraft operator holding account') or\n" +
        "             (tra.type_label = 'ETS - Aircraft operator holding account' and\n" +
        "              aqa.type_label = 'ETS - Operator holding account') or\n" +
        "             (tra.type_label = 'ETS - Operator holding account' and\n" +
        "              aqa.type_label = 'ETS - Aircraft operator holding account') or\n" +
        "             (tra.type_label = 'ETS - Trading account' and\n" +
        "              aqa.type_label = 'ETS - Aircraft operator holding account'))\n" +
        "group by transaction_type\n" +
        "order by number_of_units desc";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TransactionVolumesAndNumberOfTransactionsReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<TransactionVolumesAndNumberOfTransactionsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this, DateRangeUtil.getFrom(reportQueryInfo), DateRangeUtil.getTo(reportQueryInfo));
    }

    @Override
    public TransactionVolumesAndNumberOfTransactionsReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
            TransactionVolumesAndNumberOfTransactionsReportData.builder()
                    .numberOfUnits(resultSet.getLong(1))
                    .transactionType(resultSet.getString(2))
                    .numberOfTransactions(resultSet.getLong(3))
                .build();
    }
}
