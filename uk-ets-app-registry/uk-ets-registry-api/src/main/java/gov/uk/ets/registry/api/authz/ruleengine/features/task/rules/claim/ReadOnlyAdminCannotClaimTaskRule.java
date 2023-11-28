package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class ReadOnlyAdminCannotClaimTaskRule extends AbstractTaskBusinessRule {

    public ReadOnlyAdminCannotClaimTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "A read-only registry administrator cannot claim this task.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministratorReadOnly = userRoles.stream().anyMatch(UserRole::isReadOnlyAdministrator);
        if (isRegistryAdministratorReadOnly) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
