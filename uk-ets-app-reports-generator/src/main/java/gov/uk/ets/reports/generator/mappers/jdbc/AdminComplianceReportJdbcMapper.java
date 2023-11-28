package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.AdminComplianceReportData;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminComplianceReportJdbcMapper
    implements ReportDataMapper<AdminComplianceReportData>, RowMapper<AdminComplianceReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSTALLATION_NAME_COLUMN =
        "case\n" +
            "   when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' " +
            "   then (select installation_name from installation i where i.compliant_entity_id = ce.id)\n" +
            "   else ''\n" +
            "end";

    private static final String PERMIT_OR_MONITORING_PLAN_ID_COLUMN =
        "case\n" +
            "   when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then (select permit_identifier from installation i where i.compliant_entity_id = ce.id)\n" +
            "   else (select monitoring_plan_identifier from aircraft_operator ao where ao.compliant_entity_id = ce.id)\n" +
            "end";

    private static final String MOST_RECENT_EMISSIONS_ROWS =
        "select *\n" +
        "      from emissions_entry\n" +
        "      where (compliant_entity_id, upload_date) in\n" +
        "            (select compliant_entity_id, max(upload_date)\n" +
        "             from emissions_entry " +
        "             where upload_date <= ? " +
        "             group by compliant_entity_id, year)" +
        "       and upload_date <= ? ";

    private static final String SURRENDER_ALLOWANCES_TX_PER_YEAR_ROWS =
        "select years.year, aa.id, coalesce(sum(t.quantity), 0) as surrenders\n" +
        "          from transaction t\n" +
        "                   inner join account aa on t.transferring_account_identifier = aa.identifier\n" +
        "                   left join installation_ownership io2 on io2.account_id = aa.id\n" +
        "                   left join installation ins on io2.installation_id = ins.compliant_entity_id\n" +
        "                   cross join years\n" +
        "          where t.type = 'SurrenderAllowances'\n" +
        "            and t.status = 'COMPLETED'\n" +
        "            and execution_date >= TO_TIMESTAMP(concat(years.year - 1, '-05-01'), 'YYYY-MM-DD')\n" +
        "            and execution_date < TO_TIMESTAMP(concat(years.year, '-05-01'), 'YYYY-MM-DD')\n" +
        "            and execution_date <= ? " +
        "          group by io2.installation_id, aa.id, years.year";

    private static final String REVERSE_ALLOWANCES_TX_PER_YEAR_ROWS =
        "select years.year, aa.id, coalesce(sum(t.quantity), 0) as reversals\n" +
        "          from transaction t\n" +
        "                   inner join account aa on t.acquiring_account_identifier = aa.identifier\n" +
        "                   left join installation_ownership io2 on io2.account_id = aa.id\n" +
        "                   left join installation ins on io2.installation_id = ins.compliant_entity_id\n" +
        "                   cross join years\n" +
        "          where t.type = 'ReverseSurrenderAllowances'\n" +
        "            and t.status = 'COMPLETED'\n" +
        "            and execution_date >= TO_TIMESTAMP(concat(years.year - 1, '-05-01'), 'YYYY-MM-DD')\n" +
        "            and execution_date < TO_TIMESTAMP(concat(years.year, '-05-01'), 'YYYY-MM-DD')\n" +
        "            and execution_date <= ? \n" +
        "          group by io2.installation_id, aa.id, years.year";

    private static final String OLD_INSTALLATION_ACCOUNTS_UNION_AOHA =
            "select io.account_id " +
                    "       from installation_ownership io\n" +
                    "       join installation ins on ins.compliant_entity_id = io.installation_id\n" +
                    "       join compliant_entity ce on ce.id = io.installation_id\n" +
                    "       where ce.identifier = account_data_per_year.identifier" +
                    "  union\n" +
                    "       select acc.id from aircraft_operator ao\n" +
                    "       join compliant_entity ce on ce.id = ao.compliant_entity_id\n" +
                    "       join account acc on acc.compliant_entity_id = ce.id\n" +
                    "       where ce.identifier = account_data_per_year.identifier";

    /**
     * <p> The query returns information on compliant accounts for the years 2021 to 2030.</p>
     *
     * <p> Table t contains the report data per account and per year.</p>
     *
     |cuttof_year|year|account_id|account_identifier|account_name |           account_type          |account_status|start_year|end_year|dynamic_compliance_status|compliance_status|verified_emissions|verified_emissions_str|surrendered_emissions |
     |------------|----|----------|------------------|-------------|---------------------------------|--------------|----------|--------|-------------------------|-----------------|------------------|----------------------|----------------------|
     |    2021    |2021|   39     |     10000051     |British Wings|AIRCRAFT_OPERATOR_HOLDING_ACCOUNT|      OPEN    |   2021   |  2021  |      NOT_APPLICABLE     |  NOT_APPLICABLE |        2         |        2             |        0             |
     |    2021    |2022|   39     |     10000051     |British Wings|AIRCRAFT_OPERATOR_HOLDING_ACCOUNT|      OPEN    |   2021   |  2021  |      NOT_APPLICABLE     |  NOT_APPLICABLE |        0         |                      |                      |
     |    2021    |2023|   39     |     10000051     |British Wings|AIRCRAFT_OPERATOR_HOLDING_ACCOUNT|      OPEN    |   2021   |  2021  |      NOT_APPLICABLE     |  NOT_APPLICABLE |        0         |                      |                      |
     |    2021    |2024|   39     |     10000051     |British Wings|AIRCRAFT_OPERATOR_HOLDING_ACCOUNT|      OPEN    |   2021   |  2021  |      NOT_APPLICABLE     |  NOT_APPLICABLE |        0         |                      |                      |
     |    2021    |2025|   39     |     10000051     |British Wings|AIRCRAFT_OPERATOR_HOLDING_ACCOUNT|      OPEN    |   2021   |  2021  |      NOT_APPLICABLE     |  NOT_APPLICABLE |        0         |                      |                      |
     *
     * <p>Verified emissions are calculated by joining with the most recent entry for each compliant entity. If the account is excluded for a year, then instead of emissions the value
     * ‘EXCLUDED’ is displayed. Surrendered emissions are calculated by subtracting reversals from surrenders.</p>
     *
     * <p>We pivot table t on the year to create a separate column for each year for values verified_emissions and static_compliance_status.</p>
     * <p>We used CASE expressions to build the pivot table, creating a new column with the aggregation of rows in each case (in practice one row in each case).</p>
     *
     * <p>Columns verified_emissions_YYYY and static_surrender_status_YYYY are displayed up to the cut off year, that is why we compare with the cuttof_year.</p>
     *
     */
    private static final String REPORT_QUERY = "select regulator,\n" +
        "       ah_name,\n" +
        "       account_identifier,\n" +
        "       account_name,\n" +
        "       account_type,\n" +
        "       account_status,\n" +
        "       installation_name,\n" +
        "       identifier,\n" +
        "       permit_or_monitoring_plan_id,\n" +
        "       start_year,\n" +
        "       end_year,\n" +
        "       dynamic_compliance_status,\n" +
        "       case when cuttof_year>=2021 then string_agg((case when year = 2021 then verified_emissions_str end), '') end as verified_emissions_2021,\n" +
        "       case when cuttof_year>=2022 then string_agg((case when year = 2022 then verified_emissions_str end), '') end as verified_emissions_2022,\n" +
        "       case when cuttof_year>=2023 then string_agg((case when year = 2023 then verified_emissions_str end), '') end as verified_emissions_2023,\n" +
        "       case when cuttof_year>=2024 then string_agg((case when year = 2024 then verified_emissions_str end), '') end as verified_emissions_2024,\n" +
        "       case when cuttof_year>=2025 then string_agg((case when year = 2025 then verified_emissions_str end), '') end as verified_emissions_2025,\n" +
        "       case when cuttof_year>=2026 then string_agg((case when year = 2026 then verified_emissions_str end), '') end as verified_emissions_2026,\n" +
        "       case when cuttof_year>=2027 then string_agg((case when year = 2027 then verified_emissions_str end), '') end as verified_emissions_2027,\n" +
        "       case when cuttof_year>=2028 then string_agg((case when year = 2028 then verified_emissions_str end), '') end as verified_emissions_2028,\n" +
        "       case when cuttof_year>=2029 then string_agg((case when year = 2029 then verified_emissions_str end), '') end as verified_emissions_2029,\n" +
        "       case when cuttof_year>=2030 then string_agg((case when year = 2030 then verified_emissions_str end), '') end as verified_emissions_2030,\n" +
        "       sum(coalesce((CASE when year in (select generate_series(2021, cuttof_year)) then verified_emissions end), 0)) as cum_verified_emissions,\n" +
        "       sum(coalesce((CASE when year in (select generate_series(2021, cuttof_year)) then surrendered_emissions end), 0)) as cum_surrendered_emissions,\n" +
        "       case when cuttof_year>=2021 then string_agg((case when year = 2021 then compliance_status end), '') end as static_surrender_status_2021,\n" +
        "       case when cuttof_year>=2022 then string_agg((case when year = 2022 then compliance_status end), '') end as static_surrender_status_2022,\n" +
        "       case when cuttof_year>=2023 then string_agg((case when year = 2023 then compliance_status end), '') end as static_surrender_status_2023,\n" +
        "       case when cuttof_year>=2024 then string_agg((case when year = 2024 then compliance_status end), '') end as static_surrender_status_2024,\n" +
        "       case when cuttof_year>=2025 then string_agg((case when year = 2025 then compliance_status end), '') end as static_surrender_status_2025,\n" +
        "       case when cuttof_year>=2026 then string_agg((case when year = 2026 then compliance_status end), '') end as static_surrender_status_2026,\n" +
        "       case when cuttof_year>=2027 then string_agg((case when year = 2027 then compliance_status end), '') end as static_surrender_status_2027,\n" +
        "       case when cuttof_year>=2028 then string_agg((case when year = 2028 then compliance_status end), '') end as static_surrender_status_2028,\n" +
        "       case when cuttof_year>=2029 then string_agg((case when year = 2029 then compliance_status end), '') end as static_surrender_status_2029,\n" +
        "       case when cuttof_year>=2030 then string_agg((case when year = 2030 then compliance_status end), '') end as static_surrender_status_2030\n" +
        "from (\n" +
        "   with years as (select id, year from allocation_year order by year asc)\n" +
        "   select (select extract(year from ?)::int) as cuttof_year,\n" +
        "       account_data_per_year.*,\n" +
        "       coalesce(surrenders, 0) - coalesce(reversals, 0) as surrendered_emissions\n" +
        "   from (\n" +
        "         select years.year              as year,\n" +
        "                ce.regulator,\n" +
        "                ah.name                 as ah_name,\n" +
        "                a.id                    as account_id,\n" +
        "                a.identifier            as account_identifier,\n" +
        "                a.account_name,\n" +
        "                a.registry_account_type as account_type,\n" +
        "                a.account_status,\n" +
        "                ce.identifier,\n" +
        "                " + INSTALLATION_NAME_COLUMN + "            as installation_name,\n" +
        "                " + PERMIT_OR_MONITORING_PLAN_ID_COLUMN + " as permit_or_monitoring_plan_id,\n" +
        "                ce.start_year,\n" +
        "                ce.end_year,\n" +
        "                a.compliance_status     as dynamic_compliance_status,\n" +
        "                scs.compliance_status   as compliance_status,\n" +
        "                case\n" +
        "                    when eee.excluded is true then null\n" +
        "                    else sum(ee.emissions)\n" +
        "                    end as verified_emissions,\n" +
        "                case\n" +
        "                    when eee.excluded is true then 'EXCLUDED'\n" +
        "                    else cast(sum(ee.emissions) as varchar)\n" +
        "                    end as verified_emissions_str\n" +
        "         from account a\n" +
        "                  cross join years\n" +
        "                  inner join compliant_entity ce on ce.id = a.compliant_entity_id\n" +
        "                  left join account_holder ah on ah.id = a.account_holder_id\n" +
        "                  left join static_compliance_status scs on scs.compliant_entity_id = ce.id and scs.year = years.year and scs.year <= EXTRACT(YEAR FROM ?) \n" +
        "                  left join exclude_emissions_entry eee on eee.compliant_entity_id = ce.identifier and eee.year = years.year and eee.last_updated <= ? \n" +
        "                  left join ( "+MOST_RECENT_EMISSIONS_ROWS+" ) ee on ee.compliant_entity_id = ce.identifier and ee.year = years.year\n" +
        "         where a.account_status <> 'REJECTED'\n" +
        "         group by years.year, a.id, ce.id, ah.id, scs.id, eee.id, years.id\n" +
        "     ) account_data_per_year\n" +
        "         left join ( "+SURRENDER_ALLOWANCES_TX_PER_YEAR_ROWS+" ) tx_surrender on tx_surrender.year = account_data_per_year.year \n" +
        "         and tx_surrender.id in (" + OLD_INSTALLATION_ACCOUNTS_UNION_AOHA + ")\n" +
        "         left join ( "+REVERSE_ALLOWANCES_TX_PER_YEAR_ROWS+" ) tx_reversal on tx_reversal.year = account_data_per_year.year\n" +
        "         and tx_reversal.id in (" + OLD_INSTALLATION_ACCOUNTS_UNION_AOHA + ")\n" +
        "     ) t1\n" +
        "group by regulator,\n" +
        "         ah_name, \n" +
        "         account_name, \n" +
        "         account_identifier,\n" +
        "         account_name,\n" +
        "         account_type,\n" +
        "         account_status,\n" +
        "         installation_name, \n" +
        "         identifier, \n" +
        "         permit_or_monitoring_plan_id, \n" +
        "         start_year,\n" +
        "         end_year,\n" +
        "         dynamic_compliance_status,\n" +
        "         cuttof_year \n" +
        "order by ah_name, permit_or_monitoring_plan_id desc";

    @Override
    public List<AdminComplianceReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<AdminComplianceReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        LocalDateTime cutOffDateTime = DateRangeUtil.getCutOffDateTime(reportQueryInfo);
        return jdbcTemplate.query(REPORT_QUERY, this, cutOffDateTime, cutOffDateTime,
                cutOffDateTime, cutOffDateTime, cutOffDateTime, cutOffDateTime, cutOffDateTime);
    }

    @Override
    public AdminComplianceReportData mapRow(ResultSet rs, int i) throws SQLException {
        return AdminComplianceReportData.builder()
            .regulator(rs.getString("regulator"))
            .accountHolderName(rs.getString("ah_name"))
            .accountNumber(rs.getLong("account_identifier"))
            .accountName(rs.getString("account_name"))
            .accountStatus(rs.getString("account_status"))
            .accountType(rs.getString("account_type"))
            .installationName(rs.getString("installation_name"))
            .installationIdentifier(rs.getLong("identifier"))
            .permitOrMonitoringPlanId(rs.getString("permit_or_monitoring_plan_id"))
            .firstYearOfVerifiedEmissions(rs.getString("start_year"))
            .lastYearOfVerifiedEmissions(rs.getString("end_year"))
            .dynamicComplianceStatus(rs.getString("dynamic_compliance_status"))
            .verifiedEmissions2021(rs.getString("verified_emissions_2021"))
            .verifiedEmissions2022(rs.getString("verified_emissions_2022"))
            .verifiedEmissions2023(rs.getString("verified_emissions_2023"))
            .verifiedEmissions2024(rs.getString("verified_emissions_2024"))
            .verifiedEmissions2025(rs.getString("verified_emissions_2025"))
            .verifiedEmissions2026(rs.getString("verified_emissions_2026"))
            .verifiedEmissions2027(rs.getString("verified_emissions_2027"))
            .verifiedEmissions2028(rs.getString("verified_emissions_2028"))
            .verifiedEmissions2029(rs.getString("verified_emissions_2029"))
            .verifiedEmissions2030(rs.getString("verified_emissions_2030"))
            .cumulativeEmissions(rs.getLong("cum_verified_emissions"))
            .cumulativeSurrenders(rs.getLong("cum_surrendered_emissions"))
            .staticSurrenderStatus2021(rs.getString("static_surrender_status_2021"))
            .staticSurrenderStatus2022(rs.getString("static_surrender_status_2022"))
            .staticSurrenderStatus2023(rs.getString("static_surrender_status_2023"))
            .staticSurrenderStatus2024(rs.getString("static_surrender_status_2024"))
            .staticSurrenderStatus2025(rs.getString("static_surrender_status_2025"))
            .staticSurrenderStatus2026(rs.getString("static_surrender_status_2026"))
            .staticSurrenderStatus2027(rs.getString("static_surrender_status_2027"))
            .staticSurrenderStatus2028(rs.getString("static_surrender_status_2028"))
            .staticSurrenderStatus2029(rs.getString("static_surrender_status_2029"))
            .staticSurrenderStatus2030(rs.getString("static_surrender_status_2030"))
            .build();
    }
}
