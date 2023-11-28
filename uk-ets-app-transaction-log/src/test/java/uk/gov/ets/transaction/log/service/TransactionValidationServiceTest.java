package uk.gov.ets.transaction.log.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.ets.transaction.log.checks.CheckAcquiringAccountExists;
import uk.gov.ets.transaction.log.checks.CheckIssuanceLimits;
import uk.gov.ets.transaction.log.checks.CheckRequestedQuantityExceedsBalance;
import uk.gov.ets.transaction.log.checks.CheckSerialNumbers;
import uk.gov.ets.transaction.log.checks.CheckTransactionAlreadyExists;
import uk.gov.ets.transaction.log.checks.CheckTransferringAccountExists;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckExecutionService;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;

class TransactionValidationServiceTest {

    @Mock
    BusinessCheckExecutionService businessCheckExecutionService;

    @Mock
    CheckTransferringAccountExists checkTransferringAccountExists;

    @Mock
    CheckAcquiringAccountExists checkAcquiringAccountExists;

    @Mock
    CheckRequestedQuantityExceedsBalance checkRequestedQuantityExceedsBalance;

    @Mock
    CheckIssuanceLimits checkIssuanceLimits;

    @Mock
    CheckSerialNumbers checkSerialNumbers;

    @Mock
    CheckTransactionAlreadyExists checkTransactionAlreadyExists;

    @InjectMocks
    TransactionValidationService transactionValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testValidation() {
        transactionValidationService.validateTransaction(new TransactionNotification());
        verify(businessCheckExecutionService, times(1)).execute(
            any(), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    void testPreliminaryChecks() {
        transactionValidationService.performPreliminaryChecks(new TransactionNotification());
        verify(businessCheckExecutionService, times(1)).execute(
            any(), any()
        );
    }

}
