package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * An authority user can interact with tasks that has access.
 */
public class AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule extends AbstractTaskBusinessRule {

    public AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An authority user can interact with tasks that has access.");
    }

    @Override
    public Outcome permit() {
        boolean isAuthority = hasRole(AUTHORITY_USER);
        if (!isAuthority) {
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
            if (!hasAccessToAccountRelatedTask(accountOnTask, true)) {
                return forbiddenOutcome();
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}



