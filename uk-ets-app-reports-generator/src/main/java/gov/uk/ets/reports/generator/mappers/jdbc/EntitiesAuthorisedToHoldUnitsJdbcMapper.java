package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.EntitiesAuthorisedToHoldUnitsReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
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
public class EntitiesAuthorisedToHoldUnitsJdbcMapper
        implements ReportDataMapper<EntitiesAuthorisedToHoldUnitsReportData>, RowMapper<EntitiesAuthorisedToHoldUnitsReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY =
                "select distinct \n" +
                "ah.name as account_holder_name, \n" +
                "'Confidential' as contact_information \n" +
                "from account_holder ah \n" +
                "inner join account a \n" +
                "on a.account_holder_id = ah.id \n" +
                "and a.registry_account_type = 'NONE' \n" +
                "and a.account_status != 'CLOSED'   \n" +
                "left join account_holder_representative ahr \n" +
                "on ah.id = ahr.account_holder_id\n" +
                "and ahr.account_contact_type = 'PRIMARY' \n" +
                "order by ah.name asc";

    @Override
    public List<EntitiesAuthorisedToHoldUnitsReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<EntitiesAuthorisedToHoldUnitsReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public EntitiesAuthorisedToHoldUnitsReportData mapRow(ResultSet rs, int i) throws SQLException {
        return EntitiesAuthorisedToHoldUnitsReportData.builder()
                .authorisedLegalEntity(rs.getString("account_holder_name"))
                .contactInformation(rs.getString("contact_information"))
                .build();
    }
}
