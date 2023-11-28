package uk.gov.ets.transaction.log.domain.type;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enumerates the various transaction types.
 */
@SuppressWarnings("java:S115")
public enum TransactionType {

    /**
     * Issuance of UK allowances.
     */
    IssueAllowances(TransactionConfiguration.builder()
        .description("Issuance of allowances")
        .primaryCode(1)
        .supplementaryCode(40)
        .build()),


    /**
     * Surrender of Allowances.
     */
    SurrenderAllowances(TransactionConfiguration.builder()
        .description("Surrender allowances")
        .primaryCode(10)
        .supplementaryCode(2)
        .build()),

    /**
     * Inbound transfer.
     */
    InboundTransfer(TransactionConfiguration.builder()
        .description("Inbound transfer")
        .primaryCode(10)
        .supplementaryCode(100)
        .build()),

    /**
     * Central transfer of allowances.
     */
    CentralTransferAllowances(TransactionConfiguration.builder()
        .description("Central transfer")
        .primaryCode(10)
        .supplementaryCode(51)
        .build()),

    /**
     * Transfer of allowances.
     */
    TransferAllowances(TransactionConfiguration.builder()
        .description("Transfer allowances")
        .primaryCode(10)
        .supplementaryCode(50)
        .build()),

    /**
     * Closure transfer of allowances.
     */
    ClosureTransfer(TransactionConfiguration.builder()
        .description("Closure transfer")
        .primaryCode(10)
        .supplementaryCode(91)
        .build()),
    
    AllocateAllowances(TransactionConfiguration.builder()
        .description("Allocation of allowances")
        .primaryCode(10)
        .supplementaryCode(40)
        .build()),

    /**
     * Transfer of allowances to auction delivery account.
     */
    AuctionDeliveryAllowances(TransactionConfiguration.builder()
        .description("Transfer of allowances to auction delivery account")
        .primaryCode(10)
        .supplementaryCode(37)
        .build()),

    ReverseAllocateAllowances(TransactionConfiguration.builder()
        .description("Reversal of allocation of allowances")
        .primaryCode(10)
        .supplementaryCode(41)
        .build()),

    /**
     * Reversal of surrender of allowances.
     */
    ReverseSurrenderAllowances(TransactionConfiguration.builder()
        .description("Reversal of surrender of allowances")
        .primaryCode(10)
        .supplementaryCode(82)
        .build()),

    ExcessAllocation(TransactionConfiguration.builder()
        .description("Return of excess allocation")
        .primaryCode(10)
        .supplementaryCode(42)
        .build()),

    /**
     * Return of excess auction.
     */
    ExcessAuction(TransactionConfiguration.builder()
        .description("Return of excess auction")
        .primaryCode(10)
        .supplementaryCode(43)
        .build()),

    /**
     * Balance installation transfer of allowances.
     */
    BalanceInstallationTransferAllowances(TransactionConfiguration.builder()
        .description("Balance installation transfer allowances")
        .primaryCode(10)
        .supplementaryCode(99)
        .build()),

    /**
     * Deletion of Allowances.
     */
    DeletionOfAllowances(TransactionConfiguration.builder()
        .description("Deletion of allowances")
        .primaryCode(10)
        .supplementaryCode(90)
        .build()),

    /**
     * Reversal of deletion of Allowances.
     */
    ReverseDeletionOfAllowances(TransactionConfiguration.builder()
        .description("Reversal of deletion of allowances")
        .primaryCode(10)
        .supplementaryCode(190)
        .build());

    /**
     * The transaction configuration.
     */
    private final TransactionConfiguration transactionConfiguration;

    /**
     * Constructor.
     *
     * @param transactionConfiguration The transaction configuration.
     */
    TransactionType(TransactionConfiguration transactionConfiguration) {
        this.transactionConfiguration = transactionConfiguration;
    }

    /**
     * Returns the primary code.
     *
     * @return the primary code.
     */
    public Integer getPrimaryCode() {
        return transactionConfiguration.getPrimaryCode();
    }

    /**
     * Returns the supplementary code.
     *
     * @return the supplementary code.
     */
    public Integer getSupplementaryCode() {
        return transactionConfiguration.getSupplementaryCode();
    }

    /**
     * Returns a description of the transaction type.
     *
     * @return a description
     */
    public String getDescription() {
        return transactionConfiguration.getDescription();
    }

    /**
     * Identifies the transaction type based on the primary and supplementary codes.
     * @param primaryCode The primary code.
     * @param supplementaryCode The supplementary code.
     * @return a transaction type.
     */
    public static TransactionType of(int primaryCode, int supplementaryCode) {
        TransactionType result = null;
        Optional<TransactionType> optional = Stream.of(values())
            .filter(type ->
                type.getPrimaryCode().equals(primaryCode) &&
                type.getSupplementaryCode().equals(supplementaryCode))
            .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }
}
