package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.AOHAAllocationReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AOHAAllocationReportJdbcMapper
        implements ReportDataMapper<AOHAAllocationReportData>, RowMapper<AOHAAllocationReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "" +
            "select     case when ah.type = 'ORGANISATION' or ah.type ='GOVERNMENT' then ah.name else CONCAT(ahr.first_name,' ',ahr.last_name) end as account_holder_name, \n" +
            "           ce.identifier as aircraft_operator_id, \n"+
            "           ao.monitoring_plan_identifier,  \n"+
            "           ce.start_year as first_year_of_verified_emission_submission,  \n"+
            "           ce.regulator,  \n"+
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number, \n"+
            "           sum(ae.entitlement) as entitlement, \n" +
            "           sum(COALESCE(ae.allocated, 0) - COALESCE(ae.returned, 0) - COALESCE(ae.reversed, 0)) as allocation \n"+
            "from account_holder as ah \n" +
            "   inner join account as ac  \n" +
            "       on ah.id = ac.account_holder_id   \n" +
            "   left join account_holder_representative ahr \n"+
            "       on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n"+
            "   inner join compliant_entity as ce   \n" +
            "       on ac.compliant_entity_id = ce.id   \n" +
            "   inner join aircraft_operator as ao  \n" +
            "       on ce.id = ao.compliant_entity_id \n" +
            "   left join allocation_entry as ae \n" +
            "       on ce.id=ae.compliant_entity_id \n" +
            "   left join allocation_year as ay \n" +
            "       on ae.allocation_year_id = ay.id \n" +
            "where ay.year= ? or ay.year is null \n" +
            "group by \n" +
            "           account_holder_name, \n" +
            "           ce.identifier, \n" +
            "           ao.monitoring_plan_identifier,  \n" +
            "           ce.start_year,  \n" +
            "           ce.regulator,  \n" +
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number \n"+
            "order by account_holder_name\n";

    @Override
    public List<AOHAAllocationReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this, reportQueryInfo.getYear());
    }

    @Override
    public AOHAAllocationReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return AOHAAllocationReportData.builder()
                .accountHolderName(resultSet.getString("account_holder_name"))
                .aircraftOperatorId(Util.getNullableLong(resultSet, "aircraft_operator_id"))
                .monitoringPlanId(resultSet.getString("monitoring_plan_identifier"))
                .firstYear(resultSet.getInt("first_year_of_verified_emission_submission"))
                .regulator(resultSet.getString("regulator"))
                .allocated(resultSet.getInt("allocation"))
                .entitled(resultSet.getInt("entitlement"))
                .salesContactEmail(resultSet.getString("sales_contact_email"))
                .salesContactPhone(StringUtils.isNotBlank(resultSet.getString("sales_contact_phone_number_country")) ?  StringUtils.trim(resultSet.getString("sales_contact_phone_number_country") + " " +resultSet.getString("sales_contact_phone_number")) : "")
                .build();
    }
}
