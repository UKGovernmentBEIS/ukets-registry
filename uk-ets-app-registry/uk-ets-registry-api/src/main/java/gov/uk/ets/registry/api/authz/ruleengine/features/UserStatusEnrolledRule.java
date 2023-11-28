package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserStatus;

public class UserStatusEnrolledRule extends AbstractBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public UserStatusEnrolledRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only an Enrolled user can perform this action.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        if (UserStatus.ENROLLED.equals(user.getState())) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }
}
