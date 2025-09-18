package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.AccountHoldersNotesReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AccountHoldersNotesReportJdbcMapper
    implements ReportDataMapper<AccountHoldersNotesReportData>, RowMapper<AccountHoldersNotesReportData> {

    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = """
        SELECT
            ah.name as account_holder_name,
            ah.identifier as account_holder_id,
            n.creation_date as creation_date,
            n.user_id as created_by,
            n.description as description,
            u.known_as as user_known_as,
            u.first_name as user_first_name,
            u.last_name as user_last_name
        FROM
            note n
        INNER JOIN
            account_holder ah
            ON ah.identifier::text = n.domain_id
        INNER JOIN
            users u
            ON u.id = n.user_id
        WHERE
            n.domain_type = 'ACCOUNT_HOLDER'
        ORDER BY
            ah.id ASC,
            n.creation_date DESC;    
        """;

    @Override
    public List<AccountHoldersNotesReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(REPORT_QUERY, this);
    }

    @Override
    public AccountHoldersNotesReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return AccountHoldersNotesReportData.builder()
            .accountHolderName(resultSet.getString("account_holder_name"))
            .accountHolderId(resultSet.getString("account_holder_id"))
            .creationDate(LocalDateTime.parse(resultSet.getString("creation_date"), inputFormatter))
            .createdBy(resultSet.getString("created_by"))
            .note(resultSet.getString("description"))
            .userKnownAs(resultSet.getString("user_known_as"))
            .userFirstName(resultSet.getString("user_first_name"))
            .userLastName(resultSet.getString("user_last_name"))
            .build();
    }
}
