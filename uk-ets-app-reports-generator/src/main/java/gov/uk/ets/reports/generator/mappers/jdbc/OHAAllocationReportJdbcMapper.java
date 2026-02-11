package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.OHAAllocationReportData;
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
public class OHAAllocationReportJdbcMapper
        implements ReportDataMapper<OHAAllocationReportData>, RowMapper<OHAAllocationReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = "" +
            "select     case when ah.type = 'ORGANISATION' or ah.type ='GOVERNMENT' then ah.name else CONCAT(ahr.first_name,' ',ahr.last_name) end as account_holder_name, \n" +
            "           ce.identifier as installation_id, \n"+
            "           ins.installation_name, \n"+
            "           ins.permit_identifier, \n"+
            "           at.activity_type, \n"+
            "           ce.start_year as first_year, \n"+
            "           ce.regulator, \n"+
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number, \n"+
            "           sum(ae.entitlement) as entitlement, \n" +
            "           sum(COALESCE(ae.allocated, 0) - COALESCE(ae.returned, 0) - COALESCE(ae.reversed, 0)) as allocation, \n" +
            "           case when BOOL_OR(ac.sales_contact_uka_1_99) then 'TRUE' else 'FALSE' end as sales_contact_uka_1_99, \n" +
            "           case when BOOL_OR(ac.sales_contact_uka_100_999) then 'TRUE' else 'FALSE' end as sales_contact_uka_100_999, \n" +
            "           case when BOOL_OR(ac.sales_contact_uka_1000_plus) then 'TRUE' else 'FALSE' end as sales_contact_uka_1000_plus\n" +
            "from account_holder as ah  \n" +
            "   inner join account as ac \n" +
            "       on ah.id = ac.account_holder_id \n" +
            "   left join account_holder_representative ahr\n"+
            "       on ahr.account_holder_id = ah.id and ahr.account_contact_type = 'PRIMARY'\n"+
            "   inner join compliant_entity as ce \n" +
            "       on ac.compliant_entity_id = ce.id \n" +
            "   inner join installation as ins \n"+
            "       on ce.id = ins.compliant_entity_id \n"+
            "   inner join lateral (\n" +
            "       select compliant_entity_id, STRING_AGG(description, '; ') as activity_type from activity_type group by compliant_entity_id\n" +
            "   ) at on ins.compliant_entity_id = at.compliant_entity_id \n" +
            "   left join allocation_entry as ae \n"+
            "       on ce.id=ae.compliant_entity_id \n"+
            "   left join allocation_year as ay \n"+
            "       on ae.allocation_year_id = ay.id \n"+
            "where ay.year= ? or ay.year is null \n"+
            "group by \n"+
            "           ahr.first_name, \n"+
            "           ahr.last_name, \n"+
            "           ah.type, \n"+
            "           ah.name, \n"+
            "           ce.identifier, \n"+
            "           ins.installation_name, \n"+
            "           ins.permit_identifier, \n"+
            "           at.activity_type, \n"+
            "           ce.start_year, \n"+
            "           ce.regulator, \n"+
            "           ac.sales_contact_email, \n"+
            "           ac.sales_contact_phone_number_country, \n"+
            "           ac.sales_contact_phone_number \n"+
            "order by ah.name,ce.identifier \n";

    @Override
    public List<OHAAllocationReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this, reportQueryInfo.getYear());
    }

    @Override
    public OHAAllocationReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return OHAAllocationReportData.builder()
                .accountHolderName(resultSet.getString("account_holder_name"))
                .installationId(Util.getNullableLong(resultSet, "installation_id"))
                .installationName(resultSet.getString("installation_name"))
                .permitIdentifier(resultSet.getString("permit_identifier"))
                .activityType(resultSet.getString("activity_type"))
                .firstYear(resultSet.getInt("first_year"))
                .regulator(resultSet.getString("regulator"))
                .allocated(resultSet.getInt("allocation"))
                .entitled(resultSet.getInt("entitlement"))
                .salesContactEmail(resultSet.getString("sales_contact_email"))
                .salesContactPhone(StringUtils.isNotBlank(resultSet.getString("sales_contact_phone_number_country")) ?  StringUtils.trim(resultSet.getString("sales_contact_phone_number_country") + " " +resultSet.getString("sales_contact_phone_number")) : "")
                .uka1To99(resultSet.getString("sales_contact_uka_1_99"))
                .uka100To999(resultSet.getString("sales_contact_uka_100_999"))
                .uka1000Plus(resultSet.getString("sales_contact_uka_1000_plus"))
                .build();
    }
}
