package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.SubmitDocumentsAccountHolderReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
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
 * @created 27/01/2023 - 9:10 PM
 * @project uk-ets-app-reports-generator
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class SubmitDocumentsAccountHolderJdbcMapper implements ReportDataMapper<SubmitDocumentsAccountHolderReportData>,
        RowMapper<SubmitDocumentsAccountHolderReportData> {

    /**
     * The below fallback option, is going to be the case if a property fails to be set up properly.
     * 86400 minutes => (60 days x 24 hours x 60 minutes)
     */
    @Value("${minutes.to.delete.ah.uploaded.doc.files:86400}")
    private int minutes;

    private final JdbcTemplate jdbcTemplate;
    private String getReportQuery() {
        return "SELECT \n" +
                "ah.identifier as accHolderId, \n" +
                "case \n" +
                "   when ah.type in ('ORGANISATION','GOVERNMENT') then ah.name \n" +
                "   else CONCAT(ah.first_name,' ', ah.last_name) \n" +
                "end as accountHolderName,\n" +
                "t.request_identifier as requestDocumentTaskId, \n" +
                "t.initiated_date as taskCreationDate, \n" +
                "t.completed_date as taskLastUpdatedDate, \n" +
                "t.status as taskStatus \n" +
                "FROM task t \n" +
                "   INNER JOIN files f ON t.request_identifier = f.request_identifier  \n" +
                "   INNER JOIN users u ON t.user_id = u.id \n" +
                "   INNER JOIN account_access aa ON u.id = aa.user_id \n" +
                "   INNER JOIN account a ON a.id = aa.account_id  \n" +
                "   INNER JOIN account_holder ah ON a.account_holder_id = ah.id \n" +
                "   INNER JOIN account_holder_files ahf ON ah.id = ahf.account_holder_id \n" +
                "WHERE t.type = 'AH_REQUESTED_DOCUMENT_UPLOAD' \n" +
                "AND f.id = ahf.id \n" +
                "AND t.status = 'APPROVED' \n" +
                "AND t.parent_task_id IS null \n" +
                "AND t.request_identifier is not null \n" +
                "AND t.completed_date IS NOT null \n" +
                "AND f.file_name IS NOT NULL \n" +
                "AND f.file_status = 'SUBMITTED' \n" +
                "AND f.file_size IS NOT NULL \n" +
                "AND CAST(TRIM(SUBSTRING ( f.file_size FROM 1 FOR (length(f.file_size)-2))) AS DECIMAL) > 0 \n" +
                "AND current_timestamp >= (t.completed_date  + "+minutes+" * interval '1 minute')\n"+
                "ORDER BY ah.identifier DESC";
    }

    @Override
    public List<SubmitDocumentsAccountHolderReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(getReportQuery(), this);
    }

    @Override
    public SubmitDocumentsAccountHolderReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return SubmitDocumentsAccountHolderReportData
                .builder()
                .accountHolderId(rs.getLong("accHolderId"))
                .accountHolderName(rs.getString("accountHolderName"))
                .requestDocumentId(rs.getLong("requestDocumentTaskId"))
                .taskCreationDate(parseDate(rs.getString("taskCreationDate")))
                .taskLastUpdateDate(parseDate(rs.getString("taskLastUpdatedDate")))
                .taskStatus(rs.getString("taskStatus"))
                .build();
    }

}
