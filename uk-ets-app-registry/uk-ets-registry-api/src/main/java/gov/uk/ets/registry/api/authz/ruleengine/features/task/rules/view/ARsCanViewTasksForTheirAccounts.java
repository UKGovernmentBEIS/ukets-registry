package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserRole;

import java.util.EnumSet;
import java.util.List;

/**
 * Admins can view all tasks, rest users can view tasks if they are AR on the account that relates to the task.
 */
public class ARsCanViewTasksForTheirAccounts extends AbstractTaskBusinessRule {

    private static final List<RequestType> requestTypesProhibitedForViewByARs = RequestType.getTasksNotDisplayedToAR();

    public ARsCanViewTasksForTheirAccounts(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "A User can only view tasks for the accounts he/she is the AR.");
    }

    @Override
    public Outcome permit() {
        boolean isAdmin = userRoles.stream().anyMatch(UserRole::isRegistryAdministrator);
        if (isAdmin) {
            return Outcome.PERMITTED_OUTCOME;
        }
        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (EnumSet.of(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,RequestType.PAYMENT_REQUEST).contains(task.getType())) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        Account accountOnTask = task.getAccount();
        if (accountOnTask != null) {
            boolean isAR = userRoles.stream().anyMatch(UserRole::isAuthorizedRepresentative);
            if (!isAR && !userRoles.isEmpty()) {
                return Outcome.builder().result(Result.NOT_APPLICABLE).build();
            }

            boolean isArOnAccountRelatedToTask =
                accountAccesses.stream()
                    .filter(accountAccess -> AccountAccessState.ACTIVE.equals(accountAccess.getState()))
                        .anyMatch(a -> a.getAccount().getId().equals(accountOnTask.getId()));
            if (isArOnAccountRelatedToTask && !requestTypesProhibitedForViewByARs.contains(task.getType())) {
                return Outcome.PERMITTED_OUTCOME;
            } else {
                return forbiddenOutcome();
            }
        }
        return Outcome.NOT_APPLICABLE_OUTCOME;
    }
}
