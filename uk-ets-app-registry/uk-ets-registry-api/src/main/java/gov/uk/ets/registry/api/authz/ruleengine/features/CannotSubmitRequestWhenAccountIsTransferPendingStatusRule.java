package gov.uk.ets.registry.api.authz.ruleengine.features;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;

/**
 * Cannot submit a request when the account is in TRANSFER_PENDING status.
 */
public class CannotSubmitRequestWhenAccountIsTransferPendingStatusRule extends AbstractBusinessRule {

    public CannotSubmitRequestWhenAccountIsTransferPendingStatusRule(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("Requests cannot be made for accounts with a Transfer Pending status");
    }

    @Override
    public Outcome permit() {
        if (AccountStatus.TRANSFER_PENDING.equals(account.getAccountStatus())) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
