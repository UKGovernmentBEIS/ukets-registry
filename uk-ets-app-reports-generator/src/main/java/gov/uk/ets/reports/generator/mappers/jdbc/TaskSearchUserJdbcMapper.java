package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.Account;
import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.Task;
import gov.uk.ets.reports.generator.domain.TaskSearchUserReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@Log4j2
public class TaskSearchUserJdbcMapper
    implements ReportDataMapper<TaskSearchUserReportData>, RowMapper<TaskSearchUserReportData> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<TaskSearchUserReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(reportQueryInfo.getQuery(), this).stream()
                .sorted(Comparator.comparing(t -> t.getTask().getTaskId()))
                .collect(Collectors.toList());
    }

    @Override
    public TaskSearchUserReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return
                TaskSearchUserReportData.builder()
                .task(Task.builder()
                    .taskId(resultSet.getLong(1))
                    .taskType(resultSet.getString(2))
                    .initiatorName(resultSet.getString(3))
                    .claimantName(resultSet.getString(6))
                    .authorisedRepresentative(resultSet.getString(19))
                    .createdOn(parseDate(resultSet.getString(17)))
                    .completedOn(parseDate(resultSet.getString(22)))
                    .taskStatus(resultSet.getString(18))
                    .transactionId(resultSet.getString(16))
                    .build())
                .account(Account.builder()
                    .number(resultSet.getString(8))
                    .type(StringUtils.hasLength(resultSet.getString(11)) ? resultSet.getString(11) : resultSet.getString(24))
                    .build())
                .accountHolder(AccountHolder.builder()
                    .name(resultSet.getString(13))
                    .build())
                .build();
    }
}
