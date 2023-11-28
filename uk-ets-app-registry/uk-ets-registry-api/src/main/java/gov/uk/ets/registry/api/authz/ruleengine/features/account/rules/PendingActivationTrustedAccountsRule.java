package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class PendingActivationTrustedAccountsRule extends AbstractAccountActionBusinessRule {

    public PendingActivationTrustedAccountsRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("The account cannot be closed as there are pending activation trusted accounts.");
    }

    @Override
    public BusinessRule.Outcome permit() {

        if (getSlice().getPendingActivationTrustedAccounts() > 0) {
            return forbiddenOutcome();
        }
        return BusinessRule.Outcome.PERMITTED_OUTCOME;
    }
}