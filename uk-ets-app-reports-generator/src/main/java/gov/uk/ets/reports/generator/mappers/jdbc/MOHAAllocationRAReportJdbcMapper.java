package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.MOHAAllocationRAReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class MOHAAllocationRAReportJdbcMapper
        implements ReportDataMapper<MOHAAllocationRAReportData>, RowMapper<MOHAAllocationRAReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "select " +
            "           case when ah.type = 'ORGANISATION' or ah.type ='GOVERNMENT' then ah.name else CONCAT(ahr.first_name,' ',ahr.last_name) end as account_holder_name, \n" +
            "           ce.identifier as maritime_operator_id, \n" +
            "           mo.maritime_monitoring_plan_identifier, \n" +
            "           mo.imo, \n" +
            "           ce.start_year as first_year, \n" +
            "           ce.regulator, \n" +
            "           ce.end_year as last_year\n" +
            "   from account_holder as ah  \n" +
            "   inner join account as ac \n" +
            "       on ah.id = ac.account_holder_id \n" +
            "   left join account_holder_representative ahr \n" +
            "       on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n" +
            "   inner join compliant_entity as ce \n" +
            "       on ac.compliant_entity_id = ce.id \n" +
            "   inner join maritime_operator as mo \n" +
            "       on ce.id = mo.compliant_entity_id \n" +
            "group by \n" +
            "           ahr.first_name, \n" +
            "           ahr.last_name, \n" +
            "           ah.type, \n" +
            "           ah.name, \n" +
            "           ce.identifier, \n" +
            "           mo.maritime_monitoring_plan_identifier, \n" +
            "           mo.imo, \n" +
            "           ce.start_year, \n" +
            "           ce.regulator, \n" +
            "           ce.end_year \n" +
            "order by ah.name,ce.identifier \n";

    @Override
    public List<MOHAAllocationRAReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public MOHAAllocationRAReportData mapRow(ResultSet resultSet, int i) throws SQLException {

        return MOHAAllocationRAReportData.builder()
                .accountHolderName(resultSet.getString("account_holder_name"))
                .maritimeOperatorId(resultSet.getString("maritime_operator_id"))
                .imo(resultSet.getString("imo"))
                .maritimeMonitoringPlanId(resultSet.getString("maritime_monitoring_plan_identifier"))
                .firstYear(resultSet.getInt("first_year"))
                .lastYear(Util.getNullableInteger(resultSet, "last_year"))
                .regulator(resultSet.getString("regulator"))
                .build();
    }
}
