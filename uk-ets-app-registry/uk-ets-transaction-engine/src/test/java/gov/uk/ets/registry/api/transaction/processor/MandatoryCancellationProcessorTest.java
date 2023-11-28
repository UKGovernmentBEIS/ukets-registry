package gov.uk.ets.registry.api.transaction.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
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

class MandatoryCancellationProcessorTest {

    @InjectMocks
    MandatoryCancellationProcessor mandatoryCancellationProcessor;

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
        Transaction transaction = mandatoryCancellationProcessor.createInitialTransaction(transactionSummary);
        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        mandatoryCancellationProcessor.propose(transactionSummary);
        mandatoryCancellationProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

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
        Transaction transaction = mandatoryCancellationProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        mandatoryCancellationProcessor.propose(transactionSummary);
        mandatoryCancellationProcessor.cancel(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    @Test
    void testManuallyCancel() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = mandatoryCancellationProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        mandatoryCancellationProcessor.propose(transactionSummary);
        mandatoryCancellationProcessor.manuallyCancel(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    @Test
    void testReject() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = mandatoryCancellationProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        mandatoryCancellationProcessor.propose(transactionSummary);
        mandatoryCancellationProcessor.reject(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    @Test
    void testTerminate() {
        TransactionSummary transactionSummary = generateTransactionSummary();
        Transaction transaction = mandatoryCancellationProcessor.createInitialTransaction(transactionSummary);

        Assertions.assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        mandatoryCancellationProcessor.propose(transactionSummary);
        mandatoryCancellationProcessor.terminate(transaction);
        verify(unitReservationService, times(1)).releaseBlocks(any());
    }

    private TransactionSummary generateTransactionSummary(){
        String MANDATORY_CANCELLATION_ACCOUNT = "GB-250-1024-2-76";
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                1000L, true));

        Mockito.when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        Mockito.when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(MANDATORY_CANCELLATION_ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        return TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.MandatoryCancellation)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(MANDATORY_CANCELLATION_ACCOUNT)
                .blocks(blocks)
                .build();
    }
}
