package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.SubmitDocumentsUserReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Andreas Karmenis
 * @created 18/01/2023 - 7:23 PM
 * @project uk-ets-app-reports-generator
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class SubmitDocumentsUserJdbcMapper implements ReportDataMapper<SubmitDocumentsUserReportData>,
        RowMapper<SubmitDocumentsUserReportData> {

    /**
     * The below fallback option, is going to be the case if a property fails to be set up properly.
     * 86400 minutes => (60 days x 24 hours x 60 minutes)
     */
    @Value("${minutes.to.delete.ar.uploaded.doc.files:86400}")
    private int minutes;
    private final JdbcTemplate jdbcTemplate;
    private String getReportQuery() {
        return  "SELECT DISTINCT\n" +
                "    u.urid,\n" +
                "    u.first_name,\n" +
                "    u.last_name,\n" +
                "    t.request_identifier identifier,\n" +
                "    t.initiated_date initiated,\n" +
                "    t.completed_date completed,\n" +
                "    t.status\n" +
                "    FROM task t\n" +
                "    INNER JOIN users u ON t.user_id = u.id\n" +
                "    INNER JOIN user_files uf ON uf.user_id = u.id\n" +
                "    INNER JOIN files f ON f.id = uf.id\n" +
                "    WHERE type ='AR_REQUESTED_DOCUMENT_UPLOAD'\n" +
                "    AND t.parent_task_id IS NULL\n" +
                "    AND t.status = 'APPROVED'\n" +
                "    AND t.request_identifier = f.request_identifier\n" +
                "    AND t.request_identifier IS NOT null\n" +
                "    AND t.completed_date IS NOT null\n" +
                "    AND current_timestamp >= (t.completed_date + "+minutes+" * interval '1 minute')\n" +
                "    AND f.file_name IS NOT NULL\n" +
                "    AND f.file_status = 'SUBMITTED'\n" +
                "    AND f.file_size IS NOT NULL\n" +
                "    AND CAST( TRIM (SUBSTRING ( f.file_size FROM 1 FOR (length(f.file_size)-2))) AS DECIMAL ) > 0;";
    }


    @Override
    public List<SubmitDocumentsUserReportData> mapData(ReportCriteria criteria) {
        return List.of();
    }

    @Override
    public List<SubmitDocumentsUserReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(getReportQuery(), this);
    }

    @Override
    public SubmitDocumentsUserReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SubmitDocumentsUserReportData
                .builder()
                .userId(rs.getString("urid"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .requestDocumentId(rs.getLong("identifier"))
                .taskCreationDate(parseDate(rs.getString("initiated")))
                .taskLastUpdateDate(parseDate(rs.getString("completed")))
                .taskStatus(rs.getString("status"))
                .build();
    }
}
