package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.ChangeLogReportData;
import gov.uk.ets.reports.generator.export.util.DateRangeUtil;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChangeLogJdbcMapper implements ReportDataMapper<ChangeLogReportData>, RowMapper<ChangeLogReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY =
            "select field_changed,\n" +
            "       old_value,\n" +
            "       new_value,\n" +
            "       entity,\n" +
            "       account_number,\n" +
            "       operator_id,\n" +
            "       updated_by,\n" +
            "       updated_at\n" +
            "from integration_change_log \n" +
            "where updated_at >= ? \n" +
            "  and updated_at <= ? \n" +
            "order by updated_at desc;";

    @Override
    public List<ChangeLogReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this, DateRangeUtil.getFrom(reportQueryInfo), DateRangeUtil.getTo(reportQueryInfo));
    }

    @Override
    public ChangeLogReportData mapRow(ResultSet rs, int i) throws SQLException {
        return ChangeLogReportData.builder()
                .fieldChanged(rs.getString("field_changed"))
                .oldValue(rs.getString("old_value"))
                .newValue(rs.getString("new_value"))
                .entity(rs.getString("entity"))
                .accountNumber(rs.getString("account_number"))
                .operatorId(rs.getLong("operator_id"))
                .updatedBy(rs.getString("updated_by"))
                .updatedAt(parseDate(rs.getString("updated_at")))
                .build();
    }
}