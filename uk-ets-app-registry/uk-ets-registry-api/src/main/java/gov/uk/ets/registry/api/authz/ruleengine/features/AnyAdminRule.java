package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

import static gov.uk.ets.registry.api.user.domain.UserRole.*;

/**
 * Rule: Only user with senior, junior or system admin role perform this action.
 */
public class AnyAdminRule extends AbstractBusinessRule {
    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AnyAdminRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only an administrator can perform this action.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        Outcome result = Outcome.PERMITTED_OUTCOME;
        if (!hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR, SYSTEM_ADMINISTRATOR, READONLY_ADMINISTRATOR)) {
            result = forbiddenOutcome();
        }
        return result;
    }
}
