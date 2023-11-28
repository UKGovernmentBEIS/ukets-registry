package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ARsOfSameAccountTaskRule extends AbstractTaskBusinessRule {

    public ARsOfSameAccountTaskRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
                "The assignee must have access or be an Authorized Representative of the account.");
    }

    @Override
    public Outcome permit() {
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        boolean assigneeIsRA = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isRegistryAdministrator);
        if (assigneeIsRA) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        boolean assigneeIsAR = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isAuthorizedRepresentative);
        for (TaskBusinessRuleInfo ruleInfo: slice.getTaskBusinessRuleInfoList()) {
            Account account = getAccountOnTask(ruleInfo);
            if (account == null) {
                return Outcome.NOT_APPLICABLE_OUTCOME;
            }
            boolean assigneeBelongsToAccount;
            if (assigneeIsAR) {
                assigneeBelongsToAccount =
                    account.getAccountAccesses()
                           .stream()
                           .anyMatch(ac -> ac.getUser().getId().equals(slice.getTaskAssignee().getId())
                                           && (AccountAccessState.ACTIVE.equals(ac.getState()) ||
                                               AccountAccessState.REQUESTED.equals(ac.getState()))
                                           && !AccountAccessRight.ROLE_BASED.equals(ac.getRight()));
            } else {
                assigneeBelongsToAccount =
                    account.getAccountAccesses()
                           .stream()
                           .anyMatch(ac -> ac.getUser().getId().equals(slice.getTaskAssignee().getId())
                                           && AccountAccessState.ACTIVE.equals(ac.getState())
                                           && AccountAccessRight.ROLE_BASED.equals(ac.getRight()));
                }
            if (!assigneeBelongsToAccount) {
                return forbiddenOutcome();
            }
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}