package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A junior administrator can only assign task to another junior administrator.
 */
public class JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule extends AbstractTaskBusinessRule {

    public JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "A junior registry administrator can only assign this task to another junior registry administrator.");
    }

    @Override
    public Outcome permit() {
        boolean assignorIsJuniorRegistryAdministrator =
                userRoles.stream().anyMatch(UserRole::isJuniorAdministrator);
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        boolean assigneeIsJuniorRegistryAdministrator = slice.getTaskAssigneeRoles()
            .stream().anyMatch(UserRole::isJuniorAdministrator);
        boolean assigneeIsAuthorizedRepresentative = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isAuthorizedRepresentative);
        boolean isAccountHolderTask = slice.getTaskBusinessRuleInfoList().stream()
                .anyMatch(task -> task.getTask().getType().equals(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD));
        if (assignorIsJuniorRegistryAdministrator && !assigneeIsJuniorRegistryAdministrator && !isAccountHolderTask) {
            return forbiddenOutcome();
        } else if(assignorIsJuniorRegistryAdministrator && assigneeIsAuthorizedRepresentative && isAccountHolderTask) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return Outcome.PERMITTED_OUTCOME;
        }
    }
}
