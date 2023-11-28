package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * The account cannot be closed as there are outstanding tasks
 */
public class AccountWithOutstandingTasksRule extends AbstractAccountActionBusinessRule {

    public AccountWithOutstandingTasksRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("The account cannot be closed as there are outstanding tasks.");
    }

    @Override
    public Outcome permit() {

        if (getSlice().getPendingTasks() > 0) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
