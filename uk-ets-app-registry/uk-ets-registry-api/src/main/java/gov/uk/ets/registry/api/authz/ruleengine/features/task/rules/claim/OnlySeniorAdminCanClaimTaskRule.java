package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class OnlySeniorAdminCanClaimTaskRule extends AbstractTaskBusinessRule {

    public OnlySeniorAdminCanClaimTaskRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("Only a Senior registry administrator can claim this task.");
    }

    @Override
    public Outcome permit() {
        boolean isSeniorRegistryAdministrator = userRoles.stream().anyMatch(UserRole::isSeniorRegistryAdministrator);
        if (!isSeniorRegistryAdministrator) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}