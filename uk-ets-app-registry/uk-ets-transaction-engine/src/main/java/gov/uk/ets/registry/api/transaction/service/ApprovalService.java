package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service related with the 4-eyes principle.
 */
@Service
@RequiredArgsConstructor
public class ApprovalService {

    /**
     * Service for accounts.
     */
    private final TransactionAccountService transactionAccountService;

    /**
     * Determines whether an approval is required for the provided transaction.
     *
     * @param transaction      The transaction.
     * @param hasExtendedScope Whether the initiator has extended scope.
     * @return false/true
     */
    public boolean isApprovalRequired(TransactionSummary transaction, boolean hasExtendedScope) {
        if (Constants.isInboundTransaction(transaction)) {
            return true;
        }
        if (transaction.getType().getApprovalRequired() != null) {
            return transaction.getType().getApprovalRequired();
        }
        if (hasExtendedScope) {
            return true;
        }
        if (transaction.getType().isSinglePersonApprovalRequired()) {
            return transactionAccountService.singlePersonApprovalRequired(
                transaction.getTransferringAccountIdentifier());
        }

        boolean result;

        AccountSummary acquiringAccount = transactionAccountService.populateAcquiringAccount(transaction);
        if (acquiringAccount.getType().isGovernmentAccount()) {
            //TODO For now according to UKETS-1107 if the acquiring account is government then always a task approval is required.
            // The step 4a (https://docs.ukets.net/architecture-description/0.1/design-view/transaction-engine.html) is removed for now and will be
            // revisited in UKETS-5084
//            result = belongsToApprovalRequiredTransactions(transaction.getType());
            return true;

        } else {
            boolean acquiringAccountIsTrusted =
                transactionAccountService.belongsToTrustedList(transaction, acquiringAccount);
            boolean approvalBySecondAuthorizedRepresentativeIsRequired = transactionAccountService
                .approvalOfSecondAuthorisedRepresentativeIsRequired(transaction.getTransferringAccountIdentifier());
            result = !acquiringAccountIsTrusted || approvalBySecondAuthorizedRepresentativeIsRequired;
        }

        return result;
    }

//    private static boolean belongsToApprovalRequiredTransactions(TransactionType type) {
//        return TransactionType.SurrenderAllowances.equals(type) ||
//               TransactionType.ExcessAllocation.equals(type) ||
//               TransactionType.ExcessAuction.equals(type);
//    }

}
