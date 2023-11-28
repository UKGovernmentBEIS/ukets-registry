package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class OnlyAuthorityUserCanClaimTaskRule extends AbstractTaskBusinessRule {

    public OnlyAuthorityUserCanClaimTaskRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from(
                "Only an authority user can claim this task.");
    }

    @Override
    public Outcome permit() {
        if (!hasRole(AUTHORITY_USER)) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
