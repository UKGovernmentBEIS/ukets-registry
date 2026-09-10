package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.ServiceAgentsReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceAgentsJdbcMapper implements ReportDataMapper<ServiceAgentsReportData>, RowMapper<ServiceAgentsReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY =
            "select distinct u.agent_company_name as agent_company_name,\n" +
                    "       u.agent_email_address as agent_email_address,\n" +
                    "       u.agent_phone_number as agent_phone_number,\n" +
                    "       u.agent_phone_number_country_code as agent_phone_number_country_code\n" +
                    "from users u\n" +
                    "where u.agent = 'YES_PUBLIC'\n" +
                    "  and u.state in ('VALIDATED', 'ENROLLED')\n" +
                    "order by u.agent_company_name;";

    @Override
    public List<ServiceAgentsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return
                jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public ServiceAgentsReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
                ServiceAgentsReportData.builder()
                        .companyName(resultSet.getString("agent_company_name"))
                        .email(resultSet.getString("agent_email_address"))
                        .phoneNumber(getAgentPhoneNumber(resultSet.getString("agent_phone_number_country_code"),
                                resultSet.getString("agent_phone_number")))
                        .build();
    }

    private String getAgentPhoneNumber(String phoneNumberCountryCode, String phoneNumber) {
        return StringUtils.isNotBlank(phoneNumber) ?
                StringUtils.trim(phoneNumberCountryCode + " " + phoneNumber) : "";
    }
}
