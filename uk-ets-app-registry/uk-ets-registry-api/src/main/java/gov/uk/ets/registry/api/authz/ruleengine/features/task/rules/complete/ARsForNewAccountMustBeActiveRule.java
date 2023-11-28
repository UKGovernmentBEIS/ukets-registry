package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.Set;

/**
 * This Rule is specific for the account opening task.
 * Handles corner cases that ARs became suspended
 * after task creation and before task approval.
 */
public class ARsForNewAccountMustBeActiveRule extends AbstractTaskBusinessRule {

    private static final Set<UserStatus> activeStatuses =
        Set.of(UserStatus.REGISTERED, UserStatus.VALIDATED, UserStatus.ENROLLED);

    public ARsForNewAccountMustBeActiveRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Authorized Representatives must be active users.");
    }

    @Override
    public Outcome permit() {

        if (TaskOutcome.REJECTED == getSlice().getTaskOutcome()) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }

        boolean allActive = getSlice().getCandidateAccountARs()
            .stream()
            .map(User::getState)
            .allMatch(activeStatuses::contains);

        if (allActive) {
            return Outcome.PERMITTED_OUTCOME;
        }

        return forbiddenOutcome();
    }
}