package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.Set;

/**
 * Cannot select a user while in DEACTIVATION PENDING or DEACTIVATED status.
 */
public class ForbidUsersWithDeactivationPendingOrDeactivatedRule extends AbstractBusinessRule {


    public ForbidUsersWithDeactivationPendingOrDeactivatedRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("User is in DEACTIVATION PENDING or DEACTIVATED status.");
    }

    @Override
    public Outcome permit() {
        if (requestedUser == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (Set.of(UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED).contains(requestedUser.getState())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
