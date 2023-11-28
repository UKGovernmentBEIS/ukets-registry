package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

import static gov.uk.ets.registry.api.user.domain.UserRole.READONLY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;

public class SeniorOrReadOnlyAdminRule extends AbstractBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public SeniorOrReadOnlyAdminRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only a senior or read-only registry administrator can perform this action.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        Outcome result = Outcome.PERMITTED_OUTCOME;
        if (!hasRole(SENIOR_REGISTRY_ADMINISTRATOR, READONLY_ADMINISTRATOR)) {
            result = forbiddenOutcome();
        }
        return result;
    }
}
