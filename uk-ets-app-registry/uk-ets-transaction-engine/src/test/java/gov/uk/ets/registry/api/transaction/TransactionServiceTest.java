package gov.uk.ets.registry.api.transaction;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.event.service.TransactionEventService;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckService;
import gov.uk.ets.registry.api.transaction.checks.RequiredFieldException;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.TransactionConnection;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionConnectionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.lock.RegistryLockProvider;
import gov.uk.ets.registry.api.transaction.messaging.ITLBlockConversionService;
import gov.uk.ets.registry.api.transaction.messaging.ITLConversionService;
import gov.uk.ets.registry.api.transaction.messaging.ITLOutgoingMessageService;
import gov.uk.ets.registry.api.transaction.messaging.UKTLOutgoingMessageService;
import gov.uk.ets.registry.api.transaction.processor.ExternalTransferProcessor;
import gov.uk.ets.registry.api.transaction.processor.InternalTransferProcessor;
import gov.uk.ets.registry.api.transaction.processor.IssuanceAllowanceProcessor;
import gov.uk.ets.registry.api.transaction.processor.IssuanceProcessor;
import gov.uk.ets.registry.api.transaction.repository.TransactionConnectionRepository;
import gov.uk.ets.registry.api.transaction.service.ApprovalService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionDelayService;
import gov.uk.ets.registry.api.transaction.service.TransactionMessageService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.ets.lib.commons.kyoto.types.EvaluationResult;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;

class TransactionServiceTest {

    @Mock
    private BusinessCheckService businessCheckService;

    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private TransactionPersistenceService transactionPersistenceService;

    @Mock
    private ITLOutgoingMessageService itlOutgoingMessageService;

    @Mock
    private UKTLOutgoingMessageService uktlOutgoingMessageService;

    @Mock
    private ITLConversionService itlConversionService;

    @Mock
    private ITLBlockConversionService itlBlockConversionService;

    @Mock
    private ApprovalService approvalService;

    @Mock
    private TransactionDelayService transactionDelayService;

    @Mock
    private TransactionAccountService transactionAccountService;

    @Mock
    private ExternalTransferProcessor externalTransferProcessor;

    @Mock
    private InternalTransferProcessor internalTransferProcessor;

    @Mock
    private IssuanceAllowanceProcessor issuanceAllowanceProcessor;

    @Mock
    private IssuanceProcessor issuanceProcessor;

    @Mock
    private ReconciliationRepository reconciliationRepository;

    @Mock
    private RegistryLockProvider registryLockProvider;

    @Mock
    private TransactionEventService transactionEventService;

    @Mock
    private TransactionReversalService transactionReversalService;

    @Mock
    private TransactionMessageService transactionMessageService;

    @Mock
    private TransactionConnectionRepository transactionConnectionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void clarifyTransactionType1() {

        AccountSummary transferringAccount =
            AccountSummary.parse("GB-100-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.ExternalTransfer);
        transactionSummary.setTransferringAccountIdentifier(1L);
        transactionSummary.setAcquiringAccountFullIdentifier("GB-100-222-0-11");
        when(transactionAccountService.populateTransferringAccount(transactionSummary))
            .thenReturn(transferringAccount);

        transactionService.performChecks(transactionSummary, true);

        assertEquals(TransactionType.InternalTransfer, transactionSummary.getType());

    }

    @Test
    void clarifyTransactionType2() {

        AccountSummary transferringAccount =
            AccountSummary.parse("GB-100-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.InternalTransfer);
        transactionSummary.setTransferringAccountIdentifier(1L);
        transactionSummary.setAcquiringAccountFullIdentifier("GB-100-222-0-11");
        when(transactionAccountService.populateTransferringAccount(transactionSummary))
            .thenReturn(transferringAccount);

        transactionService.performChecks(transactionSummary, true);

        assertEquals(TransactionType.InternalTransfer, transactionSummary.getType());

    }

    @Test
    void clarifyTransactionType3() {

        AccountSummary transferringAccount =
            AccountSummary.parse("GB-100-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.InternalTransfer);
        transactionSummary.setTransferringAccountIdentifier(1L);
        transactionSummary.setAcquiringAccountFullIdentifier("JP-100-222-0");
        when(transactionAccountService.populateTransferringAccount(transactionSummary))
            .thenReturn(transferringAccount);

        transactionService.performChecks(transactionSummary, true);

        assertEquals(TransactionType.ExternalTransfer, transactionSummary.getType());

    }

    @Test
    void clarifyTransactionType4() {

        AccountSummary transferringAccount =
            AccountSummary.parse("GB-100-12345-0-22", RegistryAccountType.NONE, AccountStatus.OPEN);

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.ExternalTransfer);
        transactionSummary.setTransferringAccountIdentifier(1L);
        transactionSummary.setAcquiringAccountFullIdentifier("JP-100-222-0");
        when(transactionAccountService.populateTransferringAccount(transactionSummary))
            .thenReturn(transferringAccount);

        transactionService.performChecks(transactionSummary, true);

        assertEquals(TransactionType.ExternalTransfer, transactionSummary.getType());

    }

    @Test
    void clarifyTransactionType5() {

        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.IssueOfAAUsAndRMUs);
        transactionSummary.setAcquiringAccountIdentifier(12L);
        when(transactionAccountService.populateTransferringAccount(transactionSummary)).thenReturn(null);

        transactionService.performChecks(transactionSummary, true);

        assertEquals(TransactionType.IssueOfAAUsAndRMUs, transactionSummary.getType());

    }

    @Test
    void validateInput() {
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(null, true));

        TransactionSummary transaction = new TransactionSummary();
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        transaction.setType(TransactionType.ExternalTransfer);
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        transaction.setBlocks(blocks);
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        TransactionBlockSummary block = new TransactionBlockSummary();
        blocks.add(block);
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        block.setType(UnitType.CER);
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        block.setOriginalPeriod(CommitmentPeriod.CP1);
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        block.setOriginalPeriod(null);
        block.setApplicablePeriod(CommitmentPeriod.CP1);
        assertThrows(RequiredFieldException.class, () -> transactionService.proposeTransaction(transaction, true));

        transaction.setType(TransactionType.IssueAllowances);
        block.setQuantity("10");
        block.setType(UnitType.ALLOWANCE);
        block.setOriginalPeriod(null);
        block.setApplicablePeriod(null);
        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.IssueAllowances);
        when(approvalService.isApprovalRequired(any(), anyBoolean())).thenReturn(true);
        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(issuanceAllowanceProcessor);
        when(issuanceAllowanceProcessor.createInitialTransaction(any())).thenReturn(transaction1);
        assertDoesNotThrow(() -> {
            transactionService.proposeTransaction(transaction, false);
        });

    }

    @Test
    void testFinaliseTransaction() {
        assertThrows(RequiredFieldException.class, () ->
            transactionService.finaliseTransaction(null, TaskOutcome.APPROVED, true));

        assertThrows(RequiredFieldException.class, () ->
            transactionService.finaliseTransaction(null, TaskOutcome.APPROVED, false));

        assertThrows(RequiredFieldException.class, () ->
            transactionService.finaliseTransaction("GB12345", null, true));

        assertThrows(RequiredFieldException.class, () ->
            transactionService.finaliseTransaction("GB12345", null, false));

        when(transactionPersistenceService.getTransaction("GB12345")).thenReturn(null);
        assertThrows(RequiredFieldException.class, () ->
            transactionService.finaliseTransaction("GB12345", TaskOutcome.APPROVED, false));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.ExternalTransfer);

        when(transactionPersistenceService.getTransaction("GB12345")).thenReturn(transaction);
        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(externalTransferProcessor);
        transactionService.finaliseTransaction("GB12345", TaskOutcome.REJECTED, false);

        when(transactionDelayService.isTransactionValidForDelay(transaction)).thenReturn(false);
        transactionService.finaliseTransaction("GB12345", TaskOutcome.APPROVED, false);
        verify(transactionDelayService, times(0)).calculateTransactionDelay(false);

        when(transactionDelayService.isTransactionValidForDelay(transaction)).thenReturn(true);
        transactionService.finaliseTransaction("GB12345", TaskOutcome.APPROVED, false);
        verify(externalTransferProcessor, times(1)).delay(transaction);

    }

    @Test
    void testProposeTransaction() {

        List<TransactionBlockSummary> blocks = new ArrayList<>();
        TransactionBlockSummary block = new TransactionBlockSummary();
        blocks.add(block);
        block.setType(UnitType.CER);
        block.setOriginalPeriod(CommitmentPeriod.CP1);
        block.setApplicablePeriod(CommitmentPeriod.CP1);
        block.setQuantity("10");

        TransactionSummary transaction = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .build();
        transaction.setBlocks(blocks);
        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(externalTransferProcessor);
        when(transactionPersistenceService.getNextIdentifier()).thenReturn(100L);

        Transaction transaction1 = new Transaction();
        transaction1.setType(TransactionType.ExternalTransfer);
        when(transactionPersistenceService.getTransaction(any())).thenReturn(transaction1);
        when(externalTransferProcessor.createInitialTransaction(any())).thenReturn(transaction1);
        when(approvalService.isApprovalRequired(any(), anyBoolean())).thenReturn(false);
        assertNotNull(transactionService.proposeTransaction(transaction, false).getExecutionDate());


        when(approvalService.isApprovalRequired(any(), anyBoolean())).thenReturn(true);
        transactionService.proposeTransaction(transaction, false);
        assertNull(transactionService.proposeTransaction(transaction, false).getExecutionDate());
    }

    @Test
    void testProcessReply() {
        Transaction transaction = new Transaction();
        transaction.setIdentifier("GB12345");
        transaction.setType(TransactionType.InternalTransfer);
        transaction.setStatus(TransactionStatus.PROPOSED);
        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(externalTransferProcessor);
        when(transactionPersistenceService.getTransaction(transaction.getIdentifier())).thenReturn(transaction);

        NotificationRequest request = new NotificationRequest();
        request.setTransactionStatus(TransactionStatus.ACCEPTED.getCode());
        request.setTransactionIdentifier("GB12345");
        transactionService.processReply(request);
        verify(externalTransferProcessor, times(1)).finalise(transaction);
        verify(externalTransferProcessor, times(0)).reject(transaction);
        verify(externalTransferProcessor, times(0)).terminate(transaction);
        verify(externalTransferProcessor, times(0)).cancel(transaction);
        verify(transactionPersistenceService, times(0)).saveTransactionResponse(any());

        request.setTransactionStatus(TransactionStatus.CHECKED_NO_DISCREPANCY.getCode());
        transactionService.processReply(request);
        verify(externalTransferProcessor, times(2)).finalise(transaction);
        verify(externalTransferProcessor, times(0)).reject(transaction);
        verify(externalTransferProcessor, times(0)).terminate(transaction);
        verify(externalTransferProcessor, times(0)).cancel(transaction);
        verify(transactionPersistenceService, times(0)).saveTransactionResponse(any());

        request.setTransactionStatus(TransactionStatus.STL_CHECKED_DISCREPANCY.getCode());
        EvaluationResult error = new EvaluationResult();
        error.setResponseCode(5902);
        EvaluationResult[] evaluationResult = {error};
        request.setEvaluationResult(evaluationResult);
        transactionService.processReply(request);
        verify(externalTransferProcessor, times(2)).finalise(transaction);
        verify(externalTransferProcessor, times(0)).reject(transaction);
        verify(externalTransferProcessor, times(1)).terminate(transaction);
        verify(externalTransferProcessor, times(0)).cancel(transaction);
        verify(transactionPersistenceService, times(1)).saveTransactionResponse(any());

        request.setTransactionStatus(TransactionStatus.CHECKED_DISCREPANCY.getCode());
        EvaluationResult[] evaluationResult2 = {error};
        request.setEvaluationResult(evaluationResult2);
        transactionService.processReply(request);
        verify(externalTransferProcessor, times(2)).finalise(transaction);
        verify(externalTransferProcessor, times(0)).reject(transaction);
        verify(externalTransferProcessor, times(2)).terminate(transaction);
        verify(externalTransferProcessor, times(0)).cancel(transaction);
        verify(transactionPersistenceService, times(2)).saveTransactionResponse(any());

    }

    @Test
    void testProcessReply2() {
        Transaction transaction = new Transaction();
        transaction.setIdentifier("GB12345");
        transaction.setType(TransactionType.InternalTransfer);
        transaction.setStatus(TransactionStatus.PROPOSED);
        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(externalTransferProcessor);
        when(transactionPersistenceService.getTransaction(transaction.getIdentifier())).thenReturn(transaction);

        NotificationRequest request = new NotificationRequest();
        request.setTransactionIdentifier("GB12345");
        EvaluationResult error = new EvaluationResult();
        error.setResponseCode(5902);

        request.setTransactionStatus(TransactionStatus.REJECTED.getCode());
        EvaluationResult[] evaluationResult3 = {error};
        request.setEvaluationResult(evaluationResult3);
        transactionService.processReply(request);
        verify(externalTransferProcessor, times(0)).finalise(transaction);
        verify(externalTransferProcessor, times(0)).reject(transaction);
        verify(externalTransferProcessor, times(1)).terminate(transaction);
        verify(externalTransferProcessor, times(0)).cancel(transaction);
        verify(transactionPersistenceService, times(1)).saveTransactionResponse(any());

        request.setTransactionStatus(TransactionStatus.CANCELLED.getCode());
        transactionService.processReply(request);
        verify(externalTransferProcessor, times(0)).finalise(transaction);
        verify(externalTransferProcessor, times(0)).reject(transaction);
        verify(externalTransferProcessor, times(1)).terminate(transaction);
        verify(externalTransferProcessor, times(1)).cancel(transaction);
        verify(transactionPersistenceService, times(1)).saveTransactionResponse(any());


    }

    @Test
    void testTransactionBlockSummariesFromBlocks() {

        Transaction transaction = new Transaction();
        transaction.setIdentifier("GB12345");
        transaction.setQuantity(20L);
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.InternalTransfer);

        AccountBasicInfo transferringAccountBasicInfo = new AccountBasicInfo();
        transferringAccountBasicInfo.setAccountFullIdentifier("GB-100-10001-0-44");
        transferringAccountBasicInfo.setAccountIdentifier(10001L);
        transferringAccountBasicInfo.setAccountRegistryCode("GB");
        transferringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        transaction.setTransferringAccount(transferringAccountBasicInfo);

        AccountBasicInfo acquiringAccountBasicInfo = new AccountBasicInfo();
        acquiringAccountBasicInfo.setAccountFullIdentifier("JP-100-700900-0");
        acquiringAccountBasicInfo.setAccountIdentifier(700900L);
        acquiringAccountBasicInfo.setAccountRegistryCode("JP");
        acquiringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        transaction.setAcquiringAccount(acquiringAccountBasicInfo);

        List<TransactionBlock> blockList = new ArrayList<>();

        TransactionBlock block1 = new TransactionBlock();
        block1.setApplicablePeriod(CommitmentPeriod.CP1);
        block1.setOriginalPeriod(CommitmentPeriod.CP1);
        block1.setStartBlock(10000000000L);
        block1.setEndBlock(10000000050L);
        block1.setType(UnitType.AAU);
        block1.setTransaction(transaction);
        blockList.add(block1);

        TransactionBlock block2 = new TransactionBlock();
        block2.setApplicablePeriod(CommitmentPeriod.CP2);
        block2.setOriginalPeriod(CommitmentPeriod.CP2);
        block2.setStartBlock(10000000051L);
        block2.setEndBlock(10000000058L);
        block2.setType(UnitType.AAU);
        block2.setSubjectToSop(true);
        block2.setTransaction(transaction);
        blockList.add(block2);

        TransactionBlock block3 = new TransactionBlock();
        block3.setApplicablePeriod(CommitmentPeriod.CP2);
        block3.setOriginalPeriod(CommitmentPeriod.CP2);
        block3.setStartBlock(10000000059L);
        block3.setEndBlock(10000000062L);
        block3.setType(UnitType.CER);
        block3.setProjectNumber("JP12345");
        block3.setTransaction(transaction);
        blockList.add(block3);

        TransactionBlock block4 = new TransactionBlock();
        block4.setApplicablePeriod(CommitmentPeriod.CP2);
        block4.setOriginalPeriod(CommitmentPeriod.CP2);
        block4.setStartBlock(10000000063L);
        block4.setEndBlock(10000000065L);
        block4.setType(UnitType.AAU);
        block4.setSubjectToSop(true);
        block4.setTransaction(transaction);
        blockList.add(block4);

        block3 = new TransactionBlock();
        block3.setApplicablePeriod(CommitmentPeriod.CP2);
        block3.setOriginalPeriod(CommitmentPeriod.CP2);
        block3.setStartBlock(10000000066L);
        block3.setEndBlock(10000000070L);
        block3.setType(UnitType.CER);
        block3.setProjectNumber("JP12345");
        block3.setTransaction(transaction);
        blockList.add(block3);

        block3 = new TransactionBlock();
        block3.setApplicablePeriod(CommitmentPeriod.CP2);
        block3.setOriginalPeriod(CommitmentPeriod.CP2);
        block3.setStartBlock(10000000071L);
        block3.setEndBlock(10000000072L);
        block3.setType(UnitType.CER);
        block3.setProjectNumber("JP12345");
        block3.setTransaction(transaction);
        blockList.add(block3);

        block3 = new TransactionBlock();
        block3.setApplicablePeriod(CommitmentPeriod.CP2);
        block3.setOriginalPeriod(CommitmentPeriod.CP2);
        block3.setStartBlock(10000000072L);
        block3.setEndBlock(10000000073L);
        block3.setType(UnitType.RMU);
        block3.setEnvironmentalActivity(EnvironmentalActivity.REVEGETATION);
        block3.setTransaction(transaction);
        blockList.add(block3);

        block3 = new TransactionBlock();
        block3.setApplicablePeriod(CommitmentPeriod.CP2);
        block3.setOriginalPeriod(CommitmentPeriod.CP2);
        block3.setStartBlock(10000000074L);
        block3.setEndBlock(10000000075L);
        block3.setType(UnitType.RMU);
        block3.setEnvironmentalActivity(EnvironmentalActivity.REVEGETATION);
        block3.setTransaction(transaction);
        blockList.add(block3);

        block3 = new TransactionBlock();
        block3.setApplicablePeriod(CommitmentPeriod.CP2);
        block3.setOriginalPeriod(CommitmentPeriod.CP2);
        block3.setStartBlock(10000000076L);
        block3.setEndBlock(10000000080L);
        block3.setType(UnitType.RMU);
        block3.setEnvironmentalActivity(EnvironmentalActivity.GRAZING_LAND_MANAGEMENT);
        block3.setTransaction(transaction);
        blockList.add(block3);

        List<TransactionBlockSummary> list = transactionService.getTransactionBlockSummariesFromBlocks(blockList, null);
        assertNotNull(list);
    }

    @Test
    void test_centralTransferAllowances() {
        Long transferringAccountBalance = 250L;
        Transaction transaction = new Transaction();
        transaction.setIdentifier("GB12345");
        transaction.setQuantity(100L);
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.CentralTransferAllowances);

        AccountBasicInfo transferringAccountBasicInfo = new AccountBasicInfo();
        transferringAccountBasicInfo.setAccountFullIdentifier("UK-100-1010-0-47");
        transferringAccountBasicInfo.setAccountIdentifier(1010L);
        transferringAccountBasicInfo.setAccountRegistryCode("UK");
        transferringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        transaction.setTransferringAccount(transferringAccountBasicInfo);

        AccountBasicInfo acquiringAccountBasicInfo = new AccountBasicInfo();
        acquiringAccountBasicInfo.setAccountFullIdentifier("UK-100-1013-0-32");
        acquiringAccountBasicInfo.setAccountIdentifier(1013L);
        acquiringAccountBasicInfo.setAccountRegistryCode("UK");
        acquiringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);

        transaction.setAcquiringAccount(acquiringAccountBasicInfo);

        List<TransactionBlock> blockList = new ArrayList<>();

        TransactionBlock block1 = new TransactionBlock();
        block1.setApplicablePeriod(CommitmentPeriod.CP2);
        block1.setOriginalPeriod(CommitmentPeriod.CP2);
        block1.setOriginatingCountryCode("GB");
        block1.setYear(2021);
        block1.setStartBlock(10000000000L);
        block1.setEndBlock(10000000050L);
        block1.setType(UnitType.ALLOWANCE);
        block1.setTransaction(transaction);
        blockList.add(block1);

        TransactionBlock block2 = new TransactionBlock();
        block2.setApplicablePeriod(CommitmentPeriod.CP2);
        block2.setOriginalPeriod(CommitmentPeriod.CP2);
        block2.setOriginatingCountryCode("GB");
        block2.setStartBlock(10000000051L);
        block2.setEndBlock(10000000100L);
        block2.setType(UnitType.ALLOWANCE);
        block2.setYear(2021);
        block2.setTransaction(transaction);
        blockList.add(block2);

        List<TransactionBlockSummary> list =
            transactionService.getTransactionBlockSummariesFromBlocks(blockList, transferringAccountBalance);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(UnitType.ALLOWANCE, list.get(0).getType());
        assertEquals(transferringAccountBalance, list.get(0).getAvailableQuantity());
        assertThat(transferringAccountBalance, greaterThan(Long.valueOf(list.get(0).getQuantity())));
    }

    @Test
    @DisplayName("Manually cancel a transaction not in Delayed Status should throw an error")
    void testManuallyCancel_NotInDelayedStatus() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.ExternalTransfer);
        transaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
        when(transactionPersistenceService.getTransaction("GB12345")).thenReturn(transaction);

        assertThrows(IllegalStateException.class, () ->
            transactionService.manuallyCancel("GB12345", "a reason to cancel the transaction", "urid"));
    }

    @Test
    @DisplayName("Manually cancel a transaction in Delayed Status should invoke internalTransferProcessor.cancel() ")
    void testManuallyCancel_DelayedStatus() {
        AccountBasicInfo transferringAccountInfo = new AccountBasicInfo();
        transferringAccountInfo.setAccountIdentifier(1000L);
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.InternalTransfer);
        transaction.setStatus(TransactionStatus.DELAYED);
        transaction.setTransferringAccount(transferringAccountInfo);

        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(internalTransferProcessor);
        when(transactionPersistenceService.getTransaction("GB12345")).thenReturn(transaction);
        transactionService.manuallyCancel("GB12345", "a reason to cancel the transaction", "urid");
        verify(internalTransferProcessor, times(1)).manuallyCancel(transaction);
    }

    @Test
    @DisplayName("A failed Issuance transaction should invoke IssuanceProcessor.fail() in order to release the limit")
    void test_FailedIssuanceTransaction() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.IssueAllowances);
        transaction.setStatus(TransactionStatus.PROPOSED);

        when(transactionFactory.getTransactionProcessor(transaction.getType()))
            .thenReturn(issuanceProcessor);
        transactionService.handleTransactionFailure(transaction);
        verify(issuanceProcessor, times(1)).fail(transaction);
    }

    @Test
    void testPopulateConnectionSummary() {

        Transaction originalTransaction = new Transaction();
        originalTransaction.setType(TransactionType.DeletionOfAllowances);
        originalTransaction.setIdentifier("UK100001");
        originalTransaction.setStatus(TransactionStatus.COMPLETED);

        Transaction reversalTransaction = new Transaction();
        reversalTransaction.setType(TransactionType.ReverseDeletionOfAllowances);
        reversalTransaction.setIdentifier("UK100002");
        reversalTransaction.setStatus(TransactionStatus.AWAITING_APPROVAL);

        TransactionConnection transactionConnection = new TransactionConnection();
        transactionConnection.setObjectTransaction(originalTransaction);
        transactionConnection.setSubjectTransaction(reversalTransaction);
        transactionConnection.setType(TransactionConnectionType.REVERSES);

        when(transactionPersistenceService.getTransactionConnection(originalTransaction,
            TransactionConnectionType.REVERSES, false))
            .thenReturn(transactionConnection);
        when(transactionPersistenceService.getTransactionConnection(reversalTransaction,
            TransactionConnectionType.REVERSES, true))
            .thenReturn(transactionConnection);

        TransactionConnectionSummary transactionConnectionSummary =
            new TransactionConnectionSummary("UK100001", TransactionStatus.COMPLETED,
                "UK100002", TransactionStatus.AWAITING_APPROVAL);

        assertNull(transactionService.populateConnectionSummary(originalTransaction).getOriginalIdentifier());
        assertNull(transactionService.populateConnectionSummary(reversalTransaction).getReversalStatus());
        assertEquals(transactionConnectionSummary.getReversalIdentifier(),
            transactionService.populateConnectionSummary(originalTransaction).getReversalIdentifier());
        assertEquals(transactionConnectionSummary.getOriginalIdentifier(),
            transactionService.populateConnectionSummary(reversalTransaction).getOriginalIdentifier());
    }
}
