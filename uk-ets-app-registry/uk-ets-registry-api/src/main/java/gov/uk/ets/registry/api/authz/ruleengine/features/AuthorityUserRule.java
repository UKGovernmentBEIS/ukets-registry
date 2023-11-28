package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * Rule: Only user with authority user role perform this action.
 */
public class AuthorityUserRule extends AbstractBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public AuthorityUserRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only an authority user can perform this action.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        Outcome result = Outcome.PERMITTED_OUTCOME;
        if (!hasRole(AUTHORITY_USER)) {
            result = forbiddenOutcome();
        }
        return result;
    }
}
