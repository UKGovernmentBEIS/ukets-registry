package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class RegistryAdminCanApproveTaskWhenAccountNotClosedRule extends AbstractTaskBusinessRule {

    public RegistryAdminCanApproveTaskWhenAccountNotClosedRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
                .from(
                        "The registry administrator cannot approve updates on accounts with status CLOSED.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (!isRegistryAdministrator) {
            return Outcome.builder().result(Result.NOT_APPLICABLE).build();
        }
        if (getSlice().getTaskOutcome().equals(TaskOutcome.APPROVED) &&
                account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
