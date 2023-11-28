package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import static gov.uk.ets.registry.api.user.domain.UserRole.AUTHORITY_USER;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;

/*
 * Business rule that checks if the transaction type is central transfer. If it is, only an authority user can propose
 * this type of transaction.
 */
public class CentralTransferCanBeProposedOnlyByAuthority
    extends AbstractTransactionBusinessRule {

    /**
     * Constructor.
     *
     * @param businessSecurityStore the business rules store input.
     */
    public CentralTransferCanBeProposedOnlyByAuthority(
        BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorBody error() {
        return ErrorBody.from(
            "Only an authority user can propose a central transfer.");
    }

    /**
     * Checks if the transaction type is central transfer and if the user is an authority user.
     * {@link Outcome} value.
     *
     * @return The {@link Outcome} outcome.
     */
    @Override
    public Outcome permit() {
        TransactionBusinessSecurityStoreSlice slice = getSlice();
        Outcome result = Outcome.PERMITTED_OUTCOME;

        TransactionType transactionType = slice.getTransactionType();

        if (TransactionType.CentralTransferAllowances.equals(transactionType)) {
            switch (account.getRegistryAccountType()) {
                case UK_TOTAL_QUANTITY_ACCOUNT:
                case UK_AUCTION_ACCOUNT:
                case UK_GENERAL_HOLDING_ACCOUNT:
                case UK_NEW_ENTRANTS_RESERVE_ACCOUNT:
                case UK_MARKET_STABILITY_MECHANISM_ACCOUNT:
                case UK_ALLOCATION_ACCOUNT:
                    if (!hasRole(AUTHORITY_USER)) {
                        result = forbiddenOutcome();
                    }
                    break;
                default:
                    result = forbiddenOutcome();
            }
        }
        return result;
    }
}
