package gov.uk.ets.registry.api.transaction.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;
import gov.uk.ets.registry.api.transaction.service.LevelService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountBalanceService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.transaction.service.UnitCreationService;
import gov.uk.ets.registry.api.transaction.service.UnitMarkingService;
import gov.uk.ets.registry.api.transaction.service.UnitReservationService;
import gov.uk.ets.registry.api.transaction.service.UnitTransferService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class ReplacementProcessorTest {

    @InjectMocks
    private ReplacementProcessor replacementProcessor;

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
        Transaction transaction = replacementProcessor.createInitialTransaction(transactionSummary);
        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        replacementProcessor.propose(transactionSummary);
        replacementProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());
        verify(unitMarkingService, times(1)).markUnitsForReplacement(any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(1)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
        verify(transactionPersistenceService, times(1)).getUnitBlocksForReplacement(any());
    }

    @Test
    void testCancel() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = replacementProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        replacementProcessor.propose(transactionSummary);
        replacementProcessor.cancel(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
        verify(unitReservationService, times(1)).releaseBlocksForReplacement(any());
    }

    @Test
    void testManuallyCancel() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = replacementProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        replacementProcessor.propose(transactionSummary);
        replacementProcessor.manuallyCancel(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
        verify(unitReservationService, times(1)).releaseBlocksForReplacement(any());
    }

    @Test
    void testReject() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = replacementProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        replacementProcessor.propose(transactionSummary);
        replacementProcessor.reject(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
        verify(unitReservationService, times(1)).releaseBlocksForReplacement(any());
    }

    @Test
    void testTerminate() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = replacementProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        replacementProcessor.propose(transactionSummary);
        replacementProcessor.terminate(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
        verify(unitReservationService, times(1)).releaseBlocksForReplacement(any());
    }

    private TransactionSummary generateTransactionSummary(){
        String ACCOUNT = "GB-250-1024-2-76";
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ERU_FROM_AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                1000L, false));

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse(ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        return TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.Replacement)
                .transferringAccountIdentifier(1000L)
                .acquiringAccountFullIdentifier(ACCOUNT)
                .blocks(blocks)
                .itlNotification(ItlNotificationSummary.builder().notificationIdentifier(10000071L).build())
                .attributes("{\"notificationIdentifier\":10000071,\"quantity\":29,\"commitPeriod\":null,\"targetDate\":\"2021-01-14T07:38:21.787+00:00\"}")
                .build();
    }

}
