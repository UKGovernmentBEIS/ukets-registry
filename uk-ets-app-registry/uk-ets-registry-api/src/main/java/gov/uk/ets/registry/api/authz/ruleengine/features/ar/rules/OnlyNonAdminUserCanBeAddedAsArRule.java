package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Business rule that checks if the AR candidate is an non-admin user.
 */
public class OnlyNonAdminUserCanBeAddedAsArRule extends AbstractARBusinessRule {
    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public OnlyNonAdminUserCanBeAddedAsArRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("The AR candidate must be an non-admin user.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        if (getSlice().getCandidateUserRoles().stream().anyMatch(UserRole::isRegistryAdministrator)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
