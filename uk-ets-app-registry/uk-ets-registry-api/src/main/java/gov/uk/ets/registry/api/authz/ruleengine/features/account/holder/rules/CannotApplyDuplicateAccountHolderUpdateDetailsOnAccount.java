package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

public class CannotApplyDuplicateAccountHolderUpdateDetailsOnAccount extends AbstractAccountHolderBusinessRule {

    public CannotApplyDuplicateAccountHolderUpdateDetailsOnAccount(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
            .from("There are already one or more pending task to update account holder details for this specific Account ID");
    }

    @Override
    public Outcome permit() {

        boolean forbiddenAccountHolderUpdates = getSlice().getTasksByAccountHolder().stream()
            .anyMatch(task -> task.getType() == RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS);

        if (forbiddenAccountHolderUpdates) {
            return forbiddenOutcome();
        }

        return Outcome.PERMITTED_OUTCOME;
    }

}
