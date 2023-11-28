package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ARsCanApproveTaskWhenAccountHasSpecificStatusRule extends AbstractTaskBusinessRule {

    public ARsCanApproveTaskWhenAccountHasSpecificStatusRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "An AR cannot approve updates on accounts with status CLOSED or CLOSURE PENDING or SUSPENDED or TRANSFER PENDING or " +
                    "ALL TRANSACTIONS RESTRICTED.");
    }

    @Override
    public Outcome permit() {
        boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
        if (!isAR) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        if (getSlice().getTaskOutcome().equals(TaskOutcome.APPROVED) &&
            (account.getAccountStatus().equals(AccountStatus.CLOSED)
                || account.getAccountStatus().equals(AccountStatus.CLOSURE_PENDING)
                || account.getAccountStatus().equals(AccountStatus.SUSPENDED)
                || account.getAccountStatus().equals(AccountStatus.TRANSFER_PENDING)
                || account.getAccountStatus().equals(AccountStatus.ALL_TRANSACTIONS_RESTRICTED))) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
