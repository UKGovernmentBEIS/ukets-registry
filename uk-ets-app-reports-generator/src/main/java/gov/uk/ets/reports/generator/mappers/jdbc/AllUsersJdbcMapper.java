package gov.uk.ets.reports.generator.mappers.jdbc;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.reports.generator.domain.AllUsersReportData;
import gov.uk.ets.reports.generator.domain.KeycloakUser;
import gov.uk.ets.reports.generator.keycloak.KeycloakDbService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.criteria.ReportCriteria;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AllUsersJdbcMapper
    implements ReportDataMapper<AllUsersReportData> {

    private final KeycloakDbService keycloakDbService;


    @Override
    public List<AllUsersReportData> mapData(ReportCriteria criteria) {
        List<KeycloakUser> keycloakUsers = keycloakDbService.retrieveAllUsers();
        return keycloakUsers.stream()
            .map(u -> AllUsersReportData.builder().keycloakUser(u).build())
            .sorted(Comparator.comparing((AllUsersReportData u) -> u.getKeycloakUser().getUrid()))
            .collect(toList());
    }

    @Override
    public List<AllUsersReportData> mapData(ReportQueryInfoWithMetadata reportQueryInfo) {
        List<KeycloakUser> keycloakUsers = keycloakDbService.retrieveAllUsers();
        return keycloakUsers.stream()
            .map(u -> AllUsersReportData.builder().keycloakUser(u).build())
            .sorted(Comparator.comparing((AllUsersReportData u) -> u.getKeycloakUser().getUrid()))
            .collect(toList());
    }
}
