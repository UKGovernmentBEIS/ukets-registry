package gov.uk.ets.registry.api.transaction.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.auditevent.domain.types.TransactionEventType;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.event.service.TransactionEventService;
import gov.uk.ets.registry.api.transaction.TransactionReversalService;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.TransactionConnection;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;
import gov.uk.ets.registry.api.transaction.service.EntitlementService;
import gov.uk.ets.registry.api.transaction.service.LevelService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountBalanceService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionDelayService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.transaction.service.UnitCreationService;
import gov.uk.ets.registry.api.transaction.service.UnitMarkingService;
import gov.uk.ets.registry.api.transaction.service.UnitReservationService;
import gov.uk.ets.registry.api.transaction.service.UnitTransferService;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Parent transaction processor.
 */
@Service
public abstract class ParentTransactionProcessor implements TransactionProcessor {

    /**
     * Service for creating unit blocks.
     */
    @Autowired
    protected UnitCreationService unitCreationService;

    /**
     * Persistence service for transactions.
     */
    @Autowired
    protected TransactionPersistenceService transactionPersistenceService;

    /**
     * Account service for transactions.
     */
    @Autowired
    protected TransactionAccountService transactionAccountService;

    /**
     * Service for reserving units.
     */
    @Autowired
    protected UnitReservationService unitReservationService;

    /**
     * Service for account holdings.
     */
    @Autowired
    protected AccountHoldingService accountHoldingService;

    /**
     * Service for entitlements.
     */
    @Autowired
    protected EntitlementService entitlementService;

    /**
     * Service for levels.
     */
    @Autowired
    protected LevelService levelService;

    /**
     * Service for transferring units.
     */
    @Autowired
    protected UnitTransferService unitTransferService;

    /**
     * Service for marking units.
     */
    @Autowired
    protected UnitMarkingService unitMarkingService;

    /**
     * Service for calculating allocations.
     */
    @Autowired
    protected AllocationCalculationService calculationService;

    /**
     * Service for reversing transactions.
     */
    @Autowired
    protected TransactionReversalService transactionReversalService;

    /**
     * Service for running total per transaction.
     */
    @Autowired
    protected TransactionAccountBalanceService transactionAccountBalanceService;

    /**
     * Service for transaction delays.
     */
    @Autowired
    private TransactionDelayService transactionDelayService;

    /**
     * The transaction event service.
     */
    @Autowired
    private TransactionEventService transactionEventService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void propose(TransactionSummary transaction) {
        unitReservationService.reserveUnits(transaction);
        unitCreationService.createTransactionBlocks(transaction.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccountIdentifier(),transaction.getAcquiringAccountFullIdentifier());
        transactionAccountBalanceService.createTransactionAccountBalances(updateAccountBalanceResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finalise(Transaction transaction) {
        unitReservationService.releaseBlocks(transaction.getIdentifier());
        updateStatus(transaction, TransactionStatus.COMPLETED);
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.updateAccountBalances(transaction);
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void reject(Transaction transaction) {
        updateStatus(transaction, TransactionStatus.REJECTED);
        unitReservationService.releaseBlocks(transaction.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccount().getAccountFullIdentifier(),transaction.getAcquiringAccount().getAccountFullIdentifier());
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void terminate(Transaction transaction) {
        updateStatus(transaction, TransactionStatus.TERMINATED);
        unitReservationService.releaseBlocks(transaction.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccount().getAccountFullIdentifier(),transaction.getAcquiringAccount().getAccountFullIdentifier());
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void cancel(Transaction transaction) {
        updateStatus(transaction, TransactionStatus.CANCELLED);
        unitReservationService.releaseBlocks(transaction.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccount().getAccountFullIdentifier(),transaction.getAcquiringAccount().getAccountFullIdentifier());
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void manuallyCancel(Transaction transaction) {
        updateStatus(transaction, TransactionStatus.MANUALLY_CANCELLED);
        unitReservationService.releaseBlocks(transaction.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccount().getAccountFullIdentifier(),transaction.getAcquiringAccount().getAccountFullIdentifier());
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void fail(Transaction transaction) {
        updateStatus(transaction, TransactionStatus.FAILED);
        unitReservationService.releaseBlocks(transaction.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccount().getAccountFullIdentifier(),transaction.getAcquiringAccount().getAccountFullIdentifier());
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void delay(Transaction transaction) {

        boolean acquiringAccountTrusted = transactionAccountService.isTrustedAccount(
            transaction.getTransferringAccount().getAccountIdentifier(),
            transaction.getAcquiringAccount(),
            transaction.getAcquiringAccount().getAccountRegistryCode());
        LocalDateTime executionRounded  =
                transactionDelayService.calculateTransactionDelay(acquiringAccountTrusted)
                                       .truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        transaction.setExecutionDate(executionRounded);
        updateStatus(transaction, TransactionStatus.DELAYED);
        //Write a system event for the transaction status
        //timestamp should be exactly as the example: 21 May 2021 10:18am (UTC)
        LocalDateTime executionDate = transaction.getExecutionDate();
        String formattedDateTime = Utils.formatDay(executionDate) + " " +
            Utils.formatTime(executionDate) + " (UTC)";
        transactionEventService.createAndPublishEvent(transaction.getIdentifier(),
            null,
            formattedDateTime,
            transaction,
            TransactionEventType.TRANSACTION_DELAYED);

        UpdateAccountBalanceResult updateAccountBalanceResult = accountHoldingService.getCurrentAccountBalances(transaction.getIdentifier(),transaction.getLastUpdated(),transaction.getTransferringAccount().getAccountFullIdentifier(),transaction.getAcquiringAccount().getAccountFullIdentifier());
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.updateTransactionAccountBalances(updateAccountBalanceResult);

    }

    /**
     * Returns the transaction with the provided identifier.
     *
     * @param transactionIdentifier The unique transaction business identifier.
     * @return a transaction.
     */
    @Transactional
    public Transaction getTransaction(String transactionIdentifier) {
        return transactionPersistenceService.getTransaction(transactionIdentifier);
    }

    /**
     * Returns the blocks of the provided transaction.
     *
     * @param transactionIdentifier The unique transaction business identifier.
     * @return some transaction blocks.
     */
    @Transactional
    public List<TransactionBlock> getTransactionBlocks(String transactionIdentifier) {
        return transactionPersistenceService.getTransactionBlocks(transactionIdentifier);
    }

    /**
     * Returns the single block of the provided transaction.
     *
     * @param transactionIdentifier The unique transaction business identifier.
     * @return a single transaction block.
     */
    @Transactional
    public TransactionBlock getTransactionBlock(String transactionIdentifier) {
        return transactionPersistenceService.getTransactionBlocks(transactionIdentifier).get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Transaction createInitialTransaction(TransactionSummary transactionSummary) {
        Transaction transaction = new Transaction();
        try {
            BeanUtils.copyProperties(transaction, transactionSummary);
            transaction.setBlocks(null);
            transaction.setQuantity(transactionSummary.calculateQuantity());
            transaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
            transaction.setLastUpdated(new Date());
            transaction.setUnitType(transactionSummary.calculateUnitTypes());
            transaction.setNotificationIdentifier(transactionSummary.getItlNotification() != null ?
                    transactionSummary.getItlNotification().getNotificationIdentifier() : null);

            AccountSummary account = transactionAccountService.populateAcquiringAccount(transactionSummary);
            if (account != null) {
                AccountBasicInfo acquiringAccount = new AccountBasicInfo();
                acquiringAccount.setAccountFullIdentifier(account.getFullIdentifier());
                acquiringAccount.setAccountRegistryCode(account.getRegistryCode());
                acquiringAccount.setAccountType(account.getKyotoAccountType());
                acquiringAccount.setAccountIdentifier(account.getIdentifier());
                transaction.setAcquiringAccount(acquiringAccount);
            }

            account = transactionAccountService.populateTransferringAccount(transactionSummary);
            if (account != null) {
                AccountBasicInfo transferringAccount = new AccountBasicInfo();
                transferringAccount.setAccountFullIdentifier(account.getFullIdentifier());
                transferringAccount.setAccountRegistryCode(account.getRegistryCode());
                transferringAccount.setAccountType(account.getKyotoAccountType());
                transferringAccount.setAccountIdentifier(account.getIdentifier());
                transaction.setTransferringAccount(transferringAccount);
            }

            Map<String, Serializable> attributes = transactionSummary.getAdditionalAttributes();
            if (!CollectionUtils.isEmpty(attributes)) {
                transaction.setAttributes(new ObjectMapper().writeValueAsString(attributes));
            }

            if (!StringUtils.isEmpty(transactionSummary.getReversedIdentifier())) {
                transactionPersistenceService.save(transaction);
                Transaction reversedTransaction = transactionPersistenceService.getTransaction(transactionSummary
                    .getReversedIdentifier());
                TransactionConnection connection = new TransactionConnection();
                connection.setSubjectTransaction(transaction);
                connection.setType(TransactionConnectionType.REVERSES);
                connection.setObjectTransaction(reversedTransaction);
                connection.setDate(new Date());
                transactionPersistenceService.save(connection);
            }

        } catch (IllegalAccessException | InvocationTargetException | JsonProcessingException exc) {
            throw new TransactionExecutionException(this.getClass(), "Error when creating a new transaction", exc);
        }
        return transaction;
    }

    /**
     * Updates the status of the provided transaction.
     *
     * @param transaction          The transaction.
     * @param newTransactionStatus The transaction status.
     */
    @Transactional
    public void updateStatus(Transaction transaction, TransactionStatus newTransactionStatus) {
        transactionPersistenceService.updateStatus(transaction, newTransactionStatus);
    }

    /**
     * Updates the status of the provided transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     * @param newTransactionStatus  The transaction status.
     */
    @Transactional
    public void updateStatus(String transactionIdentifier, TransactionStatus newTransactionStatus) {
        Transaction transaction = getTransaction(transactionIdentifier);
        updateStatus(transaction, newTransactionStatus);
    }

    /**
     * Retrieves the additional attributes.
     *
     * @param attributes The transaction attributes.
     * @return the additional attributes.
     */
    protected Map<String, Serializable> getAdditionalAttributes(String attributes) {
        try {
            return new ObjectMapper().readValue(attributes, Map.class);

        } catch (JsonProcessingException exception) {
            throw new TransactionExecutionException(this.getClass(),
                "Error when de-serialising additional transaction attributes.", exception);
        }
    }

}
