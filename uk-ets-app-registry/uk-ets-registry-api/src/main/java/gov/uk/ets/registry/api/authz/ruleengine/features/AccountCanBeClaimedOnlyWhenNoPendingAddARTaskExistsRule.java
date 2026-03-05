package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.AbstractAccountActionBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRule extends AbstractAccountActionBusinessRule {

    public AccountCanBeClaimedOnlyWhenNoPendingAddARTaskExistsRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
                 .from("The Account Claim process cannot proceed because either there are ARs already associated with the account, or there is a pending addition of an AR to the account.");
    }

    @Override
    public Outcome permit() {
        
        if (getSlice().getPendingAddAuthorizedRepresentativeTasks() > 0) {
            return forbiddenOutcome();
        }
        
        return Outcome.PERMITTED_OUTCOME;
    }

}
