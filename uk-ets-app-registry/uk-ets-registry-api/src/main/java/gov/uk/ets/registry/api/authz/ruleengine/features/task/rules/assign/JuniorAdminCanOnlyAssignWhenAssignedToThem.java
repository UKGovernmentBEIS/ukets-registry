package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A junior administrator can only re-assign a task that is assigned to him.
 */
public class JuniorAdminCanOnlyAssignWhenAssignedToThem extends AbstractTaskBusinessRule {

    public JuniorAdminCanOnlyAssignWhenAssignedToThem(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "A junior administrator can only re-assign a task that is assigned to him.");
    }

    @Override
    public Outcome permit() {
        boolean isJuniorAdmin = userRoles.stream().anyMatch(UserRole::isJuniorAdministrator);
        if (!isJuniorAdmin) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (task.getClaimedBy() != null && user.getId().equals(task.getClaimedBy().getId())) {
            return Outcome.PERMITTED_OUTCOME;
        }
        return forbiddenOutcome();
    }
}
