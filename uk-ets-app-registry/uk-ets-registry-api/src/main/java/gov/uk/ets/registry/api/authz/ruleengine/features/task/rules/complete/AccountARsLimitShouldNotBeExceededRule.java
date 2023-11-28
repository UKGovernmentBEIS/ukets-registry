package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static gov.uk.ets.registry.api.account.domain.types.AccountAccessState.ACTIVE;
import static gov.uk.ets.registry.api.account.domain.types.AccountAccessState.SUSPENDED;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;

public class AccountARsLimitShouldNotBeExceededRule extends AbstractTaskBusinessRule {

    private int maxNumOfARs;

    public AccountARsLimitShouldNotBeExceededRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        maxNumOfARs = businessSecurityStore.getMaxNumOfARs();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            String.format("The account has reached the maximum number of %s Authorised Representatives.", maxNumOfARs));
    }

    @Override
    public Outcome permit() {

        long numberOfARs = account.getAccountAccesses().stream()
            .filter(aa -> !AccountAccessRight.ROLE_BASED.equals(aa.getRight()) && (ACTIVE.equals(aa.getState()) || SUSPENDED.equals(aa.getState()))).count();
        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();

        if (getSlice().getTaskOutcome().equals(TaskOutcome.APPROVED) && RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST.equals(task.getType()) &&
            numberOfARs >= maxNumOfARs) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}