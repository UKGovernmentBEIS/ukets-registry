package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.Util;
import gov.uk.ets.reports.generator.domain.AllocationReturnsReportData;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AllocationReturnsJdbcMapper implements ReportDataMapper<AllocationReturnsReportData>,
    RowMapper<AllocationReturnsReportData> {

    private static final String REGULATORS_CHECK = " and ce.regulator in (:regulators) ";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private String createReportQuery(String appendCheck) {
        return "select ce.regulator,\n" +
               " ah.name as account_holder_name,\n" +
               " COALESCE(i.permit_identifier, ao.monitoring_plan_identifier, mo.maritime_monitoring_plan_identifier) as permit_or_monitoring_plan_id,\n" +
               " ce.id as operator_id,\n" +
               " i.installation_name,\n" +
               " cast(t.attributes as json) ->> 'AllocationType' as allocation_type,\n" +
               " cast(t.attributes as json) ->> 'AllocationYear' as allocation_year,\n" +
               " t.quantity,\n" +
               " t.identifier as transaction_id,\n" +
               " t.last_updated\n" +
               " from transaction t\n" +
               " join account a on  a.identifier = t.transferring_account_identifier\n" +
               " join account_holder ah on a.account_holder_id = ah.id\n" +
               " join compliant_entity ce on ce.id = a.compliant_entity_id\n" +
               " left join installation i on ce.id = i.compliant_entity_id \n" +
               " left join aircraft_operator ao on ce.id = ao.compliant_entity_id\n" +
               " left join maritime_operator mo on ce.id = mo.compliant_entity_id\n" +
               " where t.type = 'ExcessAllocation'\n" +
               " and t.status = 'COMPLETED'\n" +
               appendCheck +
               " and t.last_updated between :from AND :to\n" +
               " order by t.last_updated desc";
    }

    @Override
    public List<AllocationReturnsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        Date from = DateRangeUtil.getFrom(reportQueryInfo);
        Date to = DateRangeUtil.getTo(reportQueryInfo);

        String appendCheck = reportQueryInfo.getRegulators() != null ? REGULATORS_CHECK : "";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
            .addValue("from", from)
            .addValue("to", to)
            .addValue("regulators", reportQueryInfo.getRegulators());

        return jdbcTemplate.query(createReportQuery(appendCheck), namedParameters, this);
    }

    @Override
    public AllocationReturnsReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AllocationReturnsReportData.builder()
            .regulator(rs.getString("regulator"))
            .accountHolderName(rs.getString("account_holder_name"))
            .permitOrMonitoringPlanId(rs.getString("permit_or_monitoring_plan_id"))
            .operatorId(rs.getLong("operator_id"))
            .installationName(rs.getString("installation_name"))
            .allocationType(rs.getString("allocation_type"))
            .year(Util.getNullableInteger(rs,"allocation_year"))
            .quantity(rs.getLong("quantity"))
            .transactionId(rs.getString("transaction_id"))
            .executionDate(parseDate(rs.getString("last_updated")))
            .build();
    }
}
