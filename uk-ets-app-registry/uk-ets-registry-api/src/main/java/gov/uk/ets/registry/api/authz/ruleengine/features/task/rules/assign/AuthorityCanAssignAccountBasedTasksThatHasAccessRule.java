package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * This rule is used to check if an authority user can assign users to tasks that has access to.
 */
public class AuthorityCanAssignAccountBasedTasksThatHasAccessRule
    extends AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule {

    public AuthorityCanAssignAccountBasedTasksThatHasAccessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An authority user can assign users to tasks that has access to.");
    }
}
