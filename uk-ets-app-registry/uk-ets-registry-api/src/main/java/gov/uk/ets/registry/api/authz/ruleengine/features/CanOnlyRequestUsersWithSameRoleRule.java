package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.JUNIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.READONLY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SYSTEM_ADMINISTRATOR;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Rule: administrators can make requests only for administrators
 * authority users only for authority users
 * and users only for themselves
 */
public class CanOnlyRequestUsersWithSameRoleRule extends AbstractBusinessRule {

	/**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public CanOnlyRequestUsersWithSameRoleRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "Not authorised to make requests for these users.");
    }
    
    @Override
    public Outcome permit() {
        if (hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR, SYSTEM_ADMINISTRATOR, 
                READONLY_ADMINISTRATOR)) {
            if (!requestedUsersRoles
                    .values()
                    .stream()
                    .allMatch(v -> v.stream().anyMatch(UserRole::isRegistryAdministrator))) {
                return forbiddenOutcome();
            }
        } else if (hasRole(UserRole.AUTHORITY_USER)) {
            if (!requestedUsersRoles
                    .values()
                    .stream()
                    .allMatch(v -> v.stream().anyMatch(UserRole::isAuthority))) {
                return forbiddenOutcome();
            }
        } else {
            if (!(requestedUsers.size() == 1 && user.getUrid().equals(requestedUsers.get(0).getUrid()))) {
                return forbiddenOutcome();
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
