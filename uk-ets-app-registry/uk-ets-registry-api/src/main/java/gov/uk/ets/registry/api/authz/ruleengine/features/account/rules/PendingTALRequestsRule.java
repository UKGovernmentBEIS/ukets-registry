package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.AccountSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class PendingTALRequestsRule extends AbstractAccountActionBusinessRule {

    private final AccountSecurityStoreSlice slice;

    public PendingTALRequestsRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getAccountSecurityStoreSlice();
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("The specified account is part of a pending trusted account list update request.");
    }

    @Override
    public Outcome permit() {
        var pendingTrustedAccounts = slice.getLinkedPendingTrustedAccounts();
        if (pendingTrustedAccounts == null) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        if (!pendingTrustedAccounts.isEmpty()) {
            return forbiddenOutcome();
        }
        return Outcome.PERMITTED_OUTCOME;
    }
}
