package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.AccountHolder;
import gov.uk.ets.reports.generator.domain.CompliantEntity;
import gov.uk.ets.reports.generator.domain.RegulatorNotice;
import gov.uk.ets.reports.generator.domain.RegulatorNoticeSearchReportData;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegulatorNoticeSearchJdbcMapper implements ReportDataMapper<RegulatorNoticeSearchReportData>,
        RowMapper<RegulatorNoticeSearchReportData> {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<RegulatorNoticeSearchReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return jdbcTemplate.query(reportQueryInfo.getQuery(), this).stream()
                .sorted(Comparator.comparing(t -> t.getRegulatorNotice().getTaskId()))
                .collect(Collectors.toList());
    }

    @Override
    public RegulatorNoticeSearchReportData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        /*
         * Column indexes below correspond to the SELECT statement in the QueryDSL projection.
         * Column index = position in SELECT list, starting from 1.
         * WARNING: If you add/remove/change the order of columns in the projection,
         * you MUST update these indexes accordingly, otherwise the report will break
         * (BadSqlGrammarException / incorrect data mapping).
         */
        return RegulatorNoticeSearchReportData.builder()
                .regulatorNotice(RegulatorNotice.builder()
                        .taskId(resultSet.getLong(1))                        // requestId
                        .processType(resultSet.getString(13))               // processType
                        .createdOn(parseDate(resultSet.getString(14)))     // initiatedDate
                        .claimedOn(parseDate(resultSet.getString(7)))      // claimedDate
                        .completedOn(parseDate(resultSet.getString(10)))   // completedDate
                        .claimantName(getClaimantName(
                                resultSet.getString(4),                   // claimant first name
                                resultSet.getString(5)                    // claimant last name
                        ))
                        .taskStatus(resultSet.getString(15))                // status
                        .build())
                .accountHolder(AccountHolder.builder()
                        .name(getAccountHolderName(
                                resultSet.getString(11),                   // account holder name
                                resultSet.getString(2),                    // account holder first name
                                resultSet.getString(3)                     // account holder last name
                        ))
                        .build())
                .compliantEntity(CompliantEntity.builder()
                        .installationPermitId(resultSet.getString(12))       // permit / monitoring plan
                        .aircraftMonitoringPlanId(resultSet.getString(12))
                        .maritimeMonitoringPlanId(resultSet.getString(12))
                        .build())
                .build();
    }

    private String getAccountHolderName(String name, String firstName, String lastName) {
        if (Objects.isNull(name)) {
            return firstName + " " + lastName;
        }
        return name;
    }

    private String getClaimantName(String firstName, String lastName) {
        if (Objects.isNull(lastName)) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
}
