package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class SeniorRegistryAdminCanOnlyAssignToSeniorOrJuniorAdminRule extends AbstractTaskBusinessRule {

    public SeniorRegistryAdminCanOnlyAssignToSeniorOrJuniorAdminRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
                "A senior registry administrator can only assign this task to another senior or junior registry administrator.");
    }

    @Override
    public Outcome permit() {
        boolean assignorIsSeniorRegistryAdministrator =
                userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        boolean assigneeIsSeniorOrJuniorRegistryAdministrator = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        boolean assigneeIsAuthorizedRepresentative = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isAuthorizedRepresentative);
        boolean isAccountHolderTask = slice.getTaskBusinessRuleInfoList().stream()
                .anyMatch(task -> task.getTask().getType().equals(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD));
        if (assignorIsSeniorRegistryAdministrator && !assigneeIsSeniorOrJuniorRegistryAdministrator && !isAccountHolderTask) {
            return forbiddenOutcome();
        } else if (assignorIsSeniorRegistryAdministrator && assigneeIsAuthorizedRepresentative && isAccountHolderTask) {
            return Outcome.PERMITTED_OUTCOME;
        } else {
            return Outcome.PERMITTED_OUTCOME;
        }
    }
}