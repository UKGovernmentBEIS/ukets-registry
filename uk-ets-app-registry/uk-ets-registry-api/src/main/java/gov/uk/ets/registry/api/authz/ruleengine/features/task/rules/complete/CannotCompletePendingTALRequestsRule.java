package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AbstractTaskBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;

public class CannotCompletePendingTALRequestsRule extends AbstractTaskBusinessRule {

    private final TaskBusinessSecurityStoreSlice slice;

    public CannotCompletePendingTALRequestsRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
        slice = businessSecurityStore.getTaskBusinessSecurityStoreSlice();
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
