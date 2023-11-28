package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule extends
        AbstractTaskBusinessRule {

    public ARsCanCompleteTaskOnlyForAccountsWithApproveOrInitiateAndApproveAccessRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
                .from(
                        "User can approve tasks only for the account he/she is the AR with 'Approve' or 'initiate & "
                                + "approve access rights.");
    }

    @Override
    public Outcome permit() {

        if (TaskOutcome.REJECTED.equals(getSlice().getTaskOutcome())) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }

        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }

        boolean permitted = accountAccesses.stream()
                .filter(access -> access.getUser().equals(user))
                .filter(access -> access.getState().equals(AccountAccessState.ACTIVE))
                .filter(access -> access.getAccount().equals(account))
                .anyMatch(access -> access.getRight().containsRight(AccountAccessRight.APPROVE)
                );
        if (permitted) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return forbiddenOutcome();
        }
    }
}
