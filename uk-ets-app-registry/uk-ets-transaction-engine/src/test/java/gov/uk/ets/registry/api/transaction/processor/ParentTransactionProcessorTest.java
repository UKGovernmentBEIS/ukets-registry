package gov.uk.ets.registry.api.transaction.processor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.service.AllocationPhaseCapService;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.auditevent.domain.types.TransactionEventType;
import gov.uk.ets.registry.api.event.service.TransactionEventService;
import gov.uk.ets.registry.api.transaction.TransactionReversalService;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ParentTransactionProcessorTest {

    @InjectMocks
    private CarryOverAAUProcessor carryOverAAUProcessor;

    @InjectMocks
    private ConversionAProcessor conversionAProcessor;

    @InjectMocks
    private CarryOverCERProcessor carryOverCERProcessor;

    @InjectMocks
    private ExternalTransferProcessor externalTransferProcessor;

    @InjectMocks
    private InternalTransferProcessor internalTransferProcessor;

    @InjectMocks
    private Art37CancellationProcessor art37CancellationProcessor;

    @InjectMocks
    private AmbitionIncreaseCancellationProcessor ambitionIncreaseCancellationProcessor;

    @InjectMocks
    private CancellationKyotoUnitsProcessor cancellationKyotoUnitsProcessor;

    @InjectMocks
    private TransferToSOPforFirstExternalTransferProcessor transferToSOPforFirstExternalTransferProcessor;

    @InjectMocks
    private IssuanceKyotoProcessor issuanceKyotoProcessor;

    @InjectMocks
    private IssuanceAllowanceProcessor issuanceAllowanceProcessor;

    @InjectMocks
    private AllocationProcessor allocationProcessor;

    @InjectMocks
    private RetirementProcessor retirementProcessor;

    @InjectMocks
    private ConversionBProcessor conversionBProcessor;

    @InjectMocks
    private TransferToSOPForConversionOfERUProcessor transferToSOPForConversionOfERUProcessor;

    @InjectMocks
    private SurrenderAllowancesProcessor surrenderAllowancesProcessor;

    @InjectMocks
    private ReverseAllocationProcessor reverseAllocationProcessor;

    @InjectMocks
    private ExcessAllocationProcessor excessAllocationProcessor;

    @InjectMocks
    private ConversionCP1Processor conversionCP1Processor;

    @InjectMocks
    private ReverseSurrenderProcessor reverseSurrenderProcessor;
    
    /**
     * Service for creating unit blocks.
     */
    @Mock
    UnitCreationService unitCreationService;

    /**
     * Persistence service for transactions.
     */
    @Mock
    TransactionPersistenceService transactionPersistenceService;

    /**
     * Account service for transactions.
     */
    @Mock
    TransactionAccountService transactionAccountService;

    /**
     * Service for reserving units.
     */
    @Mock
    UnitReservationService unitReservationService;

    /**
     * Service for account holdings.
     */
    @Mock
    AccountHoldingService accountHoldingService;

    /**
     * Service for entitlements.
     */
    @Mock
    EntitlementService entitlementService;

    /**
     * Service for levels.
     */
    @Mock
    LevelService levelService;

    /**
     * Service for transferring units.
     */
    @Mock
    UnitTransferService unitTransferService;

    /**
     * Service for marking units.
     */
    @Mock
    UnitMarkingService unitMarkingService;

    @Mock
    AllocationCalculationService allocationCalculationService;

    @Mock
    private AllocationYearCapService allocationYearCapService;

    @Mock
    private AllocationPhaseCapService allocationPhaseCapService;

    @Mock
    private AllocationConfigurationService allocationConfigurationService;

    @Mock
    TransactionReversalService transactionReversalService;

    @Mock
    TransactionAccountBalanceService transactionAccountBalanceService;

    @Mock
    private TransactionDelayService transactionDelayService;

    @Mock
    private TransactionEventService transactionEventService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCarryOverAAU() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP1, CommitmentPeriod.CP1,
                null, null, "10",
                true, null));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-130-1009-2-67", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.CarryOver_AAU)
            .transferringAccountIdentifier(10000002L)
            .blocks(blocks)
            .build();

        Transaction transaction = carryOverAAUProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        carryOverAAUProcessor.propose(transactionSummary);
        carryOverAAUProcessor.finalise(transaction);


        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(2)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(1)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
    }

    @Test
    void testCarryOverCER() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.CER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "5",
                Boolean.FALSE, "JP12345"));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.CarryOver_CER_ERU_FROM_AAU)
            .transferringAccountIdentifier(10000002L)
            .blocks(blocks)
            .build();

        Transaction transaction = carryOverCERProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        carryOverCERProcessor.propose(transactionSummary);
        carryOverCERProcessor.finalise(transaction);


        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(2)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
    }

    @Test
    void testConversionA() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "10",
                true, null));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.ConversionA)
                .transferringAccountIdentifier(1003L)
                .blocks(blocks)
                .build();

        Transaction transaction = conversionAProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        conversionAProcessor.propose(transactionSummary);
        conversionAProcessor.finalise(transaction);


        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any(), any(), any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(1)).markProjectAndConvertToERU(any(), any(), anyBoolean());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        verify(transactionAccountBalanceService, times(1)).updateTransactionAccountBalances(any());
    }

    @Test
    void testConversionB() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "10",
                true, null));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.ConversionB)
                .transferringAccountIdentifier(1003L)
                .blocks(blocks)
                .build();

        Transaction transaction = conversionBProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        conversionBProcessor.propose(transactionSummary);
        conversionBProcessor.finalise(transaction);


        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any(), any(), any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(1)).markProjectAndConvertToERU(any(), any(), anyBoolean());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
    }

    @Test
    void testExternalTransfer() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.CER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "5",
                Boolean.FALSE, "JP12345"));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .blocks(blocks)
            .build();

        Transaction transaction = externalTransferProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        externalTransferProcessor.propose(transactionSummary);
        externalTransferProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(1)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());

    }

    @Test
    void testInboundTransfer() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.CER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "5",
                Boolean.FALSE, "JP12345"));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .transferringRegistryCode("JP")
            .transferringAccountIdentifier(12345L)
            .acquiringAccountFullIdentifier("GB-100-10000002-2-99")
            .blocks(blocks)
            .build();

        Transaction transaction = externalTransferProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        externalTransferProcessor.propose(transactionSummary);
        externalTransferProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(1)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(0)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(0)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(2)).updateStatus(any(), any());

    }

    @Test
    void testArt37Cancellation() {
        String ART37_CANCELLATION_ACCOUNT = "GB-270-1017-2-28";
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                1000L, true));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(ART37_CANCELLATION_ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.Art37Cancellation)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(ART37_CANCELLATION_ACCOUNT)
                .blocks(blocks)
                .build();
        Transaction transaction = art37CancellationProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        art37CancellationProcessor.propose(transactionSummary);
        art37CancellationProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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
    void testAmbitionIncreaseCancellation() {
        String AMBITION_INCREASE_CANCELLATION_ACCOUNT = "GB-280-1018-2-30";
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                1000L, true));

                when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(AMBITION_INCREASE_CANCELLATION_ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.AmbitionIncreaseCancellation)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(AMBITION_INCREASE_CANCELLATION_ACCOUNT)
                .blocks(blocks)
                .build();
        Transaction transaction = ambitionIncreaseCancellationProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        ambitionIncreaseCancellationProcessor.propose(transactionSummary);
        ambitionIncreaseCancellationProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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
    void testRetirement() {
        String RETIREMENT_ACCOUNT = "GB-300-1022-2-24";
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                1000L, true));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(RETIREMENT_ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.AAU);
        block.setOriginatingCountryCode("GB");
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setSubjectToSop(true);
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        when(transactionPersistenceService.getTransactionBlocks(any())).thenReturn(
            Collections.singletonList(block)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.Retirement)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(RETIREMENT_ACCOUNT)
                .blocks(blocks)
                .build();
        Transaction transaction = retirementProcessor.createInitialTransaction(transactionSummary);
        when(transactionPersistenceService.getTransaction(any())).thenReturn(
                transaction
        );

        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        retirementProcessor.propose(transactionSummary);
        retirementProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(1)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(1)).consume(any(), any(), any(), any(), any());

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
    void testCancellationKyotoUnits() {
        String VOLUNTARY_CANCELLATION_ACCOUNT = "GB-230-1020-2-82";
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                1000L, true));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse(VOLUNTARY_CANCELLATION_ACCOUNT, RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.CancellationKyotoUnits)
                .transferringAccountIdentifier(1003L)
                .acquiringAccountFullIdentifier(VOLUNTARY_CANCELLATION_ACCOUNT)
                .blocks(blocks)
                .build();
        Transaction transaction = cancellationKyotoUnitsProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        cancellationKyotoUnitsProcessor.propose(transactionSummary);
        cancellationKyotoUnitsProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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
    void testInternalTransfer() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.CER, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "5",
                Boolean.FALSE, "JP12345"));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-1009-2-44", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.InternalTransfer)
            .transferringAccountIdentifier(10000002L)
            .acquiringAccountFullIdentifier("GB-100-1009-2-44")
            .blocks(blocks)
            .build();

        Transaction transaction = internalTransferProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        internalTransferProcessor.propose(transactionSummary);
        internalTransferProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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
        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        verify(transactionAccountBalanceService, times(1)).updateTransactionAccountBalances(any());
    }

    @Test
    void testTransferToSopFirstExternalTransferAAU() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "10",
                true, null));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("CDM-100-1100-0", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.TransferToSOPforFirstExtTransferAAU)
            .transferringAccountIdentifier(10000002L)
            .blocks(blocks)
            .build();

        Transaction transaction = transferToSOPforFirstExternalTransferProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        transferToSOPforFirstExternalTransferProcessor.propose(transactionSummary);
        transferToSOPforFirstExternalTransferProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(1)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());

        verify(entitlementService, times(1)).updateEntitlementsOnFinalisation(any());
    }

    @Test
    void testTransferToSopForConversionAAU() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ERU_FROM_RMU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "10",
                true, null));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
                AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
                AccountSummary.parse("CDM-100-1100-0", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
                .type(TransactionType.TransferToSOPforFirstExtTransferAAU)
                .transferringAccountIdentifier(10000002L)
                .blocks(blocks)
                .build();

        Transaction transaction = transferToSOPForConversionOfERUProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        transferToSOPForConversionOfERUProcessor.propose(transactionSummary);
        transferToSOPForConversionOfERUProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(1)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
    }

    @Test
    void testIssuanceKyotoUnits() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.AAU, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "10",
                true, null));

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-10000002-2-99", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.AAU);
        block.setOriginatingCountryCode("GB");
        block.setOriginalPeriod(CommitmentPeriod.CP2);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setSubjectToSop(true);
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        when(transactionPersistenceService.getTransactionBlocks(any())).thenReturn(
            Collections.singletonList(block)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .identifier("GB99300")
            .type(TransactionType.IssueOfAAUsAndRMUs)
            .acquiringAccountIdentifier(10000002L)
            .blocks(blocks)
            .build();

        Transaction transaction = issuanceKyotoProcessor.createInitialTransaction(transactionSummary);
        when(transactionPersistenceService.getTransaction(any())).thenReturn(
            transaction
        );
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        issuanceKyotoProcessor.propose(transactionSummary);
        
        assertNotNull(transactionSummary.getIdentifier());
        assertNull(transactionSummary.getLastUpdated());
        assertNull(transactionSummary.getTransferringAccountIdentifier());
        assertNotNull(transactionSummary.getAcquiringAccountIdentifier());
        verify(accountHoldingService, times(1)).getCurrentAccountBalances(transactionSummary.getIdentifier(),
                transactionSummary.getLastUpdated(),
                transactionSummary.getTransferringAccountIdentifier(),
                transactionSummary.getAcquiringAccountIdentifier());
        
        issuanceKyotoProcessor.finalise(transaction);

        verify(unitCreationService, times(1)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(1)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(0)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(1)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(1)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(0)).reserveUnits(any());
        verify(unitReservationService, times(0)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());

        transaction = issuanceKyotoProcessor.createInitialTransaction(transactionSummary);
        issuanceKyotoProcessor.reject(transaction);
        verify(levelService, times(1)).release(any(), any(), any(), any(), any());

        transaction = issuanceKyotoProcessor.createInitialTransaction(transactionSummary);
        issuanceKyotoProcessor.terminate(transaction);
        verify(levelService, times(2)).release(any(), any(), any(), any(), any());

        transaction = issuanceKyotoProcessor.createInitialTransaction(transactionSummary);
        issuanceKyotoProcessor.cancel(transaction);
        verify(levelService, times(3)).release(any(), any(), any(), any(), any());



    }

    @Test
    void testIssuanceAllowances() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10",
                null, null));

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000002-2-99", RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT, AccountStatus.OPEN)
        );

        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.ALLOWANCE);
        block.setOriginatingCountryCode("GB");
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        block.setYear(2020);
        when(transactionPersistenceService.getTransactionBlocks(any())).thenReturn(
            Collections.singletonList(block)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .identifier("UK83376")
            .type(TransactionType.IssueAllowances)
            .acquiringAccountIdentifier(10000002L)
            .blocks(blocks)
            .build();

        Transaction transaction = issuanceAllowanceProcessor.createInitialTransaction(transactionSummary);
        when(transactionPersistenceService.getTransaction(any())).thenReturn(
            transaction
        );
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        issuanceAllowanceProcessor.propose(transactionSummary);
        
        assertNotNull(transactionSummary.getIdentifier());
        assertNull(transactionSummary.getLastUpdated());
        assertNull(transactionSummary.getTransferringAccountIdentifier());
        assertNotNull(transactionSummary.getAcquiringAccountIdentifier());
        verify(accountHoldingService, times(1)).getCurrentAccountBalances(transactionSummary.getIdentifier(),
                transactionSummary.getLastUpdated(),
                transactionSummary.getTransferringAccountIdentifier(),
                transactionSummary.getAcquiringAccountIdentifier());
        
        issuanceAllowanceProcessor.finalise(transaction);

        verify(unitCreationService, times(1)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(1)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(0)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(allocationPhaseCapService, times(1)).reserveCap(any(), any());
        verify(allocationPhaseCapService, times(0)).releaseCap(any(), any());
        verify(allocationPhaseCapService, times(1)).consumeCap(any(), any());

        verify(allocationYearCapService, times(1)).reserveCap(any(), any());
        verify(allocationYearCapService, times(0)).releaseCap(any(), any());
        verify(allocationYearCapService, times(1)).consumeCap(any(), any());

        verify(unitMarkingService, times(0)).markApplicablePeriod(any(), any());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(0)).reserveUnits(any());
        verify(unitReservationService, times(0)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());

        verify(allocationConfigurationService, times(1)).getAllocationYear();

    }

    @Test
    void testIssuanceAllowancesNegativeScenarios() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, null, null,
                null, null, "10",
                null, null));

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-10000002-2-99", RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT, AccountStatus.OPEN)
        );

        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.ALLOWANCE);
        block.setOriginatingCountryCode("GB");
        block.setStartBlock(1L);
        block.setEndBlock(10L);
        block.setYear(2020);
        when(transactionPersistenceService.getTransactionBlocks(any())).thenReturn(
            Collections.singletonList(block)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.IssueAllowances)
            .acquiringAccountIdentifier(10000002L)
            .blocks(blocks)
            .build();

        Transaction transaction = issuanceAllowanceProcessor.createInitialTransaction(transactionSummary);
        when(transactionPersistenceService.getTransaction(any())).thenReturn(
            transaction
        );
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        transaction = issuanceAllowanceProcessor.createInitialTransaction(transactionSummary);
        issuanceAllowanceProcessor.reject(transaction);
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(allocationPhaseCapService, times(1)).releaseCap(any(), any());
        verify(allocationYearCapService, times(1)).releaseCap(any(), any());

        transaction = issuanceAllowanceProcessor.createInitialTransaction(transactionSummary);
        issuanceAllowanceProcessor.terminate(transaction);
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(allocationPhaseCapService, times(2)).releaseCap(any(), any());
        verify(allocationYearCapService, times(2)).releaseCap(any(), any());

        transaction = issuanceAllowanceProcessor.createInitialTransaction(transactionSummary);
        issuanceAllowanceProcessor.cancel(transaction);
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(allocationPhaseCapService, times(3)).releaseCap(any(), any());
        verify(allocationYearCapService, times(3)).releaseCap(any(), any());
    }

    @Test
    void testAllocation() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "100",
                Boolean.FALSE, null));

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1012-0-37", RegistryAccountType.UK_ALLOCATION_ACCOUNT, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1009-2-44", RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.AllocateAllowances)
            .transferringAccountIdentifier(1012L)
            .acquiringAccountFullIdentifier("UK-100-1009-2-44")
            .blocks(blocks)
            .build();

        transactionSummary.setAdditionalAttributes(Map.of(
            AllocationYear.class.getSimpleName(), 2021,
            AllocationType.class.getSimpleName(), AllocationType.NAT));

        Transaction transaction = allocationProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        allocationProcessor.propose(transactionSummary);
        allocationProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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
        verify(allocationCalculationService, times(1)).updateAllocationStatus(any(), any(), any(), any(), any());
        verify(allocationCalculationService, times(1)).calculateAndUpdateRegistryStatus(any());
    }

    @Test
    void testSurrenderAllowances() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            TransactionBlockSummary.builder()
                .type(UnitType.ALLOWANCE)
                .applicablePeriod(CommitmentPeriod.CP0)
                .originalPeriod(CommitmentPeriod.CP0)
                .quantity("10")
                .build());

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1009-2-44",
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1012-0-37",
                RegistryAccountType.UK_ALLOCATION_ACCOUNT,
                AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.SurrenderAllowances)
            .transferringAccountIdentifier(1009L)
            .acquiringAccountFullIdentifier("UK-100-1012-0-37")
            .blocks(blocks)
            .build();

        Transaction transaction = surrenderAllowancesProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        surrenderAllowancesProcessor.propose(transactionSummary);
        surrenderAllowancesProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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

        // TODO: Add compliance specific verifications here, when compliance gets implemented in the future.
    }

    @Test
    void testReverseAllocation() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            new TransactionBlockSummary(UnitType.ALLOWANCE, CommitmentPeriod.CP2, CommitmentPeriod.CP2,
                null, null, "100",
                Boolean.FALSE, null));

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1012-0-37", RegistryAccountType.UK_ALLOCATION_ACCOUNT, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1009-2-44", RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ReverseAllocateAllowances)
            .transferringAccountIdentifier(1012L)
            .acquiringAccountFullIdentifier("UK-100-1009-2-44")
            .blocks(blocks)
            .build();

        transactionSummary.setAdditionalAttributes(Map.of(
            AllocationYear.class.getSimpleName(), 2021,
            AllocationType.class.getSimpleName(), AllocationType.NAT));

        Transaction transaction = reverseAllocationProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        reverseAllocationProcessor.propose(transactionSummary);
        reverseAllocationProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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
        verify(allocationCalculationService, times(1)).updateAllocationStatus(any(), any(), any(), any(), any());
        verify(allocationCalculationService, times(1)).calculateAndUpdateRegistryStatus(any());

    }

    @Test
    void testReturnExcessAllocation() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            TransactionBlockSummary.builder()
                .type(UnitType.ALLOWANCE)
                .applicablePeriod(CommitmentPeriod.CP0)
                .originalPeriod(CommitmentPeriod.CP0)
                .quantity("10")
                .build());

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1009-2-44",
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1012-0-37",
                RegistryAccountType.UK_ALLOCATION_ACCOUNT,
                AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExcessAllocation)
            .allocationType(AllocationType.NAT)
            .allocationYear(2021)
            .transferringAccountIdentifier(1009L)
            .acquiringAccountFullIdentifier("UK-100-1012-0-37")
            .blocks(blocks)
            .build();

        Transaction transaction = excessAllocationProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        excessAllocationProcessor.propose(transactionSummary);
        excessAllocationProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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

        verify(allocationCalculationService, times(1)).updateAllocationStatus(any(), any(), any(), any(), any());
        verify(allocationCalculationService, times(1)).calculateAndUpdateRegistryStatus(any());
    }

    @Test
    void testConversionCP1() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            TransactionBlockSummary.builder()
                .type(UnitType.AAU)
                .applicablePeriod(CommitmentPeriod.CP1)
                .originalPeriod(CommitmentPeriod.CP1)
                .quantity("10")
                .build());

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("GB-100-1003-2-76", RegistryAccountType.NONE, AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ConversionCP1)
            .transferringAccountIdentifier(1003L)
            .blocks(blocks)
            .build();

        Transaction transaction = conversionCP1Processor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        conversionCP1Processor.propose(transactionSummary);
        conversionCP1Processor.finalise(transaction);


        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any(), any(), any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
        verify(levelService, times(0)).reserve(any(), any(), any(), any(), any());
        verify(levelService, times(0)).release(any(), any(), any(), any(), any());
        verify(levelService, times(0)).consume(any(), any(), any(), any(), any());

        verify(unitMarkingService, times(1)).markProjectAndConvertToERU(any(), any(), anyBoolean());
        verify(unitMarkingService, times(0)).markUnitsAsSubjectToSop(any(), any());

        verify(unitTransferService, times(0)).transferUnitsOutsideRegistry(any());
        verify(unitTransferService, times(0)).transferUnitsInsideRegistry(any(), any());

        verify(unitReservationService, times(1)).reserveUnits(any());
        verify(unitReservationService, times(1)).releaseBlocks(any());

        verify(accountHoldingService, times(1)).updateAccountBalances(any());
        verify(transactionPersistenceService, times(1)).updateStatus(any(), any());
    }

    @Test
    void testReverseSurrenderAllowances() {
        List<TransactionBlockSummary> blocks = Collections.singletonList(
            TransactionBlockSummary.builder()
                .type(UnitType.ALLOWANCE)
                .applicablePeriod(CommitmentPeriod.CP0)
                .originalPeriod(CommitmentPeriod.CP0)
                .quantity("10")
                .build());

        when(transactionAccountService.populateTransferringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1009-2-44",
                RegistryAccountType.UK_SURRENDER_ACCOUNT,
                AccountStatus.OPEN)
        );

        when(transactionAccountService.populateAcquiringAccount(any())).thenReturn(
            AccountSummary.parse("UK-100-1012-0-37",
                RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountStatus.OPEN)
        );

        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ReverseSurrenderAllowances)
            .transferringAccountIdentifier(1009L)
            .acquiringAccountFullIdentifier("UK-100-1012-0-37")
            .blocks(blocks)
            .build();

        Transaction transaction = reverseSurrenderProcessor.createInitialTransaction(transactionSummary);
        assertEquals(TransactionStatus.AWAITING_APPROVAL, transaction.getStatus());

        reverseSurrenderProcessor.propose(transactionSummary);
        reverseSurrenderProcessor.finalise(transaction);

        verify(unitCreationService, times(0)).generateTransactionBlock(any(), any(), any());
        verify(unitCreationService, times(0)).createUnitBlocks(any(), any());
        verify(unitCreationService, times(1)).createTransactionBlocks(any());

        verify(transactionAccountBalanceService, times(1)).createTransactionAccountBalances(any());
        
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

        // TODO: Add compliance specific verifications here, when compliance gets implemented in the future.
    }

    @Test
    void testDelay() {
        Transaction transaction = new Transaction();
        transaction.setIdentifier("GB12345");
        transaction.setType(TransactionType.ExternalTransfer);
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountIdentifier(12L);
        transferringAccount.setAccountFullIdentifier("GB12");
        transaction.setTransferringAccount(transferringAccount);

        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountIdentifier(54L);
        acquiringAccount.setAccountRegistryCode("GB");
        acquiringAccount.setAccountFullIdentifier("GB54");
        transaction.setAcquiringAccount(acquiringAccount);

        UpdateAccountBalanceResult updateAccountBalanceResult = UpdateAccountBalanceResult.builder().build();

        when(transactionDelayService.calculateTransactionDelay(false))
            .thenReturn(LocalDateTime.of(2020, 4, 20, 10, 1, 1));

        when(accountHoldingService.getCurrentAccountBalances("GB12345", null, "GB12", "GB54"))
            .thenReturn(updateAccountBalanceResult);

        externalTransferProcessor.delay(transaction);

        verify(transactionPersistenceService, times(1)).updateStatus(transaction, TransactionStatus.DELAYED);

        verify(transactionEventService, times(1)).createAndPublishEvent(eq("GB12345"),
            eq(null),
            anyString(),
            eq(transaction),
            eq(TransactionEventType.TRANSACTION_DELAYED));

        verify(transactionAccountBalanceService, times(1)).updateTransactionAccountBalances(updateAccountBalanceResult);
    }

}
