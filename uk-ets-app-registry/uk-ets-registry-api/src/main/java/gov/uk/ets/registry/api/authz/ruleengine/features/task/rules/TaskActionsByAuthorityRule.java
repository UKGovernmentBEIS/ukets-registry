package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;

public class TaskActionsByAuthorityRule extends AbstractTaskBusinessRule {

    public TaskActionsByAuthorityRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("Only an authority user can perform Central account task actions.");
    }

    @Override
    public Outcome permit() {
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        for (TaskBusinessRuleInfo taskBusinessRuleInfo : slice.getTaskBusinessRuleInfoList()) {
            if (!checkHasCentralAccountAccess(taskBusinessRuleInfo.getTask())) {
                return forbiddenOutcome();
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    /**
     * This function checks if the account related to the task is Central account and also
     * checks the role of the user who will perform the action.
     * This will be used to block the senior registry administrator from submitting or accepting
     * transaction proposals from Central accounts.
     *
     * @param task the {@link Task}
     * @return true/false
     */
    private boolean checkHasCentralAccountAccess(Task task) {
        if (task.getAccount() != null) {
            switch (task.getAccount().getRegistryAccountType()) {
                case UK_ALLOCATION_ACCOUNT:
                case UK_TOTAL_QUANTITY_ACCOUNT:
                case UK_AUCTION_ACCOUNT:
                case UK_NEW_ENTRANTS_RESERVE_ACCOUNT:
                case UK_GENERAL_HOLDING_ACCOUNT:
                case UK_MARKET_STABILITY_MECHANISM_ACCOUNT:
                    return hasRole(AUTHORITY_USER);
                default:
                    return true;
            }
        }
        return true;
    }
}
