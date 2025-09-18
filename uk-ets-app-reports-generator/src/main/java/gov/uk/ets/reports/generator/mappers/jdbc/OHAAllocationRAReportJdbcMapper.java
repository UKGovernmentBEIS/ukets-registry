package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.OHAAllocationRAReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.time.LocalDate;
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
public class OHAAllocationRAReportJdbcMapper
        implements ReportDataMapper<OHAAllocationRAReportData>, RowMapper<OHAAllocationRAReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "select " +
            "           case when ah.type = 'ORGANISATION' or ah.type ='GOVERNMENT' then ah.name else CONCAT(ahr.first_name,' ',ahr.last_name) end as account_holder_name, \n" +
            "           ce.identifier as installation_id, \n"+
            "           ins.installation_name, \n"+
            "           ins.permit_identifier, \n"+
            "           ins.activity_type, \n"+
            "           ce.start_year as first_year, \n"+
            "           ce.regulator, \n"+
            "           ay.year as allocation_year, \n"+
            "           sum(case when ae.type = 'NAT' then ae.entitlement else 0 end) as nat_entitlement, \n" +
            "           sum(case when ae.type = 'NER' then ae.entitlement else 0 end) as ner_entitlement, \n" +
            "           sum(ae.entitlement) as total_entitlement, \n" +
            "           sum(case when ae.type = 'NAT' then COALESCE(ae.allocated, 0) - COALESCE(ae.returned, 0) - COALESCE(ae.reversed, 0) else 0 end) as nat_allowances_received, \n" +
            "           sum(case when ae.type = 'NER' then COALESCE(ae.allocated, 0) - COALESCE(ae.returned, 0) - COALESCE(ae.reversed, 0) else 0 end) as ner_allowances_received, \n" +
            "           sum(COALESCE(ae.allocated, 0) - COALESCE(ae.returned, 0) - COALESCE(ae.reversed, 0)) as total_allowances_received, \n" +
            "           ce.end_year as last_year,\n" +
            "           case when ast.status = 'WITHHELD' then 'TRUE' else 'FALSE' end as withheld,\n" +
            "           case when eee.excluded is true then 'TRUE' else 'FALSE' end as excluded\n"+
            "from account_holder as ah  \n" +
            "   inner join account as ac \n" +
            "       on ah.id = ac.account_holder_id \n" +
            "   left join account_holder_representative ahr\n"+
            "       on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n"+
            "   inner join compliant_entity as ce \n" +
            "       on ac.compliant_entity_id = ce.id \n" +
            "   inner join installation as ins \n"+
            "       on ce.id = ins.compliant_entity_id \n"+
            "   left join allocation_entry as ae \n"+
            "       on ce.id=ae.compliant_entity_id \n"+
            "   left join allocation_year as ay \n"+
            "       on ae.allocation_year_id = ay.id \n"+
            "   left join allocation_status ast " +
            "       on ast.allocation_year_id = ae.allocation_year_id and ce.id = ast.compliant_entity_id \n" +
            "   left join exclude_emissions_entry eee " +
            "       on eee.compliant_entity_id = ce.identifier and eee.year = ay.year \n"+
            "group by \n"+
            "           ahr.first_name, \n"+
            "           ahr.last_name, \n"+
            "           ah.type, \n"+
            "           ah.name, \n"+
            "           ce.identifier, \n"+
            "           ins.installation_name, \n"+
            "           ins.permit_identifier, \n"+
            "           ins.activity_type, \n"+
            "           ce.start_year, \n"+
            "           ce.regulator, \n"+
            "           ay.year, \n" +
            "           ast.status, \n" +
            "           ce.end_year, \n" +
            "           eee.excluded \n"+
            "order by ah.name,ce.identifier \n";

    @Override
    public List<OHAAllocationRAReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
         return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public OHAAllocationRAReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return OHAAllocationRAReportData.builder()
                .accountHolderName(resultSet.getString("account_holder_name"))
                .installationId(resultSet.getString("installation_id"))
                .installationName(resultSet.getString("installation_name"))
                .permitIdentifier(resultSet.getString("permit_identifier"))
                .activityType(resultSet.getString("activity_type"))
                .firstYear(resultSet.getInt("first_year"))
                .regulator(resultSet.getString("regulator"))
                .allocationYear(resultSet.getInt("allocation_year"))
                .natEntitlement(resultSet.getInt("nat_entitlement"))
                .nerEntitlement(resultSet.getInt("ner_entitlement"))
                .totalEntitlement(resultSet.getInt("total_entitlement"))
                .natAllowancesReceived(resultSet.getInt("nat_allowances_received"))
                .nerAllowancesReceived(resultSet.getInt("ner_allowances_received"))
                .totalAllowancesReceived(resultSet.getInt("total_allowances_received"))
                .withheld(resultSet.getString("withheld"))
                .excluded(resultSet.getString("excluded"))
                .lastYear(Util.getNullableInteger(resultSet, "last_year"))
                .build();
    }
}
