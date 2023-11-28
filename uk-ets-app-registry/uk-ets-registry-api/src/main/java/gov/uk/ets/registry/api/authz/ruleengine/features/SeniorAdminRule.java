package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Rule: Only user with senior admin role perform this action.
 */
public class SeniorAdminRule extends AbstractBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public SeniorAdminRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only a senior registry administrator can perform this action.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        Outcome result = Outcome.PERMITTED_OUTCOME;
        if (!hasRole(SENIOR_REGISTRY_ADMINISTRATOR)) {
            result = forbiddenOutcome();
        }
        return result;
    }
}
