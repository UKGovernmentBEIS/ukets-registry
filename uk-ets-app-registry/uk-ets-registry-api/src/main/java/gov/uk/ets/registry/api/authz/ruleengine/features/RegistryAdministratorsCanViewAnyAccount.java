package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A registry administrator can view all accounts in the registry.
 */
public class RegistryAdministratorsCanViewAnyAccount extends AbstractBusinessRule {

    public RegistryAdministratorsCanViewAnyAccount(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("A registry administrator can view all accounts in the registry");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (isRegistryAdministrator) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
    }
}
