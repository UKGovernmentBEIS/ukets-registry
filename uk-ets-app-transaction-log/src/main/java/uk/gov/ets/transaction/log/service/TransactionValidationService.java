package uk.gov.ets.transaction.log.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.*;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckExecutionService;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckResult;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;

/**
 * Service for validating proposed transactions.
 */
@Service
@AllArgsConstructor
public class TransactionValidationService {

    /**
     * Service for executing business checks.
     */
    private BusinessCheckExecutionService businessCheckExecutionService;

    /**
     * Check for transferring account.
     */
    private CheckTransferringAccountExists checkTransferringAccountExists;

    /**
     * Check for acquiring account.
     */
    private CheckAcquiringAccountExists checkAcquiringAccountExists;

    /**
     * Check for available balance.
     */
    private CheckRequestedQuantityExceedsBalance checkRequestedQuantityExceedsBalance;

    /**
     * Check for issuance limits.
     */
    private CheckIssuanceLimits checkIssuanceLimits;

    /**
     * Check for serial numbers.
     */
    private CheckSerialNumbers checkSerialNumbers;

    /**
     * Check whether this transaction has already been processed by Transaction Log.
     */
    private CheckTransactionAlreadyExists transactionAlreadyExists;

    /**
     * Check whether this transaction has start date before 24 hours
     */
    private CheckForStoppedTransaction checkForStoppedTransaction;

    /**
     * Validates the proposed transaction.
     * @param transaction The transaction.
     * @return the business check result.
     */
    public BusinessCheckResult validateTransaction(TransactionNotification transaction) {

        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(transaction);

        return businessCheckExecutionService.execute(context,
            checkTransferringAccountExists, checkAcquiringAccountExists,
            checkRequestedQuantityExceedsBalance, checkIssuanceLimits, checkSerialNumbers, checkForStoppedTransaction);

    }

    /**
     * Performs preliminary checks on the proposed transaction.
     * @param transaction The transaction.
     * @return the business check result.
     */
    public BusinessCheckResult performPreliminaryChecks(TransactionNotification transaction) {

        BusinessCheckContext context = new BusinessCheckContext();
        context.setTransaction(transaction);

        return businessCheckExecutionService.execute(context, transactionAlreadyExists);

    }

}
