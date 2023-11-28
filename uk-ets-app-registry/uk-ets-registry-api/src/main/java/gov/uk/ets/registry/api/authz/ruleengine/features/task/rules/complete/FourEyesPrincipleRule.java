package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;

/**
 * 4-eyes principle rule. The task must be approved by a user other than the one who initiated it.
 */
public class FourEyesPrincipleRule extends AbstractTaskBusinessRule {

    public FourEyesPrincipleRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * Refer to {@link BusinessRule#error()}.
     */
    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("You cannot approve a task initiated by you. The 4-eyes security principle applies to this task.");
    }

    /**
     * Returns the permission result.
     *
     * @return The permission result
     */
    @Override
    public Outcome permit() {
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskOutcome().equals(TaskOutcome.APPROVED) &&
            user.getId().equals(slice.getTaskBusinessRuleInfoList().get(0).getTask().getInitiatedBy().getId())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
