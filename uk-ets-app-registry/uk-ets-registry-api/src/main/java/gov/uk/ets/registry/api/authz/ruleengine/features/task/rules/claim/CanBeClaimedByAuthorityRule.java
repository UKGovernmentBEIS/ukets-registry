package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.TaskActionsByAuthorityRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * This rule is used to check if the user can claim a Central Account based task.
 */
public class CanBeClaimedByAuthorityRule extends TaskActionsByAuthorityRule {

    public CanBeClaimedByAuthorityRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("Only an authority user can claim a Central Account based task.");
    }
}
