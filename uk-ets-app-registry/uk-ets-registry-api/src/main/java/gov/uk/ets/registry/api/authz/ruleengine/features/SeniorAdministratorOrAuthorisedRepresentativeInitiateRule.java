package gov.uk.ets.registry.api.authz.ruleengine.features;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORISED_REPRESENTATIVE;
import static gov.uk.ets.registry.api.user.domain.UserRole.SENIOR_REGISTRY_ADMINISTRATOR;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AbstractTransactionBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Optional;

/**
 * Rule: A user can perform this action only if he is a senior administrator or an authorised representative on the account with the 'Initiate' access right.
 */
public class SeniorAdministratorOrAuthorisedRepresentativeInitiateRule extends AbstractTransactionBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input
     */
    public SeniorAdministratorOrAuthorisedRepresentativeInitiateRule(BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from("A user can perform this action only if he is a senior administrator or an authorised representative on the account with the 'Initiate' access right.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Outcome permit() {
        Outcome result = Outcome.PERMITTED_OUTCOME;

        if (!hasRole(SENIOR_REGISTRY_ADMINISTRATOR, AUTHORISED_REPRESENTATIVE)) {
            result = forbiddenOutcome();

        } else if (account == null) {
            result = Outcome.NOT_APPLICABLE_OUTCOME;

        } else if (hasRole(AUTHORISED_REPRESENTATIVE)) {
            boolean permitted = isAuthorisedRepresentative(AccountAccessState.ACTIVE, AccountAccessRight.INITIATE) ||
                validTransactionForSurrenderAuthorisedRepresentative();
            result = permitted ? Outcome.PERMITTED_OUTCOME : forbiddenOutcome();
        }

        return result;
    }

    private boolean validTransactionForSurrenderAuthorisedRepresentative() {
        Optional<TransactionType> transactionType = Optional.ofNullable(getSlice())
            .map(TransactionBusinessSecurityStoreSlice::getTransactionType);

        if (transactionType.isEmpty() || transactionType.get().isOptionAvailableToSurrenderAR()) {
            return isAuthorisedRepresentative(AccountAccessState.ACTIVE, AccountAccessRight.SURRENDER_INITIATE_AND_APPROVE);
        }

        return false;
    }
}
