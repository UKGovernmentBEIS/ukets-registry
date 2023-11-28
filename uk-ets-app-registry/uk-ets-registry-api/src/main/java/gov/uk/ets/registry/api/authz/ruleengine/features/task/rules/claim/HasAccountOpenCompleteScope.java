package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class HasAccountOpenCompleteScope extends AbstractTaskBusinessRule {

    public HasAccountOpenCompleteScope(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("You do not have permission to perform this action");
    }

    @Override
    public BusinessRule.Outcome permit() {
        boolean unauthorized = !userScopes.contains(Scope.SCOPE_TASK_ACCOUNT_OPEN_COMPLETE.getScopeName());
        if (unauthorized) {
            return forbiddenOutcome();
        }
        return BusinessRule.Outcome.PERMITTED_OUTCOME;
    }
}
