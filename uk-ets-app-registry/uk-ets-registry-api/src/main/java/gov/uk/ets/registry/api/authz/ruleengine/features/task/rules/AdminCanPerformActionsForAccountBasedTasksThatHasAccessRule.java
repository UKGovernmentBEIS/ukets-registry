package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.JUNIOR_REGISTRY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.READONLY_ADMINISTRATOR;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;

import java.util.List;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;

/**
 * An administrator can interact with tasks that has access.
 */
public class AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule extends AbstractTaskBusinessRule {

    public AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An administrator can interact with tasks that has access.");
    }

    @Override
    public Outcome permit() {
        boolean isAdmin = hasRole(SENIOR_REGISTRY_ADMINISTRATOR, JUNIOR_REGISTRY_ADMINISTRATOR,
            READONLY_ADMINISTRATOR);
        if (!isAdmin) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        for (TaskBusinessRuleInfo taskBusinessRuleInfo : slice.getTaskBusinessRuleInfoList()) {
            Account accountOnTask = getAccountOnTask(taskBusinessRuleInfo);
            if (accountOnTask == null) {
                return Outcome.NOT_APPLICABLE_OUTCOME;
            }
            if (!isAllocationRequest(taskBusinessRuleInfo) &&
                !isReversalTransaction(taskBusinessRuleInfo) &&
                !hasAccessToAccountRelatedTask(accountOnTask, false)) {
                return forbiddenOutcome();
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    private boolean isAllocationRequest(TaskBusinessRuleInfo ruleInfo) {
        return RequestType.ALLOCATION_REQUEST.equals(ruleInfo.getTask().getType());
    }

    private boolean isReversalTransaction(TaskBusinessRuleInfo ruleInfo) {
        if (getTransactionOnTask(ruleInfo) != null) {
            List<TransactionType> types = getTransactionOnTask(ruleInfo).stream().map(t -> t.getType()).toList();
            boolean result = true;
            for(TransactionType type:types) {
                result = result && TransactionType.isReversalTransaction(type);
            }
            return result;
        }
        return false;
    }
}



