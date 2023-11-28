package gov.uk.ets.registry.api.common.reports;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.reports.model.ReportRequestingRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReportRoleMappingService {

    private final AuthorizationService authorizationService;

    /**
     * returns the ReportRequestingRole of the user, depending on their role
     * administrator for any admin, authority for authority users
     * and null for ARs
     */
    public ReportRequestingRole getUserReportRequestingRole() {
        if (authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            return ReportRequestingRole.administrator; 
        } else if (authorizationService.hasClientRole(UserRole.AUTHORITY_USER)) {
            return ReportRequestingRole.authority;
        } else {
            return null;
        }
    }
}
