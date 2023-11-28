package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Business rule that checks if the AR candidate is an non-admin user.
 */
public class OnlyNonAdminUsersCanBeAddedAsArRule extends AbstractBusinessRule {


    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public OnlyNonAdminUsersCanBeAddedAsArRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public Outcome permit() {
        if (requestedUserRoles.stream().anyMatch(UserRole::isRegistryAdministrator)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "The AR candidate must be an non-admin user.");
    }
}
