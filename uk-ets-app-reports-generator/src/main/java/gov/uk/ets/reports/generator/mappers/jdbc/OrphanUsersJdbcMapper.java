package gov.uk.ets.reports.generator.mappers.jdbc;

import gov.uk.ets.reports.generator.domain.OrphanUserData;
import gov.uk.ets.reports.generator.domain.User;
import gov.uk.ets.reports.generator.domain.UserRole;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrphanUsersJdbcMapper implements ReportDataMapper<OrphanUserData>, RowMapper<OrphanUserData> {

    /**
     * selecting registry db users not linked to accounts contains administrators as well.
     */
    private static final String REPORT_QUERY =
        "select id, urid, iam_identifier\n" +
            "from users\n" +
            "where (id not in (select user_id from account_access)\n" +
            "           or id in (\n" +
            "               select user_id\n" +
            "               from account_access\n" +
            "               where access_right <> 'ROLE_BASED' and state in ('REMOVED', 'SUSPENDED', 'REJECTED')" +
            "                 and user_id not in (\n" +
            "                     select user_id\n" +
            "                     from account_access\n" +
            "                     where access_right <> 'ROLE_BASED'\n" +
            "                       and state not in ('REMOVED', 'SUSPENDED', 'REJECTED'))\n" +
            "               )\n" +
            "    )\n" +
            "  and state is not null\n" +
            "  and state not in ('UNENROLLED', 'DEACTIVATED')" +
            "order by urid";

    private final JdbcTemplate jdbcTemplate;

    private final KeycloakDbService keycloakDbService;
    private final Set<String> rolesToBeExcluded =
        Stream.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR,
                  UserRole.JUNIOR_REGISTRY_ADMINISTRATOR,
                  UserRole.READONLY_ADMINISTRATOR,
                  UserRole.AUTHORITY_USER,
                  UserRole.SYSTEM_ADMINISTRATOR)
                          .map(UserRole::getKeycloakLiteral)
                          .collect(Collectors.toSet());

    @Override
    public List<OrphanUserData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        List<OrphanUserData> query = jdbcTemplate.query(REPORT_QUERY, this);
        // get all users from keycloak with all of their roles and filter out excluded roles
        Set<String> keycloakUsersIdsAfterExcludingRoles =
            keycloakDbService.getKeycloakUsersRolesMap()
                             .entrySet()
                             .stream()
                             .filter(u -> Collections.disjoint(u.getValue(), rolesToBeExcluded))
                             .map(Map.Entry::getKey)
                             .collect(Collectors.toSet());

        // return a list that exist in both collections.
        return
            query.stream().filter(u -> keycloakUsersIdsAfterExcludingRoles.contains(u.getUser().getIamIdentifier()))
                 .collect(Collectors.toList());
    }


    @Override
    public OrphanUserData mapRow(ResultSet resultSet, int i) throws SQLException {
        return OrphanUserData.builder()
            .user(User.builder()
                .id(resultSet.getString(1))
                .urid(resultSet.getString(2))
                .iamIdentifier(resultSet.getString(3))
                .build())
            .build();
    }
}
