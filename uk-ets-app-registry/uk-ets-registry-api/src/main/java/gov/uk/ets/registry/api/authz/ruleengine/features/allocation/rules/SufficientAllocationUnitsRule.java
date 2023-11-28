package gov.uk.ets.registry.api.authz.ruleengine.features.allocation.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class SufficientAllocationUnitsRule extends AbstractAllocationBusinessRule {

    public SufficientAllocationUnitsRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("There are not enough allowances in UK Allocation and/or UK NER accounts to allow execution of the present and other already scheduled allocations.");
    }

    @Override
    public Outcome permit() {

        if (getSlice() == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }

        if (getSlice().isEnoughUnitsOnAllocationAccounts()) {
            return Outcome.PERMITTED_OUTCOME;
        }

        return forbiddenOutcome();
    }
}
