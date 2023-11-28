package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ARsCanAssignOnlyToOtherARsTaskRule extends AbstractTaskBusinessRule {

    public ARsCanAssignOnlyToOtherARsTaskRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
                "An Authorised Representative can only assign this task to another Authorised Representative of an account.");
    }

    @Override
    public Outcome permit() {
        boolean assignorIsAR =
                userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        boolean assigneeIsAR = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isAuthorizedRepresentative);
        Outcome result = Outcome.PERMITTED_OUTCOME;
        if (assignorIsAR && !assigneeIsAR) {
            return forbiddenOutcome();
        }
        return result;
    }
}