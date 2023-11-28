package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.types.RequestType;

public class CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccount extends AbstractAccountHolderBusinessRule {

    public CannotApplyDuplicateAccountHolderUpdateAlternativePrimaryContactDetailsOnAccount(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody
                .from("There are already one or more pending tasks for the alternative primary contact of account holder " +
                        "details for this specific Account ID");
    }

    @Override
    public Outcome permit() {

        boolean forbiddenPrimaryContactUpdates = getSlice().getTasksByAccountHolder().stream()
                .anyMatch(task -> task.getType() == RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE
                        || task.getType() == RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE
                        || task.getType() == RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD);

        if (forbiddenPrimaryContactUpdates) {
            return forbiddenOutcome();
        }

        return Outcome.PERMITTED_OUTCOME;
    }

}
