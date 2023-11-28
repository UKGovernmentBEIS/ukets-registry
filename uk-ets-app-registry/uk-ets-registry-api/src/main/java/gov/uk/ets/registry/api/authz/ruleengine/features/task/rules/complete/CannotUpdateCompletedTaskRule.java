package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;

public class CannotUpdateCompletedTaskRule extends AbstractTaskBusinessRule {
    public CannotUpdateCompletedTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("You cannot update a completed task.");
    }

    @Override
    public Outcome permit() {
        Task taskForUpdate = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (RequestStateEnum.APPROVED.equals(taskForUpdate.getStatus()) ||
            RequestStateEnum.REJECTED.equals(taskForUpdate.getStatus())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
