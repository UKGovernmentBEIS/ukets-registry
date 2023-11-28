package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * A user cannot perform an action affecting themselves.
 */
public class AffectedUserCannotPerformActionRule extends AbstractBusinessRule {

    public AffectedUserCannotPerformActionRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public Outcome permit() {
        if (user == requestedUser) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "A user can perform this action affecting only different users.");
    }
}
