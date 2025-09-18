package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.AccountType;
import gov.uk.ets.reports.generator.domain.Task;
import gov.uk.ets.reports.generator.domain.TaskSearchAdminReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@Log4j2
public class TaskSearchAdminJdbcMapper
    implements ReportDataMapper<TaskSearchAdminReportData>, RowMapper<TaskSearchAdminReportData> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TaskSearchAdminReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(reportQueryInfo.getQuery(), this);
    }

    @Override
    public TaskSearchAdminReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        Long ahId = retrieveLong(resultSet, 25);
        return
            TaskSearchAdminReportData.builder()
                .task(Task.builder()
                    .taskId(resultSet.getLong(1))
                    .taskType(resultSet.getString(2))
                    .initiatorName(resultSet.getString(4) != null 
                    ? concatNames(resultSet.getString(3), resultSet.getString(4)) : resultSet.getString(3))
                    .initiatorId(resultSet.getString(6))
                    .claimantName(resultSet.getString(8) != null 
                    ? concatNames(resultSet.getString(7), resultSet.getString(8)) : resultSet.getString(7))
                    .claimantId(resultSet.getString(23))
                    .authorisedRepresentative(resultSet.getString(16))
                    .createdOn(parseDate(resultSet.getString(19)))
                    .completedOn(parseDate(resultSet.getString(24)))
                    .taskStatus(resultSet.getString(20))
                    .transactionId(resultSet.getString(18))
                    .build())
                .account(Account.builder()
                    .number(resultSet.getString(10))
                    .type(StringUtils.hasLength(resultSet.getString(13)) ? AccountType.getLabel(resultSet.getString(13)) : resultSet.getString(26))
                    .build())
                .accountHolder(AccountHolder.builder()
                    .name(resultSet.getString(15))
                    .id(ahId)
                    .build())
                .build();
    }

    private String concatNames(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return "";
        }
        return firstName + " " + lastName;
    }
}
