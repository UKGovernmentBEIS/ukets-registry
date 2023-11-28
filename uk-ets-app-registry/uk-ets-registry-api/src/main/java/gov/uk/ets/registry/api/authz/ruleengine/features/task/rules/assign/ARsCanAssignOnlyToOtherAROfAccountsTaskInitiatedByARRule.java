package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;


/**
 * This rule is in conflict with the filtering of the tasks rule see UKETS-1544 and UKETS-1558 so it cannot be
 * property used for now.
 */
public class ARsCanAssignOnlyToOtherAROfAccountsTaskInitiatedByARRule extends AbstractTaskBusinessRule {

    public ARsCanAssignOnlyToOtherAROfAccountsTaskInitiatedByARRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Outcome permit() {

        // TODO implement me

        return Outcome.NOT_APPLICABLE_OUTCOME;
    }
}
