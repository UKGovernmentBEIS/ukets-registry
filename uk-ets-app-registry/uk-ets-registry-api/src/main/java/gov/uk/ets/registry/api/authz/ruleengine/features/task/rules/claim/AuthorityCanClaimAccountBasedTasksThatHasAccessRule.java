package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * This rule is used to check if the authority user can claim tasks that has access to.
 */
public class AuthorityCanClaimAccountBasedTasksThatHasAccessRule
    extends AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule {

    public AuthorityCanClaimAccountBasedTasksThatHasAccessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An authority user can claim tasks that has access to.");
    }
}
