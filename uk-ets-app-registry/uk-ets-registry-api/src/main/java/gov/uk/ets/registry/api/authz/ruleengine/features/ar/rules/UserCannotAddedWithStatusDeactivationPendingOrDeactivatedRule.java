package gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserStatus;

/**
 * Business rule that checks if the user is DEACTIVATION PENDING or DEACTIVATED.
 */
public class UserCannotAddedWithStatusDeactivationPendingOrDeactivatedRule extends AbstractARBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public UserCannotAddedWithStatusDeactivationPendingOrDeactivatedRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("This user ID cannot be selected.");
    }

    /**
     * Checks if the user is DEACTIVATION PENDING or DEACTIVATED.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        if (requestedUser != null && (UserStatus.DEACTIVATION_PENDING.equals(requestedUser.getState()) || 
        		UserStatus.DEACTIVATED.equals(requestedUser.getState()))) {
           return forbiddenOutcome();
        }
       return Outcome.PERMITTED_OUTCOME;
    }
}
