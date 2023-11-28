package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.AllocationsReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.math.BigDecimal;
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
public class AllocationsReportJdbcMapper implements ReportDataMapper<AllocationsReportData>,
    RowMapper<AllocationsReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String PERMIT_OR_MONITORING_PLAN_ID_COLUMN =
        "case\n" +
        "    when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT'\n" +
        "        then (select permit_identifier from installation i where i.compliant_entity_id = ce.id)\n" +
        "    else (select monitoring_plan_identifier\n" +
        "          from aircraft_operator ao\n" +
        "          where ao.compliant_entity_id = ce.id)";

    private static final String INSTALLATION_NAME_COLUMN =
        "case\n" +
        "    when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then (select installation_name\n" +
        "                                                                    from installation i\n" +
        "                                                                    where i.compliant_entity_id = ce.id)\n" +
        "    else ''";

    private static final String ACTIVITY_TYPE_COLUMN =
        "case\n" +
        "    when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then (select activity_type\n" +
        "                                                                    from installation i\n" +
        "                                                                    where i.compliant_entity_id = ce.id)\n" +
        "    else 'AIRCRAFT_OPERATOR'";

    /**
     * <p> The query returns information about compliant entities allocations for the years 2021 to 2030.</p>
     * <p> Table t query returns the report data per entity and per year.</p>
     *
     | year  |  ah_name  | permit_or_monitoring_plan_id  |    identifier  |   installation_name  |    activity_type  |regulator|  type  |entitlement | allocated | remaining | withhold_status |
     |-------|-----------|-------------------------------|----------------|----------------------|-------------------|---------|--------|------------|-----------|-----------|-----------------|
     | 2021  |  AH new   |        ghtrhw355456           |     1000116    |   Installation name  |        TYPE       |  SEPA   |   NER  | 10         |      0    |    10     |    ALLOWED      |
     | 2022  |  AH new   |        ghtrhw355456           |     1000116    |   Installation name  |        TYPE       |  SEPA   |   NER  | 120        |      0    |    120    |    ALLOWED      |
     | 2023  |  AH new   |        ghtrhw355456           |     1000116    |   Installation name  |        TYPE       |  SEPA   |   NER  | 0          |      0    |    0      |    ALLOWED      |
     | 2024  |  AH new   |        ghtrhw355456           |     1000116    |   Installation name  |        TYPE       |  SEPA   |   NER  | 0          |      0    |    0      |    ALLOWED      |
     *
     * We pivot table t on the year to create a separate column for each year for values entitlement, allocated, remaining and withhold_status.
     * We used CASE expressions to build the pivot table, creating a new column with the sum of rows in each case.
     */
    private static final String REPORT_QUERY = "select ah_name,\n" +
        "       permit_or_monitoring_plan_id,\n" +
        "       identifier,\n" +
        "       installation_name,\n" +
        "       activity_type,\n" +
        "       regulator,\n" +
        "       type,\n" +
        "       string_agg((CASE WHEN year = 2021 THEN withhold_status END), '') AS withhold_status_2021,\n" +
        "       sum((CASE WHEN year = 2021 THEN entitlement END))   AS entitlement_2021,\n" +
        "       sum((CASE WHEN year = 2021 THEN allocated END))     AS allocated_2021,\n" +
        "       sum((CASE WHEN year = 2021 THEN remaining END))     AS remaining_2021,\n" +
        "       string_agg((CASE WHEN year = 2022 THEN withhold_status END), '') AS withhold_status_2022,\n" +
        "       sum((CASE WHEN year = 2022 THEN entitlement END))   AS entitlement_2022,\n" +
        "       sum((CASE WHEN year = 2022 THEN allocated END))     AS allocated_2022,\n" +
        "       sum((CASE WHEN year = 2022 THEN remaining END))     AS remaining_2022,\n" +
        "       string_agg((CASE WHEN year = 2023 THEN withhold_status END), '') AS withhold_status_2023,\n" +
        "       sum((CASE WHEN year = 2023 THEN entitlement END))   AS entitlement_2023,\n" +
        "       sum((CASE WHEN year = 2023 THEN allocated END))     AS allocated_2023,\n" +
        "       sum((CASE WHEN year = 2023 THEN remaining END))     AS remaining_2023,\n" +
        "       string_agg((CASE WHEN year = 2024 THEN withhold_status END), '') AS withhold_status_2024,\n" +
        "       sum((CASE WHEN year = 2024 THEN entitlement END))   AS entitlement_2024,\n" +
        "       sum((CASE WHEN year = 2024 THEN allocated END))     AS allocated_2024,\n" +
        "       sum((CASE WHEN year = 2024 THEN remaining END))     AS remaining_2024,\n" +
        "       string_agg((CASE WHEN year = 2025 THEN withhold_status END), '') AS withhold_status_2025,\n" +
        "       sum((CASE WHEN year = 2025 THEN entitlement END))   AS entitlement_2025,\n" +
        "       sum((CASE WHEN year = 2025 THEN allocated END))     AS allocated_2025,\n" +
        "       sum((CASE WHEN year = 2025 THEN remaining END))     AS remaining_2025,\n" +
        "       string_agg((CASE WHEN year = 2026 THEN withhold_status END), '') AS withhold_status_2026,\n" +
        "       sum((CASE WHEN year = 2026 THEN entitlement END))   AS entitlement_2026,\n" +
        "       sum((CASE WHEN year = 2026 THEN allocated END))     AS allocated_2026,\n" +
        "       sum((CASE WHEN year = 2026 THEN remaining END))     AS remaining_2026,\n" +
        "       string_agg((CASE WHEN year = 2027 THEN withhold_status END), '') AS withhold_status_2027,\n" +
        "       sum((CASE WHEN year = 2027 THEN entitlement END))   AS entitlement_2027,\n" +
        "       sum((CASE WHEN year = 2027 THEN allocated END))     AS allocated_2027,\n" +
        "       sum((CASE WHEN year = 2027 THEN remaining END))     AS remaining_2027,\n" +
        "       string_agg((CASE WHEN year = 2028 THEN withhold_status END), '') AS withhold_status_2028,\n" +
        "       sum((CASE WHEN year = 2028 THEN entitlement END))   AS entitlement_2028,\n" +
        "       sum((CASE WHEN year = 2028 THEN allocated END))     AS allocated_2028,\n" +
        "       sum((CASE WHEN year = 2028 THEN remaining END))     AS remaining_2028,\n" +
        "       string_agg((CASE WHEN year = 2029 THEN withhold_status END), '') AS withhold_status_2029,\n" +
        "       sum((CASE WHEN year = 2029 THEN entitlement END))   AS entitlement_2029,\n" +
        "       sum((CASE WHEN year = 2029 THEN allocated END))     AS allocated_2029,\n" +
        "       sum((CASE WHEN year = 2029 THEN remaining END))     AS remaining_2029,\n" +
        "       string_agg((CASE WHEN year = 2030 THEN withhold_status END), '') AS withhold_status_2030,\n" +
        "       sum((CASE WHEN year = 2030 THEN entitlement END))   AS entitlement_2030,\n" +
        "       sum((CASE WHEN year = 2030 THEN allocated END))     AS allocated_2030,\n" +
        "       sum((CASE WHEN year = 2030 THEN remaining END))     AS remaining_2030\n" +
        "from (\n" +
        "         with years as (select id, year from allocation_year order by year asc)\n" +
        "         select years.year as year,\n" +
        "                ah.name as ah_name,\n" +
        "                " + PERMIT_OR_MONITORING_PLAN_ID_COLUMN + " end as permit_or_monitoring_plan_id,\n" +
        "                " + INSTALLATION_NAME_COLUMN + " end as installation_name,\n" +
        "                " + ACTIVITY_TYPE_COLUMN + " end as activity_type,\n" +
        "                ce.identifier,\n" +
        "                ce.regulator,\n" +
        "                ae.type,\n" +
        "                coalesce(ae.entitlement, 0) as entitlement,\n" +
        "                coalesce(ae.allocated, 0) - coalesce(ae.returned, 0) - coalesce(ae.reversed, 0) as allocated,\n" +
        "                coalesce(ae.entitlement, 0) - (coalesce(ae.allocated, 0) - coalesce(ae.returned, 0) - coalesce(ae.reversed, 0)) as remaining,\n" +
        "                ast.status as withhold_status\n" +
        "         from account a\n" +
        "                  cross join years\n" +
        "                  inner join compliant_entity ce on ce.id = a.compliant_entity_id\n" +
        "                  left join account_holder ah on ah.id = a.account_holder_id\n" +
        "                  join allocation_entry ae on ae.compliant_entity_id = ce.id and years.id = ae.allocation_year_id\n" +
        "                  left join allocation_status ast on ast.allocation_year_id = ae.allocation_year_id and ce.id = ast.compliant_entity_id\n" +
        "         order by account_name, ah_name, installation_name, year\n" +
        "     ) t\n" +
        "group by ah_name,\n" +
        "         permit_or_monitoring_plan_id,\n" +
        "         identifier,\n" +
        "         installation_name,\n" +
        "         activity_type,\n" +
        "         regulator,\n" +
        "         type\n" +
        "order by ah_name desc";

    @Override
    public List<AllocationsReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<AllocationsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public AllocationsReportData mapRow(ResultSet rs, int i) throws SQLException {
        return AllocationsReportData.builder()
            .accountHolderName(rs.getString("ah_name"))
            .permitOrMonitoringPlanId(rs.getString("permit_or_monitoring_plan_id"))
            .identifier(rs.getLong("identifier"))
            .installationName(rs.getString("installation_name"))
            .activityTypeCode(rs.getString("activity_type"))
            .type(rs.getString("type"))
            .regulator(rs.getString("regulator"))
            .withholdStatus2021(rs.getString("withhold_status_2021"))
            .withholdStatus2022(rs.getString("withhold_status_2022"))
            .withholdStatus2023(rs.getString("withhold_status_2023"))
            .withholdStatus2024(rs.getString("withhold_status_2024"))
            .withholdStatus2025(rs.getString("withhold_status_2025"))
            .withholdStatus2026(rs.getString("withhold_status_2026"))
            .withholdStatus2027(rs.getString("withhold_status_2027"))
            .withholdStatus2028(rs.getString("withhold_status_2028"))
            .withholdStatus2029(rs.getString("withhold_status_2029"))
            .withholdStatus2030(rs.getString("withhold_status_2030"))
            .entitlement2021(longValueOrNull(rs.getObject("entitlement_2021")))
            .entitlement2022(longValueOrNull(rs.getObject("entitlement_2022")))
            .entitlement2023(longValueOrNull(rs.getObject("entitlement_2023")))
            .entitlement2024(longValueOrNull(rs.getObject("entitlement_2024")))
            .entitlement2025(longValueOrNull(rs.getObject("entitlement_2025")))
            .entitlement2026(longValueOrNull(rs.getObject("entitlement_2026")))
            .entitlement2027(longValueOrNull(rs.getObject("entitlement_2027")))
            .entitlement2028(longValueOrNull(rs.getObject("entitlement_2028")))
            .entitlement2029(longValueOrNull(rs.getObject("entitlement_2029")))
            .entitlement2030(longValueOrNull(rs.getObject("entitlement_2030")))
            .allocated2021(longValueOrNull(rs.getObject("allocated_2021")))
            .allocated2022(longValueOrNull(rs.getObject("allocated_2022")))
            .allocated2023(longValueOrNull(rs.getObject("allocated_2023")))
            .allocated2024(longValueOrNull(rs.getObject("allocated_2024")))
            .allocated2025(longValueOrNull(rs.getObject("allocated_2025")))
            .allocated2026(longValueOrNull(rs.getObject("allocated_2026")))
            .allocated2027(longValueOrNull(rs.getObject("allocated_2027")))
            .allocated2028(longValueOrNull(rs.getObject("allocated_2028")))
            .allocated2029(longValueOrNull(rs.getObject("allocated_2029")))
            .allocated2030(longValueOrNull(rs.getObject("allocated_2030")))
            .remaining2021(longValueOrNull(rs.getObject("remaining_2021")))
            .remaining2022(longValueOrNull(rs.getObject("remaining_2022")))
            .remaining2023(longValueOrNull(rs.getObject("remaining_2023")))
            .remaining2024(longValueOrNull(rs.getObject("remaining_2024")))
            .remaining2025(longValueOrNull(rs.getObject("remaining_2025")))
            .remaining2026(longValueOrNull(rs.getObject("remaining_2026")))
            .remaining2027(longValueOrNull(rs.getObject("remaining_2027")))
            .remaining2028(longValueOrNull(rs.getObject("remaining_2028")))
            .remaining2029(longValueOrNull(rs.getObject("remaining_2029")))
            .remaining2030(longValueOrNull(rs.getObject("remaining_2030")))
            .build();
    }

    /**
     * Method for getting non-null long value from a result set object.
     * @param object the object
     * @return null if the object is null, the long value otherwise
     */
    private Long longValueOrNull(Object object) {
        return object != null ? ((BigDecimal) object).longValue() : null;
    }
}