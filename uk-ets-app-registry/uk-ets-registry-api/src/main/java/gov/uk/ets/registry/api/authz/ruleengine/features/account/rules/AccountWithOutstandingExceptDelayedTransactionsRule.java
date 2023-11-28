package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * The account cannot be closed as there are outstanding transactions (except delayed).
 */
public class AccountWithOutstandingExceptDelayedTransactionsRule extends AbstractAccountActionBusinessRule {

    public AccountWithOutstandingExceptDelayedTransactionsRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("The account cannot be closed as there are outstanding transactions.");
    }

    @Override
    public BusinessRule.Outcome permit() {

        if (getSlice().getActiveTransactions() > 0) {
            return forbiddenOutcome();
        }
        return BusinessRule.Outcome.PERMITTED_OUTCOME;
    }
}
