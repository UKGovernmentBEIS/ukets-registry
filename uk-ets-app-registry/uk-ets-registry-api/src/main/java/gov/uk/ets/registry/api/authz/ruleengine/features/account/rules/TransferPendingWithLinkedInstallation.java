package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;

public class TransferPendingWithLinkedInstallation extends AbstractBusinessRule {
    public TransferPendingWithLinkedInstallation(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "The account cannot be closed as it is in transfer pending status and has a linked installation");
    }

    @Override
    public Outcome permit() {
        if (AccountStatus.TRANSFER_PENDING.equals(account.getAccountStatus()) && account.getCompliantEntity() != null) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
