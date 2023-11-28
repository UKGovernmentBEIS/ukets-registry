package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.Compliance10YearsReportData;
import gov.uk.ets.reports.generator.export.util.ComplianceReportUtil;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class Compliance10YearsReportJdbcMapper
    implements ReportDataMapper<Compliance10YearsReportData>, RowMapper<Compliance10YearsReportData> {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Reference year is the year for which compliance data (emissions, surrender,
     * cumulative emissions or surrenders, cumulative surrenders - cumulative emissions)
     * will be included in the report.
     **/
    private int referenceYear;
    private static final int[] years = IntStream.rangeClosed(2021, 2030).toArray();
    private static final int CURRENT_YEAR = LocalDateTime.now().getYear();

    private static final String INSTALLATION_NAME_COLUMN =
        "case\n" +
            "   when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' " +
            "   then (select installation_name from installation i where i.compliant_entity_id = ce.id)\n" +
            "   else ''\n" +
            "end";

    private static final String ACTIVITY_TYPE_COLUMN =
        "case\n" +
            "   when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then (select activity_type from installation i where i.compliant_entity_id = ce.id)\n" +
            "   else ''\n" +
            "end";

    private static final String PERMIT_OR_MONITORING_PLAN_ID_COLUMN =
        "case\n" +
            "   when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then (select permit_identifier from installation i where i.compliant_entity_id = ce.id)\n" +
            "   else (select monitoring_plan_identifier from aircraft_operator ao where ao.compliant_entity_id = ce.id)\n" +
            "end";

    private static final String VERIFIED_EMISSIONS_COLUMN =
        "case\n" +
            "   when eee.excluded is true then null\n" +
            "   else sum(ee.emissions)\n" +
            "end";

    private static final String MOST_RECENT_EMISSIONS_ROWS =
        "select *\n" +
            "from emissions_entry\n" +
            "where (compliant_entity_id, upload_date) in\n" +
            "   (select compliant_entity_id, max(upload_date)\n" +
            "   from emissions_entry\n" +
            "    where upload_date <= ? " +
            "   group by compliant_entity_id, year)" +
            "       and upload_date <= ? ";

    /**
     * <p> The query returns information for compliance accounts for years between 2021 and 2030.</p>
     * <p> The table account_data_per_year contains the report data per account and per year. Allocations and verified
     * emissions data are obtained by grouping the rows and summing the columns in each group.</p>
     * | year | regulator  |              ah_name          |    account_name   |            account_type           | installation_name  | identifier |    activity_type      | permit_or_monitoring_plan_id | start_year | last_year  |   compliance_status | allocations | verified_emissions |
     * |------|------------|-------------------------------|-------------------|-----------------------------------|--------------------|------------|-----------------------|------------------------------|------------|------------|---------------------|-------------|--------------------|
     * | 2021 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2022 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2023 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2024 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2025 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2026 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2027 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2028 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2029 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     * | 2030 |    SEPA    | Compliance Check Organisation |     5255_Closed   |     OPERATOR_HOLDING_ACCOUNT      |    Installation2   |   1000193  | PRODUCTION_OF_AMMONIA |       123468778              |    2021    |            |                     |      0      |       0            |
     *
     *
     * <p> We join the table above with tables tx_surrender and tx_reversal. tx_surrender table contains the completed
     * surrender transactions executed between 01/05/X-1 00:01 and 30/4/X 23.59 and tx_reversal table contains the
     * completed reversal transactions executed between 01/05/X-1 00:01 and 30/4/X 23.59. </p>
     *
     * <p> Column surrendered_emissions has been added to the above example table and the result is the t1 table. Now
     * we need to pivot t1 on the year to create a separate column for each year. We used CASE expressions to build the
     * pivot table, creating a new column with the sum of rows in each case. </p>
     *
     * <p>To calculate the verified emissions we did not join with emissions_entry but with the most recent entry for
     * each entity.</p>
     */
    private static final String REPORT_QUERY =
        "select regulator,\n" +
            "       ah_name,\n" +
            "       account_name,\n" +
            "       account_type,\n" +
            "       installation_name,\n" +
            "       identifier,\n" +
            "       activity_type,\n" +
            "       permit_or_monitoring_plan_id,\n" +
            "       start_year,\n" +
            "       end_year,\n" +
            "       excluded_2021,\n" +
            "       excluded_2022,\n" +
            "       excluded_2023,\n" +
            "       excluded_2024,\n" +
            "       excluded_2025,\n" +
            "       excluded_2026,\n" +
            "       excluded_2027,\n" +
            "       excluded_2028,\n" +
            "       excluded_2029,\n" +
            "       excluded_2030,\n" +
            "       verified_emissions_2021,\n" +
            "       verified_emissions_2022,\n" +
            "       verified_emissions_2023,\n" +
            "       verified_emissions_2024,\n" +
            "       verified_emissions_2025,\n" +
            "       verified_emissions_2026,\n" +
            "       verified_emissions_2027,\n" +
            "       verified_emissions_2028,\n" +
            "       verified_emissions_2029,\n" +
            "       verified_emissions_2030,\n" +
            "       verified_emissions_total,\n" +
            "       surrendered_emissions_2021,\n" +
            "       surrendered_emissions_2022,\n" +
            "       surrendered_emissions_2023,\n" +
            "       surrendered_emissions_2024,\n" +
            "       surrendered_emissions_2025,\n" +
            "       surrendered_emissions_2026,\n" +
            "       surrendered_emissions_2027,\n" +
            "       surrendered_emissions_2028,\n" +
            "       surrendered_emissions_2029,\n" +
            "       surrendered_emissions_2030,\n" +
            "       surrendered_emissions_total,\n" +
            "       static_surrender_status_2021,\n" +
            "       static_surrender_status_2022,\n" +
            "       static_surrender_status_2023,\n" +
            "       static_surrender_status_2024,\n" +
            "       static_surrender_status_2025,\n" +
            "       static_surrender_status_2026,\n" +
            "       static_surrender_status_2027,\n" +
            "       static_surrender_status_2028,\n" +
            "       static_surrender_status_2029,\n" +
            "       static_surrender_status_2030,\n" +
            "       cum_verified_emissions_2021,\n" +
            "       cum_verified_emissions_2022,\n" +
            "       cum_verified_emissions_2023,\n" +
            "       cum_verified_emissions_2024,\n" +
            "       cum_verified_emissions_2025,\n" +
            "       cum_verified_emissions_2026,\n" +
            "       cum_verified_emissions_2027,\n" +
            "       cum_verified_emissions_2028,\n" +
            "       cum_verified_emissions_2029,\n" +
            "       cum_verified_emissions_2030,\n" +
            "       cum_surrendered_emissions_2021,\n" +
            "       cum_surrendered_emissions_2022,\n" +
            "       cum_surrendered_emissions_2023,\n" +
            "       cum_surrendered_emissions_2024,\n" +
            "       cum_surrendered_emissions_2025,\n" +
            "       cum_surrendered_emissions_2026,\n" +
            "       cum_surrendered_emissions_2027,\n" +
            "       cum_surrendered_emissions_2028,\n" +
            "       cum_surrendered_emissions_2029,\n" +
            "       cum_surrendered_emissions_2030,\n" +
            "       cum_surrendered_emissions_2021 - cum_verified_emissions_2021 as cum_annual_emissions_2021,\n" +
            "       cum_surrendered_emissions_2022 - cum_verified_emissions_2022 as cum_annual_emissions_2022,\n" +
            "       cum_surrendered_emissions_2023 - cum_verified_emissions_2023 as cum_annual_emissions_2023,\n" +
            "       cum_surrendered_emissions_2024 - cum_verified_emissions_2024 as cum_annual_emissions_2024,\n" +
            "       cum_surrendered_emissions_2025 - cum_verified_emissions_2025 as cum_annual_emissions_2025,\n" +
            "       cum_surrendered_emissions_2026 - cum_verified_emissions_2026 as cum_annual_emissions_2026,\n" +
            "       cum_surrendered_emissions_2027 - cum_verified_emissions_2027 as cum_annual_emissions_2027,\n" +
            "       cum_surrendered_emissions_2028 - cum_verified_emissions_2028 as cum_annual_emissions_2028,\n" +
            "       cum_surrendered_emissions_2029 - cum_verified_emissions_2029 as cum_annual_emissions_2029,\n" +
            "       cum_surrendered_emissions_2030 - cum_verified_emissions_2030 as cum_annual_emissions_2030,\n" +
            "       allocations_2021,\n" +
            "       allocations_2022,\n" +
            "       allocations_2023,\n" +
            "       allocations_2024,\n" +
            "       allocations_2025,\n" +
            "       allocations_2026,\n" +
            "       allocations_2027,\n" +
            "       allocations_2028,\n" +
            "       allocations_2029,\n" +
            "       allocations_2030,\n" +
            "       allocations_total\n" +
            "from (\n" +
            "     select regulator,\n" +
            "            ah_name,\n" +
            "            account_name,\n" +
            "            account_type,\n" +
            "            installation_name,\n" +
            "            identifier,\n" +
            "            activity_type,\n" +
            "            permit_or_monitoring_plan_id,\n" +
            "            start_year,\n" +
            "            end_year,\n" +
            "            string_agg((CASE WHEN year = 2021 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2021,\n" +
            "            string_agg((CASE WHEN year = 2022 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2022,\n" +
            "            string_agg((CASE WHEN year = 2023 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2023,\n" +
            "            string_agg((CASE WHEN year = 2024 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2024,\n" +
            "            string_agg((CASE WHEN year = 2025 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2025,\n" +
            "            string_agg((CASE WHEN year = 2026 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2026,\n" +
            "            string_agg((CASE WHEN year = 2027 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2027,\n" +
            "            string_agg((CASE WHEN year = 2028 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2028,\n" +
            "            string_agg((CASE WHEN year = 2029 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2029,\n" +
            "            string_agg((CASE WHEN year = 2030 and excluded is true THEN 'EXCLUDED' END), '') AS excluded_2030,\n" +
            "            sum(coalesce((CASE WHEN year = 2021 THEN verified_emissions END), 0)) AS verified_emissions_2021,\n" +
            "            sum(coalesce((CASE WHEN year = 2022 THEN verified_emissions END), 0)) AS verified_emissions_2022,\n" +
            "            sum(coalesce((CASE WHEN year = 2023 THEN verified_emissions END), 0)) AS verified_emissions_2023,\n" +
            "            sum(coalesce((CASE WHEN year = 2024 THEN verified_emissions END), 0)) AS verified_emissions_2024,\n" +
            "            sum(coalesce((CASE WHEN year = 2025 THEN verified_emissions END), 0)) AS verified_emissions_2025,\n" +
            "            sum(coalesce((CASE WHEN year = 2026 THEN verified_emissions END), 0)) AS verified_emissions_2026,\n" +
            "            sum(coalesce((CASE WHEN year = 2027 THEN verified_emissions END), 0)) AS verified_emissions_2027,\n" +
            "            sum(coalesce((CASE WHEN year = 2028 THEN verified_emissions END), 0)) AS verified_emissions_2028,\n" +
            "            sum(coalesce((CASE WHEN year = 2029 THEN verified_emissions END), 0)) AS verified_emissions_2029,\n" +
            "            sum(coalesce((CASE WHEN year = 2030 THEN verified_emissions END), 0)) AS verified_emissions_2030,\n" +
            "            sum(coalesce(CASE WHEN year in (select generate_series(2021, ?)) THEN verified_emissions END, 0)) AS verified_emissions_total,\n" +
            "            sum(coalesce((CASE WHEN year = 2021 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2021,\n" +
            "            sum(coalesce((CASE WHEN year = 2022 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2022,\n" +
            "            sum(coalesce((CASE WHEN year = 2023 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2023,\n" +
            "            sum(coalesce((CASE WHEN year = 2024 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2024,\n" +
            "            sum(coalesce((CASE WHEN year = 2025 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2025,\n" +
            "            sum(coalesce((CASE WHEN year = 2026 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2026,\n" +
            "            sum(coalesce((CASE WHEN year = 2027 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2027,\n" +
            "            sum(coalesce((CASE WHEN year = 2028 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2028,\n" +
            "            sum(coalesce((CASE WHEN year = 2029 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2029,\n" +
            "            sum(coalesce((CASE WHEN year = 2030 THEN surrendered_emissions END), 0)) AS surrendered_emissions_2030,\n" +
            "            sum(coalesce(CASE WHEN year in (select generate_series(2021, ?)) THEN surrendered_emissions END, 0)) AS surrendered_emissions_total,\n" +
            "            string_agg((CASE WHEN year = 2021 THEN compliance_status END), '') AS static_surrender_status_2021,\n" +
            "            string_agg((CASE WHEN year = 2022 THEN compliance_status END), '') AS static_surrender_status_2022,\n" +
            "            string_agg((CASE WHEN year = 2023 THEN compliance_status END), '') AS static_surrender_status_2023,\n" +
            "            string_agg((CASE WHEN year = 2024 THEN compliance_status END), '') AS static_surrender_status_2024,\n" +
            "            string_agg((CASE WHEN year = 2025 THEN compliance_status END), '') AS static_surrender_status_2025,\n" +
            "            string_agg((CASE WHEN year = 2026 THEN compliance_status END), '') AS static_surrender_status_2026,\n" +
            "            string_agg((CASE WHEN year = 2027 THEN compliance_status END), '') AS static_surrender_status_2027,\n" +
            "            string_agg((CASE WHEN year = 2028 THEN compliance_status END), '') AS static_surrender_status_2028,\n" +
            "            string_agg((CASE WHEN year = 2029 THEN compliance_status END), '') AS static_surrender_status_2029,\n" +
            "            string_agg((CASE WHEN year = 2030 THEN compliance_status END), '') AS static_surrender_status_2030,\n" +
            "            sum(coalesce((CASE WHEN year in (2021) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2021,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2022)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2022,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2023)) THEN surrendered_emissions END),0)) AS cum_surrendered_emissions_2023,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2024)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2024,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2025)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2025,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2026)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2026,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2027)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2027,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2028)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2028,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2029)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2029,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2030)) THEN surrendered_emissions END), 0)) AS cum_surrendered_emissions_2030,\n" +
            "            sum(coalesce((CASE WHEN year in (2021) THEN verified_emissions END), 0)) AS cum_verified_emissions_2021,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2022)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2022,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2023)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2023,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2024)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2024,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2025)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2025,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2026)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2026,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2027)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2027,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2028)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2028,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2029)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2029,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021,2030)) THEN verified_emissions END), 0)) AS cum_verified_emissions_2030,\n" +
            "            sum(coalesce((CASE WHEN year = 2021 THEN allocation END), 0)) AS allocations_2021,\n" +
            "            sum(coalesce((CASE WHEN year = 2022 THEN allocation END), 0)) AS allocations_2022,\n" +
            "            sum(coalesce((CASE WHEN year = 2023 THEN allocation END), 0)) AS allocations_2023,\n" +
            "            sum(coalesce((CASE WHEN year = 2024 THEN allocation END), 0)) AS allocations_2024,\n" +
            "            sum(coalesce((CASE WHEN year = 2025 THEN allocation END), 0)) AS allocations_2025,\n" +
            "            sum(coalesce((CASE WHEN year = 2026 THEN allocation END), 0)) AS allocations_2026,\n" +
            "            sum(coalesce((CASE WHEN year = 2027 THEN allocation END), 0)) AS allocations_2027,\n" +
            "            sum(coalesce((CASE WHEN year = 2028 THEN allocation END), 0)) AS allocations_2028,\n" +
            "            sum(coalesce((CASE WHEN year = 2029 THEN allocation END), 0)) AS allocations_2029,\n" +
            "            sum(coalesce((CASE WHEN year = 2030 THEN allocation END), 0)) AS allocations_2030,\n" +
            "            sum(coalesce((CASE WHEN year in (select generate_series(2021, ?)) THEN allocation END), 0)) AS allocations_total\n" +
            "      from (\n" +
            "        with years as (select id, year from allocation_year order by year asc)\n" +
            "        select account_data_per_year.*, coalesce(surrenders, 0) - coalesce(reversals, 0) as surrendered_emissions from (\n" +
            "           select years.year                                 as year,\n" +
            "                 ce.regulator,\n" +
            "                 ah.name                                     as ah_name,\n" +
            "                 a.id                                        as account_id,\n" +
            "                 a.account_name,\n" +
            "                 a.registry_account_type                     as account_type,\n" +
            "                 " + INSTALLATION_NAME_COLUMN + "            as installation_name,\n" +
            "                 ce.identifier,\n" +
            "                 " + ACTIVITY_TYPE_COLUMN + "                as activity_type,\n" +
            "                 " + PERMIT_OR_MONITORING_PLAN_ID_COLUMN + " as permit_or_monitoring_plan_id,\n" +
            "                 ce.start_year,\n" +
            "                 ce.end_year,\n" +
            "                 scs.compliance_status,\n" +
            "                 eee.excluded,\n" +
            "                 sum(coalesce(ae.allocated, 0) - coalesce(ae.returned, 0) - coalesce(ae.reversed, 0)) as allocation,\n" +
            "                 " + VERIFIED_EMISSIONS_COLUMN + "           as verified_emissions\n" +
            "           from account a\n" +
            "           cross join years\n" +
            "           inner join compliant_entity ce on ce.id = a.compliant_entity_id\n" +
            "           left join account_holder ah on ah.id = a.account_holder_id\n" +
            "           left join allocation_entry ae on ae.compliant_entity_id = ce.id and years.id = ae.allocation_year_id and years.year <= EXTRACT(YEAR FROM ?) \n" +
            "           left join static_compliance_status scs on scs.compliant_entity_id = ce.id and scs.year = years.year and scs.year <= EXTRACT(YEAR FROM ?) \n" +
            "           left join exclude_emissions_entry eee on eee.compliant_entity_id = ce.identifier and eee.year = years.year and eee.last_updated <= ? \n" +
            "           left join ( " + MOST_RECENT_EMISSIONS_ROWS + " ) ee on ee.compliant_entity_id = ce.identifier and ee.year = years.year\n" +
            "           group by    years.year," +
            "                       ce.regulator," +
            "                       ah.name, " +
            "                       a.id," +
            "                       a.account_name," +
            "                       a.registry_account_type, " +
            "                       ce.identifier," +
            "                       ce.start_year," +
            "                       ce.end_year," +
            "                       ce.id, ah.id, scs.id, eee.id, years.id,scs.compliance_status,eee.excluded\n" +
            "           order by account_name, ah_name, installation_name, year\n" +
            "    ) account_data_per_year \n" +
            "        left join ( " + getSurrendersOrReversalsQuery("SurrenderAllowances") + " ) tx_surrender on tx_surrender.year = account_data_per_year.year and tx_surrender.id = account_data_per_year.account_id\n" +
            "        left join ( " + getSurrendersOrReversalsQuery("ReverseSurrenderAllowances") + " ) tx_reversal on tx_reversal.year = account_data_per_year.year and tx_reversal.id = account_data_per_year.account_id\n" +
            "    ) t1\n" +
            "group by regulator,\n" +
            "         ah_name, \n" +
            "         account_name, \n" +
            "         account_type, \n" +
            "         installation_name, \n" +
            "         identifier, \n" +
            "         activity_type,         \n" +
            "         permit_or_monitoring_plan_id, \n" +
            "         start_year, \n" +
            "         end_year \n" +
            "order by ah_name desc\n" +
            ") t2";

    @Override
    public List<Compliance10YearsReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<Compliance10YearsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        LocalDateTime cutOffDateTime = DateRangeUtil.getCutOffDateTime(reportQueryInfo);
        referenceYear = calculateReferenceYear(cutOffDateTime);
        return jdbcTemplate.query(REPORT_QUERY, this, referenceYear, referenceYear, referenceYear,
                cutOffDateTime, cutOffDateTime, cutOffDateTime, cutOffDateTime, cutOffDateTime, cutOffDateTime, cutOffDateTime);
    }

    @Override
    public Compliance10YearsReportData mapRow(ResultSet rs, int i) throws SQLException {

        return Compliance10YearsReportData.builder()
            .regulator(rs.getString("regulator"))
            .accountHolderName(rs.getString("ah_name"))
            .accountName(rs.getString("account_name"))
            .accountType(rs.getString("account_type"))
            .installationName(rs.getString("installation_name"))
            .installationIdentifier(rs.getLong("identifier"))
            .activityTypeCode(rs.getString("activity_type"))
            .permitOrMonitoringPlanId(rs.getString("permit_or_monitoring_plan_id"))
            .firstYearOfVerifiedEmissions(rs.getString("start_year"))
            .lastYearOfVerifiedEmissions(rs.getObject("end_year") != null ? rs.getString("end_year") : null)
            .reportingYears(ComplianceReportUtil.calculateReportingYears(rs.getInt("start_year"), rs.getInt("end_year"), referenceYear))
            .reportingMonths(ComplianceReportUtil.calculateReportingMonths(rs.getInt("start_year"), rs.getInt("end_year"), referenceYear))
            .annualEmissionsReported(create10YearVerifiedEmissionsList(rs, referenceYear))
            .surrenderedEmissions(create10YearList(rs, "surrendered_emissions", true, referenceYear))
            .staticSurrenderStatus(create10YearList(rs, "static_surrender_status", false, referenceYear))
            .cumulativeVerifiedEmissions(create10YearList(rs, "cum_verified_emissions", false, referenceYear))
            .cumulativeSurrenders(create10YearList(rs, "cum_surrendered_emissions", false, referenceYear))
            .cumulativeAnnualEmissionsReported(create10YearList(rs, "cum_annual_emissions", false, referenceYear))
            .freeAllocations(create10YearList(rs, "allocations", true, CURRENT_YEAR))
            .build();
    }

    /**
     * Method calculates the reference year for which compliance data will be included in the report.
     * If current date is after the 1st of May (> 30/4) of the current year, the reference year is the current year.
     * Otherwise, the reference year is the previous year.
     */
    private int calculateReferenceYear(LocalDateTime currentDate) {
        int currentYear = currentDate.getYear();
        LocalDateTime complianceDate = LocalDateTime.parse(currentYear + "-05-01T00:00:00");

        if (currentDate.isBefore(complianceDate)) {
            return currentYear - 1;
        } else {
            return currentYear;
        }
    }

    /**
     * Method for creating the list of annual values for each data of the report
     *
     * @param rs the result set
     * @param column the column name for which we will retrieve the annual prices
     * @param hasTotal if years total is contained in the result set
     * @param upperYear the year until which we want to display the data
     **/
    private List<String> create10YearList(ResultSet rs, String column, boolean hasTotal, int upperYear) throws SQLException {
        List<String> list = new ArrayList<>();

        for (int year : years) {
            list.add(upperYear >= year ? rs.getString(column + "_" + year) : null);
        }
        if (hasTotal) {
            list.add(rs.getString(column + "_total"));
        }
        return list;
    }


    /**
     * Method for creating the list of annual values for allocation data of the report
     *
     * @param rs the result set
     * @param upperYear the year until which we want to display the data
     **/
    private List<String> create10YearVerifiedEmissionsList(ResultSet rs, int upperYear) throws SQLException {
        List<String> list = new ArrayList<>();

        for (int year : years) {

            String excluded = rs.getString("excluded_" + year);
            Long emissions = rs.getLong("verified_emissions_" + year);
            String value = "EXCLUDED".equals(excluded) ? "EXCLUDED" : String.valueOf(emissions);

            list.add(upperYear >= year ? value : null);
        }
        list.add(String.valueOf(rs.getLong("verified_emissions_total")));
        return list;
    }

    /**
     * Method for creating the query for surrender or reversal transactions
     **/
    private static String getSurrendersOrReversalsQuery(String type) {

        String alias = type.equals("ReverseSurrenderAllowances") ? "reversals" : "surrenders";
        String joinOn = type.equals("ReverseSurrenderAllowances") ? "t.acquiring_account_identifier" : "t.transferring_account_identifier";

        String query =
            "select years.year, aa.id, COALESCE(sum(t.quantity), 0) as " + alias + "\n" +
                "from transaction t\n" +
                "         inner join account aa on " + joinOn + " = aa.identifier\n" +
                "         left join installation_ownership io2 on io2.account_id = aa.id\n" +
                "         left join installation ins on io2.installation_id = ins.compliant_entity_id\n" +
                "         cross join years\n" +
                "where t.type = '" + type + "'\n" +
                "  and t.status = 'COMPLETED'\n" +
                "  and execution_date >= TO_TIMESTAMP(concat(years.year - 1, '-05-01'), 'YYYY-MM-DD')\n" +
                "  and execution_date < TO_TIMESTAMP(concat(years.year, '-05-01'), 'YYYY-MM-DD')\n" +
                " and execution_date <= ? " +
                "group by io2.installation_id, aa.id, years.year";

        return query;
    }
}
