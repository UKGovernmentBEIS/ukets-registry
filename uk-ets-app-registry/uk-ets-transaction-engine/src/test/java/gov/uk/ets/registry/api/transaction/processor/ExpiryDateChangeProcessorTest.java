package gov.uk.ets.registry.api.transaction.processor;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.*;
import gov.uk.ets.registry.api.transaction.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ExpiryDateChangeProcessorTest {
    @InjectMocks
    private ExpiryDateChangeProcessor expiryDateChangeProcessor;

    @Mock
    UnitCreationService unitCreationService;

    @Mock
    TransactionAccountService transactionAccountService;

    @Mock
    LevelService levelService;

    @Mock
    UnitMarkingService unitMarkingService;

    @Mock
    UnitTransferService unitTransferService;

    @Mock
    UnitReservationService unitReservationService;

    @Mock
    AccountHoldingService accountHoldingService;

    @Mock
    TransactionPersistenceService transactionPersistenceService;
    
    @Mock
    TransactionAccountBalanceService transactionAccountBalanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFinalize() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = expiryDateChangeProcessor.createInitialTransaction(transactionSummary);
        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        expiryDateChangeProcessor.propose(transactionSummary);
        expiryDateChangeProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());
        verify(unitMarkingService, times(2)).changeExpireDate(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(1)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
    }

    @Test
    void testCancel() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = expiryDateChangeProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        expiryDateChangeProcessor.propose(transactionSummary);
        expiryDateChangeProcessor.cancel(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    @Test
    void testManuallyCancel() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = expiryDateChangeProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        expiryDateChangeProcessor.propose(transactionSummary);
        expiryDateChangeProcessor.manuallyCancel(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    @Test
    void testReject() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = expiryDateChangeProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        expiryDateChangeProcessor.propose(transactionSummary);
        expiryDateChangeProcessor.reject(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    @Test
    void testTerminate() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = expiryDateChangeProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        expiryDateChangeProcessor.propose(transactionSummary);
        expiryDateChangeProcessor.terminate(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    private TransactionSummary generateTransactionSummary(){
        String ACCOUNT = "GB-250-1024-2-76";
        List<TransactionBlockSummary> blocks = Arrays.asList(
                new TransactionBlockSummary(UnitType.TCER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                        1000L, true));

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse(ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        return TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.ExpiryDateChange)
                .transferringAccountIdentifier(1000L)
                .acquiringAccountFullIdentifier(ACCOUNT)
                .blocks(blocks)
                .itlNotification(ItlNotificationSummary.builder()
                        .notificationIdentifier(10000071L)
                        .build())
                .attributes("{\"notificationIdentifier\":10000071,\"quantity\":29,\"commitPeriod\":null,\"targetDate\":\"2021-01-14T07:38:21.787+00:00\"}")
                .build();
    }
}
