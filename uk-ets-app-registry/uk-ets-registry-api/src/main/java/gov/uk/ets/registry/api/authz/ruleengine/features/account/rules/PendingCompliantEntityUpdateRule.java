package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class PendingCompliantEntityUpdateRule extends AbstractAccountActionBusinessRule {

    public PendingCompliantEntityUpdateRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("There is already another pending task to update details for this Operator ID.");
    }

    @Override
    public BusinessRule.Outcome permit() {

        if (getSlice().getPendingComplianceEntityUpdate() > 0) {
            return forbiddenOutcome();
        }
        return BusinessRule.Outcome.PERMITTED_OUTCOME;
    }
}
