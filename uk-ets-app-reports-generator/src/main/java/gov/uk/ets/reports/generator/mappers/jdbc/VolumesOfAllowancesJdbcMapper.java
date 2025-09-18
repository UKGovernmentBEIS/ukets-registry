package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.VolumeOfAllowancesReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
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
public class VolumesOfAllowancesJdbcMapper
    implements ReportDataMapper<VolumeOfAllowancesReportData>, RowMapper<VolumeOfAllowancesReportData> {

    /**
     * Get, per Central Accounts, Auction Delivery Account, Operator Holding Account, Aircraft Operator Holding Account,
     * Trading Account (1 of each 5 account type categories), the number of units
     */
    private static final String REPORT_QUERY =
        "select\n" +
            "    a.type_label as reg_account_type,\n" +
            "    sum(coalesce(ub.end_block - ub.start_block + 1, 0)) as number_of_units\n" +
            "from\n" +
            "    (\n" +
            "        select\n" +
            "            case\n" +
            "                when acc.type_label = 'ETS - UK Auction delivery account' then 'Auction Delivery Account'\n" +
            "                when acc.type_label = 'ETS - Operator holding account' then 'Operator Holding Accounts'\n" +
            "                when acc.type_label = 'ETS - Aircraft operator holding account' then 'Aircraft Operator Holding Accounts'\n" +
            "                when acc.type_label = 'ETS - Maritime operator holding account' then 'Maritime Operator Holding Accounts'\n" +
            "                when acc.type_label = 'ETS - Trading account' then 'Trading Accounts'\n" +
            "                when acc.type_label in (\n" +
            "                                        'ETS - UK Total Quantity Account',\n" +
            "                                        'ETS - UK Auction Account',\n" +
            "                                        'ETS - UK Allocation Account',\n" +
            "                                        'ETS - UK Surrender Account',\n" +
            "                                        'ETS - UK New Entrants Reserve Account',\n" +
            "                                        'ETS - UK Market Stability Mechanism Account',\n" +
            "                                        'ETS - UK General Holding Account') then 'Central Accounts'\n" +
            "                end as type_label,\n" +
            "            coalesce(acc.balance, 0) as balance,\n" +
            "            acc.identifier as identifier\n" +
            "        from\n" +
            "            account acc\n" +
            "        where\n" +
            "            (acc.registry_code = 'UK'\n" +
            "                or acc.full_identifier like 'UK%')\n" +
            "          and acc.type_label in (\n" +
            "                                 'ETS - UK Auction delivery account',\n" +
            "                                 'ETS - Operator holding account',\n" +
            "                                 'ETS - Aircraft operator holding account',\n" +
            "                                 'ETS - Maritime operator holding account',\n" +
            "                                 'ETS - Trading account',\n" +
            "                                 'ETS - UK Total Quantity Account',\n" +
            "                                 'ETS - UK Auction Account',\n" +
            "                                 'ETS - UK Allocation Account',\n" +
            "                                 'ETS - UK Surrender Account',\n" +
            "                                 'ETS - UK New Entrants Reserve Account',\n" +
            "                                 'ETS - UK Market Stability Mechanism Account',\n" +
            "                                 'ETS - UK General Holding Account' ) ) a\n" +
            "        " +
            "left join unit_block ub on\n" +
            "                a.identifier = ub.account_identifier\n" +
            "group by\n" +
            "    a.type_label\n" +
            "order by\n" +
            "    number_of_units desc";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<VolumeOfAllowancesReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public VolumeOfAllowancesReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
            VolumeOfAllowancesReportData.builder()
                    .accountType(resultSet.getString(1))
                    .numberOfUnits(resultSet.getLong(2))
                    .build();
    }
}
