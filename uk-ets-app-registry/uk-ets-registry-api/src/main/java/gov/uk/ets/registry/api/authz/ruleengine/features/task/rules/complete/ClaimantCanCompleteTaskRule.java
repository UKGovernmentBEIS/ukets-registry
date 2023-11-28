package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;

public class ClaimantCanCompleteTaskRule extends AbstractTaskBusinessRule {
    public ClaimantCanCompleteTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("You cannot complete this task as you are not the claimant of this task.");
    }

    @Override
    public Outcome permit() {
        Task taskForCompletion = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (!user.getUrid().equals(taskForCompletion.getClaimedBy().getUrid())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
