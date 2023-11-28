package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule extends AbstractTaskBusinessRule {

    public OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only a Senior or Junior Registry Administrator can be assigned to this task.");
    }

    @Override
    public Outcome permit() {
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (slice.getTaskBusinessRuleInfoList().isEmpty()) {
            return forbiddenOutcome();
        }
        boolean isSeniorOrJuniorRegistryAdministrator = slice.getTaskAssigneeRoles()
                .stream().anyMatch(UserRole::isSeniorOrJuniorRegistryAdministrator);
        if (!isSeniorOrJuniorRegistryAdministrator) {
            return forbiddenOutcome();
        } else {
            return Outcome.PERMITTED_OUTCOME;
        }
    }
}
