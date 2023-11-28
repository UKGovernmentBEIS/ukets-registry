package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * This rule is used to check if an administrator can assign users to tasks that has access to.
 */
public class AdminCanAssignAccountBasedTasksThatHasAccessRule
    extends AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule {

    public AdminCanAssignAccountBasedTasksThatHasAccessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An administrator can assign users to tasks that has access to.");
    }
}
