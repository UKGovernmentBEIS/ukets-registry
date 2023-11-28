package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;

/*
 * Business rule that checks if the transaction type is transfer of allowances to auction delivery accounts. If it
 * is, only an authority user can propose this type of transaction.
 */
public class AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority
    extends AbstractTransactionBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "Only an authority user can propose a transfer of allowances to auction delivery accounts.");
    }

    /**
     * Checks if the transaction type is a transfer of allowances to auction delivery accounts and if the user is an
     * authority user.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        TransactionBusinessSecurityStoreSlice slice = getSlice();
        Outcome result = Outcome.PERMITTED_OUTCOME;

        TransactionType transactionType = slice.getTransactionType();

        if (TransactionType.AuctionDeliveryAllowances.equals(transactionType) && !hasRole(AUTHORITY_USER)) {
            result = forbiddenOutcome();
        }
        return result;
    }
}
