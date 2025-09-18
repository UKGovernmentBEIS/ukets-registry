package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.ARsOneYearSinceLoginReportData;
import gov.uk.ets.reports.generator.domain.KeycloakUser;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
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
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ARsOneYearSinceLoginReportJdbcMapper
    implements ReportDataMapper<ARsOneYearSinceLoginReportData>, RowMapper<ARsOneYearSinceLoginReportData> {

    private final KeycloakDbService keycloakDbService;
    private final JdbcTemplate jdbcTemplate;

    private static final String REPORT_QUERY = """
        SELECT
            ah.name AS account_holder_name,
            acc.identifier AS account_id,
            acc.kyoto_account_type as account_type,
            acc.account_status,
            ce.start_year,
            ce.end_year,
            ce.status AS compliance_status,
            u.urid AS user_urid,
            u.first_name AS user_first_name,
            u.last_name AS user_last_name,
            u.state as user_status,
            u.email AS user_email,
                
            p_ahr.first_name AS primary_first_name,
            p_ahr.last_name AS primary_last_name,
            p_contact.email_address AS primary_email,
                
            a_ahr.first_name AS alternative_first_name,
            a_ahr.last_name AS alternative_last_name,
            a_contact.email_address AS alternative_email
          
        FROM account_holder ah
        left JOIN account acc ON acc.account_holder_id = ah.id
        left JOIN account_access aa ON aa.account_id = acc.id
        left JOIN users u ON aa.user_id = u.id
        left JOIN compliant_entity ce ON ce.id = acc.compliant_entity_id
                
        LEFT JOIN account_holder_representative p_ahr
            ON p_ahr.account_holder_id = ah.id
            AND p_ahr.account_contact_type = 'PRIMARY'
        LEFT JOIN contact p_contact
            ON p_contact.id = p_ahr.contact_id
                
        LEFT JOIN account_holder_representative a_ahr
            ON a_ahr.account_holder_id = ah.id
            AND a_ahr.account_contact_type = 'ALTERNATIVE'
        LEFT JOIN contact a_contact
            ON a_contact.id = a_ahr.contact_id
            
        WHERE aa.state IN ('SUSPENDED', 'ACTIVE')
          AND aa.access_right <> 'ROLE_BASED'
          AND acc.request_status != 'REQUESTED'
          AND acc.account_status NOT IN ('CLOSED', 'CLOSURE_PENDING', 'TRANSFER_PENDING');
        """;

    @Override
    public List<ARsOneYearSinceLoginReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {

        LocalDateTime currentDateTime = LocalDateTime.now();

        Map<String, KeycloakUser> userUridMap = keycloakDbService.retrieveAllUsers()
            .stream()
            .filter(user -> user.getLastLoginOn() != null && ChronoUnit.WEEKS.between(user.getLastLoginOn(), currentDateTime) > 52)
            .collect(Collectors.toMap(KeycloakUser::getUrid, user -> user));

        List<ARsOneYearSinceLoginReportData> rows = jdbcTemplate.query(REPORT_QUERY, this);
        return rows.stream()
            .filter(row -> userUridMap.containsKey(row.getUserUrid()))
            .map(row -> {
                KeycloakUser user = userUridMap.get(row.getUserUrid());
                row.setWeeksSinceRegistered(Optional.ofNullable(user.getRegisteredOn())
                    .map(registeredOn -> ChronoUnit.WEEKS.between(registeredOn, currentDateTime))
                    .orElse(null));
                row.setWeeksSinceLogin(Optional.ofNullable(user.getLastLoginOn())
                    .map(lastLoginOn -> ChronoUnit.WEEKS.between(lastLoginOn, currentDateTime))
                    .orElse(null));
                return row;
            })
            .sorted(Comparator.comparingLong((ARsOneYearSinceLoginReportData row) -> Optional.ofNullable(row.getWeeksSinceLogin()).orElse(0L))
                .reversed())
            .collect(Collectors.toList());
    }

    @Override
    public ARsOneYearSinceLoginReportData mapRow(ResultSet resultSet, int i) throws SQLException {
        return ARsOneYearSinceLoginReportData.builder()
            .accountHolderName(resultSet.getString("account_holder_name"))
            .accountId(resultSet.getString("account_id"))
            .accountType(resultSet.getString("account_type"))
            .accountStatus(resultSet.getString("account_status"))
            .firstReportingYear(resultSet.getInt("start_year"))
            .lastReportingYear(resultSet.getInt("end_year"))
            .surrenderStatus(resultSet.getString("compliance_status"))
            .userUrid(resultSet.getString("user_urid"))
            .userFirstName(resultSet.getString("user_first_name"))
            .userLastName(resultSet.getString("user_last_name"))
            .userStatus(resultSet.getString("user_status"))
            .userEmail(resultSet.getString("user_email"))
            .primaryContactFirstName(resultSet.getString("primary_first_name"))
            .primaryContactLastName(resultSet.getString("primary_last_name"))
            .primaryContactEmail(resultSet.getString("primary_email"))
            .alternativeContactFirstName(resultSet.getString("alternative_first_name"))
            .alternativeContactEmail(resultSet.getString("alternative_last_name"))
            .alternativeContactEmail(resultSet.getString("alternative_email"))
            .build();
    }
}
