package gov.uk.ets.reports.generator.export.standard;

import gov.uk.ets.reports.generator.domain.AllUsersReportData;
import gov.uk.ets.reports.generator.export.ReportTypeService;
import gov.uk.ets.reports.generator.mappers.ReportDataMapper;
import gov.uk.ets.reports.model.ReportQueryInfoWithMetadata;
import gov.uk.ets.reports.model.ReportType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AllUsersReportService implements ReportTypeService<AllUsersReportData> {

    private final ReportDataMapper<AllUsersReportData> mapper;

    @Override
    public List<AllUsersReportData> generateReportData(ReportQueryInfoWithMetadata reportQueryInfo) {
        return mapper.mapData(reportQueryInfo);
    }

    @Override
    public List<Object> getReportDataRow(AllUsersReportData reportData) {
        List<Object> data = new ArrayList<>();

        data.add(reportData.getKeycloakUser().getUrid());
        data.add(reportData.getKeycloakUser().getKeycloakUserId());
        data.add(reportData.getKeycloakUser().getCreatedOn());
        data.add(reportData.getKeycloakUser().getRegisteredOn());
        data.add(reportData.getKeycloakUser().getLastLoginOn());
        data.add(reportData.getKeycloakUser().getUsername());
        data.add(reportData.getKeycloakUser().getEmail());
        data.add(reportData.getKeycloakUser().getStatus());
        data.add(reportData.getKeycloakUser().getFirstName());
        data.add(reportData.getKeycloakUser().getLastName());
        data.add(reportData.getKeycloakUser().getAlsoKnownAs());
        data.add(reportData.getKeycloakUser().getBirthDate());
        data.add(reportData.getKeycloakUser().getCountry());
        data.add(reportData.getKeycloakUser().getCountryOfBirth());
        data.add(reportData.getKeycloakUser().getBuildingAndStreet());
        data.add(reportData.getKeycloakUser().getBuildingAndStreetOptional());
        data.add(reportData.getKeycloakUser().getBuildingAndStreetOptional2());
        data.add(reportData.getKeycloakUser().getPostCode());
        data.add(reportData.getKeycloakUser().getTownOrCity());
        data.add(reportData.getKeycloakUser().getStateOrProvince());
        data.add(reportData.getKeycloakUser().getWorkCountry());
        data.add(reportData.getKeycloakUser().getWorkMobileCountryCode());
        data.add(reportData.getKeycloakUser().getWorkMobilePhoneNumber());
        data.add(reportData.getKeycloakUser().getWorkAlternativeCountryCode());
        data.add(reportData.getKeycloakUser().getWorkAlternativePhoneNumber());
        data.add(reportData.getKeycloakUser().getWorkBuildingAndStreet());
        data.add(reportData.getKeycloakUser().getWorkBuildingAndStreetOptional());
        data.add(reportData.getKeycloakUser().getWorkBuildingAndStreetOptional2());
        data.add(reportData.getKeycloakUser().getWorkPostCode());
        data.add(reportData.getKeycloakUser().getWorkTownOrCity());
        data.add(reportData.getKeycloakUser().getWorkStateOrProvince());
        return data;
    }

    @Override
    public List<String> getReportHeaders(Long year) {
        return List.of(
            "URID",
            "Keycloak User ID",
            "Created On (UTC)",
            "Registered On (UTC)",
            "Last Login On (UTC)",
            "Username",
            "Email",
            "Status",
            "First Name",
            "Last Name",
            "Also Known As",
            "Birth Date",
            "Country",
            "Country Of Birth",
            "Address Line 1",
            "Address Line 2",
            "Address Line 3",
            "Postal Code or ZIP",
            "Town Or City",
            "State Or Province",
            "Work Country",
            "Work Mobile Country Code",
            "Work Mobile Phone Number",
            "Work Alternative Country Code",
            "Work Alternative Phone Number",
            "Work Address Line 1",
            "Work Address Line 2",
            "Work Address Line 3",
            "Work Postal Code or ZIP",
            "Work Town Or City",
            "Work State Or Province"
        );
    }

    @Override
    public ReportType reportType() {
        return ReportType.R0004;
    }
}
