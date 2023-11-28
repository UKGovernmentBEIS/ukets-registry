package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * This rule is used to check if the administrator can claim tasks that has access to.
 */
public class AdminCanClaimAccountBasedTasksThatHasAccessRule
    extends AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule {

    public AdminCanClaimAccountBasedTasksThatHasAccessRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("An administrator can claim tasks that has access to.");
    }
}
