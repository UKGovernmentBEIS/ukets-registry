package gov.uk.ets.reports.api.roleaccess.service;

import gov.uk.ets.reports.model.ReportRequestingRole;
import gov.uk.ets.reports.model.ReportType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Setter
@Log4j2
@PropertySource({"classpath:reportsPerRole.properties","classpath:application.properties"})
@ConfigurationProperties(prefix = "report.per")
public class ReportTypesPerRoleService {

    private Map<ReportType, Map<String, String>> role;

    /**
     * Checks whether the requesting role has access to the requested report type.
     */
    public boolean availableTypesForRole(ReportType type, ReportRequestingRole requestingRole) {
        return ReportRequestingRole.administrator.equals(requestingRole) ? reportTypesForRole("administrators").contains(type)
                : ReportRequestingRole.authority.equals(requestingRole) ? reportTypesForRole("authorities").contains(type)
                : false;
    }

    /**
     * Returns a list of report types that are available to the requesting role.
     */
    public List<ReportType> getReportTypes(ReportRequestingRole requestingRole) {
        return ReportRequestingRole.administrator.equals(requestingRole) ? reportTypesForRole("administrators")
            : ReportRequestingRole.authority.equals(requestingRole) ? reportTypesForRole("authorities")
            : Collections.emptyList();
    }

    private List<ReportType> reportTypesForRole(String requestingRole) {
        return role.entrySet().stream()
                .filter(x -> x.getValue().get("request").contains(requestingRole))
                .map(y -> y.getKey())
                .collect(Collectors.toList());
    }
}
