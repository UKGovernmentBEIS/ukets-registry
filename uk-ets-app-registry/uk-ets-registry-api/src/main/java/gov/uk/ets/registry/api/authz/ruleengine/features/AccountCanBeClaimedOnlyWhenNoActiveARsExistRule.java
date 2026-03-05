package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.AbstractAccountActionBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class AccountCanBeClaimedOnlyWhenNoActiveARsExistRule extends AbstractAccountActionBusinessRule {

    public AccountCanBeClaimedOnlyWhenNoActiveARsExistRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
                 .from("The Account Claim process cannot proceed because either there are ARs already associated with the account, or there is a pending addition of an AR to the account.");
    }

    @Override
    public Outcome permit() {
        Long nonAdminActiveRights = account.
            getAccountAccesses().
            stream().
            filter(t -> !AccountAccessRight.ROLE_BASED.equals(t.getRight())).
            filter(t -> AccountAccessState.ACTIVE.equals(t.getState())).count();

        if (nonAdminActiveRights > 0) {
            return forbiddenOutcome();
        }
        
        return Outcome.PERMITTED_OUTCOME;
    }

}
