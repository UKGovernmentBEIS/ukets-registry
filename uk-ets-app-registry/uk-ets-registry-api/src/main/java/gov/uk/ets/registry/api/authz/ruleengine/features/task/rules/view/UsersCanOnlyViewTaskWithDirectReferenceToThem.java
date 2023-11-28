package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * Admins can view all tasks, rest users can view tasks if they are AR on the account that relates to the task.
 */
public class UsersCanOnlyViewTaskWithDirectReferenceToThem extends AbstractTaskBusinessRule {

    public UsersCanOnlyViewTaskWithDirectReferenceToThem(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "Users cannot view tasks concerning other users.");
    }

    @Override
    public Outcome permit() {
        boolean isAdmin = userRoles.stream().anyMatch(UserRole::isRegistryAdministrator);
        if (isAdmin) {
            return Outcome.PERMITTED_OUTCOME;
        }
        Task task = getSlice().getTaskBusinessRuleInfoList().get(0).getTask();
        if (RequestType.AH_REQUESTED_DOCUMENT_UPLOAD.equals(task.getType())) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        // allow viewing tasks that are not affiliated with an account, but have a reference to specific user
        if (task.getUser() != null) {
            if (user.getId().equals(task.getUser().getId()) ||
                user.getId().equals(task.getInitiatedBy().getId())) {
                return Outcome.PERMITTED_OUTCOME;
            } else {
                return forbiddenOutcome();
            }
        }
        return Outcome.NOT_APPLICABLE_OUTCOME;
    }
}
