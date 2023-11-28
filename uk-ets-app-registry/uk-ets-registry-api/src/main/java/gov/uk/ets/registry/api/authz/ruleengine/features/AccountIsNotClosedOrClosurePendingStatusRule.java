package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;

/**
 * Can perform this action only when the account is not in CLOSED/CLOSURE PENDING status.
 */
public class AccountIsNotClosedOrClosurePendingStatusRule extends AbstractBusinessRule {

    public AccountIsNotClosedOrClosurePendingStatusRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
                .from("This action cannot be performed for accounts with a Closed or Closure Pending status");
    }

    @Override
    public Outcome permit() {
        if (AccountStatus.CLOSED.equals(account.getAccountStatus()) ||
        		AccountStatus.CLOSURE_PENDING.equals(account.getAccountStatus())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
