package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

/**
 * A senior registry administrator can complete tasks where the initiator is an administrator.
 */
public class SeniorAdminCanCompleteTaskInitiatedByAdminRule
    extends AbstractTaskBusinessRule {

    public SeniorAdminCanCompleteTaskInitiatedByAdminRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "Task shall only be completed by a senior administrator since it was initiated by an administrator.");
    }

    @Override
    public Outcome permit() {
        boolean isInitiatorAdministrator =
            getSlice().getTaskAssigneeRoles().stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        boolean isUserRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
        if (isInitiatorAdministrator) {
            return isUserRegistryAdministrator ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
        }
        return Outcome.builder().result(Result.NOT_APPLICABLE).build();
    }
}
