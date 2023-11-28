package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

/**
 * The account cannot be closed as there is non-zero account balance
 */
public class NonZeroAccountBalanceRule extends AbstractBusinessRule {

    public NonZeroAccountBalanceRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("The account cannot be closed as there is non-zero account balance.");
    }

    @Override
    public Outcome permit() {
        if (account.getBalance() != null && account.getBalance() > 0L) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
