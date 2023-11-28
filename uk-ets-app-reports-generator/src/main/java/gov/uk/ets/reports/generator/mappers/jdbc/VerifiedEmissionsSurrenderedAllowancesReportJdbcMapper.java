package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.VerifiedEmissionsSurrenderedAllowancesReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class VerifiedEmissionsSurrenderedAllowancesReportJdbcMapper implements
    ReportDataMapper<VerifiedEmissionsSurrenderedAllowancesReportData>,
    RowMapper<VerifiedEmissionsSurrenderedAllowancesReportData> {

    private static final String EXCLUDED = "EXCLUDED";
    private static final String N_A = "N/A";
    private final JdbcTemplate jdbcTemplate;

    private static final String AH_NAME_COLUMN =
        "case\n" +
        "  when ah.name is NULL\n" +
        "  then (select CONCAT(ah.first_name, ' ', ah.last_name) from account_holder where id = ah.id)\n" +
        "  else (select ah.name from account_holder where id = ah.id)\n" +
        "end\n";

    private static final String PERMIT_ID_OR_MONITORING_PLAN_ID_COLUMN =
        "case\n" +
        "  when acc.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then i.permit_identifier\n" +
        "  when acc.registry_account_type = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT'\n" +
        "  then ao.monitoring_plan_identifier\n" +
        "end\n";

    private static final String VERIFIED_EMISSIONS_OR_EXCLUDED_COLUMN =
        "case\n" +
        "  when eee.excluded is true then '"+EXCLUDED+"'\n" +
        "  else cast(sum(ee.emissions) as varchar)\n" +
        "end\n";

    private static final String ACCOUNT_STATUS_OR_CLOSURE_DATE_COLUMN =
        "case\n" +
        "  when acc.account_status != 'CLOSED' then 'OPEN'\n" +
        "  when acc.closing_date is not null then to_char(acc.closing_date, 'YYYY-MM-SS HH24:MI:SS')\n" +
        "  else 'CLOSED'\n" +
        "end\n";

    private static final String MOST_RECENT_EMISSIONS_ROWS =
        "select *\n" +
        "from emissions_entry\n" +
        "where (compliant_entity_id, upload_date) in\n" +
        "      (select compliant_entity_id, max(upload_date)\n" +
        "       from emissions_entry\n" +
        "       group by compliant_entity_id, year)\n";

    private static final String SURRENDER_ALLOWANCES_TX_ROWS =
            "SELECT aa.id, y.year, COALESCE(SUM(t.quantity), 0) as surrenders " +
                    "FROM years y " +
                    "JOIN transaction t ON DATE_PART('year', t.execution_date) = y.year " +
                    "INNER JOIN account aa ON t.transferring_account_identifier = aa.identifier " +
                    "LEFT JOIN installation_ownership io2 ON io2.account_id = aa.id " +
                    "LEFT JOIN installation ins ON io2.installation_id = ins.compliant_entity_id " +
                    "WHERE t.type = 'SurrenderAllowances' AND t.status = 'COMPLETED' " +
                    "GROUP BY io2.installation_id, aa.id, y.year";

    private static final String REVERSE_ALLOWANCES_TX_ROWS =
            "SELECT aa.id, y.year, COALESCE(SUM(t.quantity), 0) as reversals " +
                    "FROM years y " +
                    "JOIN transaction t ON DATE_PART('year', t.execution_date) = y.year " +
                    "INNER JOIN account aa ON t.acquiring_account_identifier = aa.identifier " +
                    "LEFT JOIN installation_ownership io2 ON io2.account_id = aa.id " +
                    "LEFT JOIN installation ins ON io2.installation_id = ins.compliant_entity_id " +
                    "WHERE t.type = 'ReverseSurrenderAllowances' AND t.status = 'COMPLETED' " +
                    "GROUP BY io2.installation_id, aa.id, y.year";

    private static final String OLD_INSTALLATION_ACCOUNTS_UNION_AOHA =
            "select io.account_id " +
                    "       from installation_ownership io\n" +
                    "       join installation ins on ins.compliant_entity_id = io.installation_id\n" +
                    "       join compliant_entity ce on ce.id = io.installation_id\n" +
                    "       where ce.identifier = account_data.entity_identifier" +
                    "  union\n" +
                    "       select acc.id from aircraft_operator ao\n" +
                    "       join compliant_entity ce on ce.id = ao.compliant_entity_id\n" +
                    "       join account acc on acc.compliant_entity_id = ce.id\n" +
                    "       where ce.identifier = account_data.entity_identifier";


    /**
     * <p> The query returns information for compliance accounts starting from 2021 and up to the current year.</p>
     * <p> The table account_data contains the report data per account for the current year. Allocations
     * are obtained by grouping the rows properly and summing the columns in each group.</p>
     *
     * <p> We join the account_data table with tables tx_surrender and tx_reversal. tx_surrender table contains the
     * completed surrender transactions executed until the moment of the report creation and tx_reversal table
     * contains the completed reversal transactions executed until the moment of the report creation. </p>
     */
    private static final String REPORT_QUERY =
        "with years as (select generate_series(2021,?) as year)\n" +
        "select account_data.*, coalesce(tx_surrender.surrenders, 0) - coalesce(tx_reversal.reversals, 0) as total_surrendered_allowances\n" +
        "from (\n" +
        "         select years.year,\n" +
        "                acc.id                                         as account_id,\n" +
        "                ce.regulator                                   as regulator,\n" +
        "                " + AH_NAME_COLUMN + "                         as account_holder_name,\n" +
        "                i.installation_name                            as installation_name,\n" +
        "                ce.identifier                                  as entity_identifier,\n" +
        "                " + PERMIT_ID_OR_MONITORING_PLAN_ID_COLUMN + " as permit_identifier,\n" +
        "                i.activity_type                                as activity_type,\n" +
        "                COALESCE(sum(ae.entitlement), 0)               as entitlement,\n" +
        "                COALESCE(sum(ae.returned), 0)                  as returned,\n" +
        "                COALESCE(sum(ae.reversed), 0)                  as reversed,\n" +
        "                COALESCE(sum(ae.allocated), 0)                 as allocated,\n" +
        "                " + VERIFIED_EMISSIONS_OR_EXCLUDED_COLUMN + "  as verified_emissions,\n" +
        "                " + ACCOUNT_STATUS_OR_CLOSURE_DATE_COLUMN + "  as account_closure,\n" +
        "                acc.compliance_status                          as dynamic_surrender_status,\n" +
        "                scs.compliance_status                          as static_compliance_status\n" +
        "         from years\n" +
        "                  cross join compliant_entity ce\n" +
        "                  inner join account acc on ce.id = compliant_entity_id\n" +
        "                  left join account_holder ah on acc.account_holder_id = ah.id\n" +
        "                  left join installation i on ce.id = i.compliant_entity_id\n" +
        "                  left join aircraft_operator ao on ce.id = ao.compliant_entity_id\n" +
        "                  left join allocation_entry ae on ce.id = ae.compliant_entity_id and ae.allocation_year_id = (select id from allocation_year where year = years.year)\n" +
        "                  left join (" + MOST_RECENT_EMISSIONS_ROWS + ") ee on ee.compliant_entity_id = ce.identifier and ee.year = years.year\n" +
        "                  left join exclude_emissions_entry eee on eee.compliant_entity_id = ce.identifier and eee.year = years.year and ee.compliant_entity_id = eee.compliant_entity_id\n" +
        "                  left join static_compliance_status scs on scs.compliant_entity_id = ce.id and scs.year = years.year and scs.year = years.year\n" +
        "         group by years.year, ce.id, acc.id, ah.id, i.installation_name, i.activity_type, i.permit_identifier, ao.monitoring_plan_identifier, ee.id, eee.id, scs.id\n" +
        "     ) account_data\n" +
        "     left join (" + SURRENDER_ALLOWANCES_TX_ROWS + ") tx_surrender ON tx_surrender.year = account_data.year\n" +
        "     and tx_surrender.id in (" + OLD_INSTALLATION_ACCOUNTS_UNION_AOHA + ")\n" +
        "     left join (" + REVERSE_ALLOWANCES_TX_ROWS + ") tx_reversal on tx_reversal.year = account_data.year\n" +
        "     and tx_reversal.id in (" + OLD_INSTALLATION_ACCOUNTS_UNION_AOHA + ")\n" +
        "order by account_holder_name, account_data.year";

    @Override
    public List<VerifiedEmissionsSurrenderedAllowancesReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<VerifiedEmissionsSurrenderedAllowancesReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate
            .query(REPORT_QUERY, this, LocalDateTime.now().getYear());
    }

    @Override
    public VerifiedEmissionsSurrenderedAllowancesReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return VerifiedEmissionsSurrenderedAllowancesReportData.builder()
            .regulator(resultSet.getString("regulator"))
            .accountHolderName(resultSet.getString("account_holder_name"))
            .installationName(resultSet.getString("installation_name"))
            .installationIdentifier(resultSet.getLong("entity_identifier"))
            .permitIdentifier(resultSet.getString("permit_identifier"))
            .mainActivityTypeCode(resultSet.getString("activity_type"))
            .year(resultSet.getInt("year"))
            .allocationEntitlement(resultSet.getLong("entitlement"))
            .allocationReturned(resultSet.getLong("returned"))
            .allocationReversed(resultSet.getLong("reversed"))
            .allocationTotal(resultSet.getLong("allocated"))
            .verifiedEmissions(parseVerifiedEmissions(resultSet))
            .accountClosure(resultSet.getString("account_closure"))
            .totalSurrenderedAllowances(resultSet.getLong("total_surrendered_allowances"))
            .dynamicComplianceStatus(resultSet.getString("dynamic_surrender_status"))
            .staticComplianceStatus(resultSet.getString("static_compliance_status"))
            .build();
    }

    private Object parseVerifiedEmissions(ResultSet resultSet) throws SQLException {

        String verifiedEmissions = resultSet.getString("verified_emissions");
        if (verifiedEmissions == null) {
            return N_A;
        }

        if (EXCLUDED.equals(verifiedEmissions)) {
            return verifiedEmissions;
        }

        return Long.parseLong(verifiedEmissions);

    }
}
