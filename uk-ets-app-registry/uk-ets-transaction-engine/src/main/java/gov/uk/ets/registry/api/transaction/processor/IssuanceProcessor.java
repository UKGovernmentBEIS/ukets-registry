package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public abstract class IssuanceProcessor extends ParentTransactionProcessor {

    /**
     * Reserves the issuance limit.
     *
     * @param block The transaction block.
     */
    @Transactional
    public abstract void reserveIssuanceLimit(TransactionBlockSummary block);

    /**
     * Consumes the issuance limit.
     *
     * @param block The transaction block.
     */
    @Transactional
    public abstract void consumeIssuanceLimit(TransactionBlock block);

    /**
     * Releases the issuance limit.
     *
     * @param block The transaction block.
     */
    @Transactional
    public abstract void releaseIssuanceLimit(TransactionBlock block);

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Transaction createInitialTransaction(TransactionSummary transactionSummary) {
        Transaction transaction = super.createInitialTransaction(transactionSummary);
        AccountBasicInfo acquiringAccount = transaction.getAcquiringAccount();
        if (acquiringAccount != null) {
            // In issuance of KP units, the transferring and acquiring accounts are identical.
            AccountBasicInfo transferringAccount = new AccountBasicInfo();
            transferringAccount.setAccountIdentifier(acquiringAccount.getAccountIdentifier());
            transferringAccount.setAccountFullIdentifier(acquiringAccount.getAccountFullIdentifier());
            transferringAccount.setAccountRegistryCode(acquiringAccount.getAccountRegistryCode());
            transferringAccount.setAccountType(acquiringAccount.getAccountType());
            transaction.setTransferringAccount(transferringAccount);
        }
        return transaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void propose(TransactionSummary transaction) {
        List<TransactionBlockSummary> proposedBlocks = transaction.getBlocks();
        TransactionBlockSummary block = proposedBlocks.get(0);
        unitCreationService.generateTransactionBlock(transaction, block, Constants.KYOTO_REGISTRY_CODE);
        reserveIssuanceLimit(block);
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(
                transaction.getIdentifier(),
                transaction.getLastUpdated(),
                transaction.getTransferringAccountIdentifier(),
                transaction.getAcquiringAccountIdentifier());
        transactionAccountBalanceService.createTransactionAccountBalances(updateAccountBalanceResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        List<TransactionBlock> blocks = getTransactionBlocks(transaction.getIdentifier());

        unitCreationService.createUnitBlocks(transaction.getAcquiringAccount().getAccountIdentifier(), blocks);
        updateStatus(transaction, TransactionStatus.COMPLETED);
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.updateAccountBalances(transaction);
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);
        consumeIssuanceLimit(blocks.get(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reject(Transaction transaction) {
        super.reject(transaction);
        TransactionBlock block = getTransactionBlock(transaction.getIdentifier());
        releaseIssuanceLimit(block);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void terminate(Transaction transaction) {
        super.terminate(transaction);
        TransactionBlock block = getTransactionBlock(transaction.getIdentifier());
        releaseIssuanceLimit(block);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancel(Transaction transaction) {
        super.cancel(transaction);
        TransactionBlock block = getTransactionBlock(transaction.getIdentifier());
        releaseIssuanceLimit(block);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void fail(Transaction transaction) {
        super.fail(transaction);
        TransactionBlock block = getTransactionBlock(transaction.getIdentifier());
        releaseIssuanceLimit(block);
    }

}
