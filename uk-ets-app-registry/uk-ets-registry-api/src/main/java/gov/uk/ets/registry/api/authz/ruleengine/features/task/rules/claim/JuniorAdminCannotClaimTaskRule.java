package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.user.domain.UserRole;

public class JuniorAdminCannotClaimTaskRule extends AbstractTaskBusinessRule {

    public JuniorAdminCannotClaimTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "A junior registry administrator cannot claim this task.");
    }

    @Override
    public Outcome permit() {
        boolean isRegistryAdministratorJunior = userRoles.stream().anyMatch(UserRole::isJuniorAdministrator);
        if (isRegistryAdministratorJunior) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
