package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class OnlySeniorRegistryAdminCanRejectTaskRule extends AbstractTaskBusinessRule {

    public OnlySeniorRegistryAdminCanRejectTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "Only a Senior registry administrator can reject this task.");
    }

    @Override
    public Outcome permit() {
        TaskBusinessSecurityStoreSlice slice = getSlice();
        if (TaskOutcome.APPROVED.equals(slice.getTaskOutcome())) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        boolean isSeniorRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
        if (!isSeniorRegistryAdministrator) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
