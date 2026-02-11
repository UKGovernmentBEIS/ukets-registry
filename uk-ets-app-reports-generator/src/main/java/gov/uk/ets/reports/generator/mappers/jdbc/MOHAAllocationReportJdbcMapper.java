package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.MOHAAllocationReportData;
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
public class MOHAAllocationReportJdbcMapper
        implements ReportDataMapper<MOHAAllocationReportData>, RowMapper<MOHAAllocationReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "select " +
            "           case when ah.type = 'ORGANISATION' or ah.type ='GOVERNMENT' then ah.name else CONCAT(ahr.first_name,' ',ahr.last_name) end as account_holder_name, \n" +
            "           ce.identifier as maritime_operator_id, \n" +
            "           mo.maritime_monitoring_plan_identifier, \n" +
            "           mo.imo, \n" +
            "           ce.start_year as first_year, \n" +
            "           ce.regulator, \n" +
            "           ce.end_year as last_year,\n" +
            "           case when eee.excluded is true then 'TRUE' else 'FALSE' end as excluded, \n"+
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number, \n"+
            "           case when BOOL_OR(ac.sales_contact_uka_1_99) then 'TRUE' else 'FALSE' end as sales_contact_uka_1_99, \n" +
            "           case when BOOL_OR(ac.sales_contact_uka_100_999) then 'TRUE' else 'FALSE' end as sales_contact_uka_100_999, \n" +
            "           case when BOOL_OR(ac.sales_contact_uka_1000_plus) then 'TRUE' else 'FALSE' end as sales_contact_uka_1000_plus\n" +
            "   from account_holder as ah  \n" +
            "   inner join account as ac \n" +
            "       on ah.id = ac.account_holder_id \n" +
            "   left join account_holder_representative ahr \n" +
            "       on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n" +
            "   inner join compliant_entity as ce \n" +
            "       on ac.compliant_entity_id = ce.id \n" +
            "   inner join maritime_operator as mo \n" +
            "       on ce.id = mo.compliant_entity_id \n" +
            "   left join exclude_emissions_entry eee " +
            "       on eee.compliant_entity_id = ce.identifier and eee.year = ? \n"+
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
            "           ce.end_year, \n" +
            "           eee.excluded, \n"+
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number \n"+
            "order by ah.name,ce.identifier \n";

    @Override
    public List<MOHAAllocationReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this, reportQueryInfo.getYear());
    }

    @Override
    public MOHAAllocationReportData mapRow(ResultSet resultSet, int i) throws SQLException {

        return MOHAAllocationReportData.builder()
                .accountHolderName(resultSet.getString("account_holder_name"))
                .maritimeOperatorId(resultSet.getString("maritime_operator_id"))
                .imo(resultSet.getString("imo"))
                .maritimeMonitoringPlanId(resultSet.getString("maritime_monitoring_plan_identifier"))
                .excludedForSchemeYear("TRUE".equals(resultSet.getString("excluded")) ? "YES" : "")
                .firstYear(resultSet.getInt("first_year"))
                .lastYear(Util.getNullableInteger(resultSet, "last_year"))
                .regulator(resultSet.getString("regulator"))
                .excludedForSchemeYear("TRUE".equals(resultSet.getString("excluded")) ? "YES" : "")
                .salesContactEmail(resultSet.getString("sales_contact_email"))
                .salesContactPhone(StringUtils.isNotBlank(resultSet.getString("sales_contact_phone_number_country")) ?  StringUtils.trim(resultSet.getString("sales_contact_phone_number_country") + " " +resultSet.getString("sales_contact_phone_number")) : "")
                .uka1To99(resultSet.getString("sales_contact_uka_1_99"))
                .uka100To999(resultSet.getString("sales_contact_uka_100_999"))
                .uka1000Plus(resultSet.getString("sales_contact_uka_1000_plus"))
                .build();
    }
}
