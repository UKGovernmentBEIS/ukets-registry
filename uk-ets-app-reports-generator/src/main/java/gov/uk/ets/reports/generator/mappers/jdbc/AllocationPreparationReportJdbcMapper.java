package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.AllocationPreparationReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AllocationPreparationReportJdbcMapper
        implements ReportDataMapper<AllocationPreparationReportData>, RowMapper<AllocationPreparationReportData> {

    private static final String PERMIT_OR_MONITORING_PLAN_ID_COLUMN =
            "case\n" +
                    "       when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT'\n" +
                    "           then (select permit_identifier from installation i where i.compliant_entity_id = ce.id)\n" +
                    "       else (select monitoring_plan_identifier\n" +
                    "             from aircraft_operator ao\n" +
                    "             where ao.compliant_entity_id = ce.id) end ";

    private static final String INSTALLATION_NAME_COLUMN =
            "case\n" +
                    "   when a.registry_account_type = 'OPERATOR_HOLDING_ACCOUNT' then (select installation_name\n" +
                    "                                                                   from installation i\n" +
                    "                                                                       where i.compliant_entity_id = ce.id)\n" +
                    "   else '' end ";

    private static final String DIFFERENCE = "coalesce(ae.entitlement, 0) - (coalesce(ae.allocated, 0) - coalesce(ae.returned, 0) - coalesce(ae.reversed, 0))";

    /**
     * Calculating difference: when excluded = true, entitlement = 0
     */
    private static final String DIFFERENCE_EXCLUDED = "0 - (coalesce(ae.allocated, 0) - coalesce(ae.returned, 0) - coalesce(ae.reversed, 0))";

    private static final String DIFFERENCE_COLUMN =
            "case \n" +
                    "   when eee.excluded = true \n" +
                    "       then "+DIFFERENCE_EXCLUDED+"\n" +
                    "       else "+DIFFERENCE+" end ";

    private final JdbcTemplate jdbcTemplate;

    private String createReportQuery(){
        return "select \n" +
                "    ce.regulator,\n" +
                "    ah.name as ah_name,\n" +
                "    a.full_identifier as account_number,\n" +
                "    ae.type as allocation_type,\n" +
                "    ay.year,\n" +
                "    a.account_name as account_name,\n" +
                "    a.registry_account_type as account_type,\n" +
                "    a.account_status as account_status,\n" +
                "    ce.identifier,\n" +
                "    "+PERMIT_OR_MONITORING_PLAN_ID_COLUMN+" as permit_or_monitoring_plan_id,\n" +
                "    "+INSTALLATION_NAME_COLUMN+" as installation_name,\n" +
                "    coalesce(ae.entitlement, 0) as entitlement,\n" +
                "    coalesce(ae.allocated, 0) - coalesce(ae.returned, 0) - coalesce(ae.reversed, 0) as allocated,\n" +
                "    "+DIFFERENCE_COLUMN+" as difference,\n" +
                "    case when ast.status = 'WITHHELD' then 'TRUE' else 'FALSE' end as withheld,\n" +
                "    case when eee.excluded = true then 'TRUE' else 'FALSE' end as excluded\n" +
                "from account a\n" +
                "join compliant_entity ce on ce.id = a.compliant_entity_id\n" +
                "left join account_holder ah on ah.id = a.account_holder_id\n" +
                "join allocation_entry ae on ae.compliant_entity_id = ce.id\n" +
                "left join allocation_status ast on ast.allocation_year_id = ae.allocation_year_id and ce.id = ast.compliant_entity_id\n" +
                "join allocation_year ay on ae.allocation_year_id = ay.id\n" +
                "left join exclude_emissions_entry eee on eee.compliant_entity_id = ce.identifier and eee.year = ay.year\n" +
                "where " +
                "   account_status <> 'CLOSED' and ay.year <= "+this.getCurrentYear()+" and"+
                "   ((eee.excluded = true and "+DIFFERENCE_EXCLUDED+" <> 0) or ((eee.excluded = false or eee.excluded is null) and "+DIFFERENCE+" <> 0))"+
                "order by\n" +
                "    regulator,\n" +
                "    ah_name,\n" +
                "    account_number,\n" +
                "    year desc";
    }

    @Override
    public List<AllocationPreparationReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<AllocationPreparationReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(createReportQuery(), this);
    }

    @Override
    public AllocationPreparationReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return
                AllocationPreparationReportData.builder()
                        .accountHolderName(rs.getString("ah_name"))
                        .regulator(rs.getString("regulator"))
                        .accountName(rs.getString("account_name"))
                        .accountNumber(rs.getString("account_number"))
                        .accountStatus(rs.getString("account_status"))
                        .accountType(rs.getString("account_type"))
                        .installationOrAircraftOperatorId(rs.getLong("identifier"))
                        .permitOrMonitoringPlanId(rs.getString("permit_or_monitoring_plan_id"))
                        .installationName(rs.getString("installation_name"))
                        .allocationType(rs.getString("allocation_type"))
                        .year(rs.getInt("year"))
                        .entitlement(rs.getLong("entitlement"))
                        .allocated(rs.getLong("allocated"))
                        .toBeReturned(rs.getLong("difference") < 0 ? Math.abs(rs.getLong("difference")) : 0)
                        .toBeDelivered(rs.getLong("difference") > 0 ? Math.abs(rs.getLong("difference")) : 0)
                        .withheld(rs.getString("withheld"))
                        .excluded(rs.getString("excluded"))
                        .build();
    }

    private String getCurrentYear() {
        return Integer.toString(Year.now().getValue());
    }
}
