package uk.gov.ets.transaction.log.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import uk.gov.ets.transaction.log.checks.core.BusinessCheckError;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckResult;
import uk.gov.ets.transaction.log.domain.AccountBasicInfo;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.KyotoAccountType;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.messaging.types.TransactionAnswer;
import uk.gov.ets.transaction.log.repository.TransactionRepository;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

@DisplayName("Testing transaction related service methods")
public class TransactionServiceTest {

    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UnitBlockRepository unitBlockRepository;
    @Mock
    private SplitUnitBlocksService splitBlocksService;
    @Mock
    private TransactionValidationService transactionValidationService;
    @Mock
    private KafkaTemplate<String,TransactionAnswer> kafkaTemplate;

    @Mock
    private LimitService limitService;

    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        transactionService = new TransactionService(transactionRepository,unitBlockRepository,splitBlocksService,kafkaTemplate,transactionValidationService, limitService);
    }
    
    @Test
    void completeTransaction() {
        Transaction proposal = new Transaction();
        proposal.setIdentifier("UK100053");
        proposal.setType(TransactionType.TransferAllowances);
        proposal.setUnitType(UnitType.ALLOWANCE);
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transferringAccount.setAccountIdentifier(10000992L);
        transferringAccount.setAccountFullIdentifier("UK-100-10000992-0-48");
        proposal.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        acquiringAccount.setAccountIdentifier(10000993L);
        acquiringAccount.setAccountFullIdentifier("UK-100-10000993-2-37");
        proposal.setAcquiringAccount(acquiringAccount);
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        transactionBlock_1.setYear(2021);
        transactionBlock_1.setTransaction(proposal);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        when(transactionRepository.save(proposal)).thenReturn(proposal);

        BusinessCheckResult context = new BusinessCheckResult();

        proposal = transactionService.completeTransaction(proposal, TransactionStatus.COMPLETED, context);
        
        assertEquals(TransactionStatus.COMPLETED, proposal.getStatus());
        assertNotNull(proposal.getHistoryEntries());
        assertEquals(1,proposal.getHistoryEntries().size());
        assertEquals(TransactionStatus.COMPLETED,proposal.getHistoryEntries().get(0).getStatus());
        assertNotNull(proposal.getHistoryEntries().get(0).getDate());
        assertNotNull(proposal.getHistoryEntries().get(0).getTransaction());
    }
    
    @Test
    void updateUnitBlocksOwnership() {
        Transaction proposal = new Transaction();
        proposal.setIdentifier("UK100053");
        proposal.setType(TransactionType.TransferAllowances);
        proposal.setUnitType(UnitType.ALLOWANCE);
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transferringAccount.setAccountIdentifier(10000992L);
        transferringAccount.setAccountFullIdentifier("UK-100-10000992-0-48");
        proposal.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        acquiringAccount.setAccountIdentifier(10000993L);
        acquiringAccount.setAccountFullIdentifier("UK-100-10000993-2-37");
        proposal.setAcquiringAccount(acquiringAccount);
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        transactionBlock_1.setYear(2021);
        transactionBlock_1.setTransaction(proposal);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        List<UnitBlock> affectedBlocks = transactionService.updateUnitBlocksOwnership(proposal); 
        
        affectedBlocks.forEach(b-> assertTrue(b.getAccountIdentifier().equals(proposal.getAcquiringAccount().getAccountIdentifier())));
    }
    
    @Test
    void toUnitBlock() {
        Transaction proposal = new Transaction();
        proposal.setIdentifier("UK100049");
        proposal.setType(TransactionType.IssueAllowances);
        proposal.setUnitType(UnitType.ALLOWANCE);
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transferringAccount.setAccountIdentifier(10000987L);
        transferringAccount.setAccountFullIdentifier("UK-100-10000987-0-73");
        proposal.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        acquiringAccount.setAccountIdentifier(10000987L);
        acquiringAccount.setAccountFullIdentifier("UK-100-10000987-0-73");
        proposal.setAcquiringAccount(acquiringAccount);
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        transactionBlock_1.setYear(2021);
        transactionBlock_1.setTransaction(proposal);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        proposal.getBlocks().forEach(b->{
            UnitBlock insertedBlock = transactionService.toUnitBlock(b);
                assertTrue(transactionBlock_1.getType().equals(insertedBlock.getType()));
                assertTrue(transactionBlock_1.getTransaction().getAcquiringAccount().getAccountIdentifier().equals(insertedBlock.getAccountIdentifier()));
                assertTrue(transactionBlock_1.getStartBlock().equals(insertedBlock.getStartBlock()));
                assertTrue(transactionBlock_1.getEndBlock().equals(insertedBlock.getEndBlock()));
                assertTrue(transactionBlock_1.getYear().equals(insertedBlock.getYear()));
            });

       
    }
    
    @Test
    void toTransaction() {
        TransactionNotification proposal = new TransactionNotification();
        proposal.setIdentifier("UK100049");
        proposal.setType(TransactionType.IssueAllowances);
        proposal.setUnitType(UnitType.ALLOWANCE);
        proposal.setExecutionDate(LocalDateTime.now());
        proposal.setQuantity(1250L-1200L+1);
        proposal.setStarted(new Date());
        proposal.setTransferringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT.toString());
        proposal.setTransferringAccountIdentifier(10000987L);
        proposal.setTransferringAccountFullIdentifier("UK-100-10000987-0-73");
        proposal.setAcquiringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT.toString());
        proposal.setAcquiringAccountIdentifier(10000987L);
        proposal.setAcquiringAccountFullIdentifier("UK-100-10000987-0-73");
        List<TransactionBlock> transactionUnitBlocks = new LinkedList<>();
        TransactionBlock transactionBlock_1 = new TransactionBlock();
        transactionBlock_1.setType(UnitType.ALLOWANCE);
        transactionBlock_1.setStartBlock(1200L);
        transactionBlock_1.setEndBlock(1250L);
        transactionBlock_1.setYear(2021);
        transactionUnitBlocks.add(transactionBlock_1);
        proposal.setBlocks(transactionUnitBlocks);
        
        Transaction transaction = transactionService.toTransaction(proposal);
        assertTrue(transaction.getIdentifier().equals(proposal.getIdentifier()));
        assertTrue(transaction.getType().equals(proposal.getType()));
        assertTrue(transaction.getUnitType().equals(proposal.getUnitType()));
        assertTrue(transaction.getExecutionDate().equals(proposal.getExecutionDate()));
        assertTrue(transaction.getQuantity().equals(proposal.getQuantity()));
        assertTrue(transaction.getStarted().equals(proposal.getStarted()));
        assertIterableEquals(transaction.getBlocks() , proposal.getBlocks());
        assertTrue(transaction.getTransferringAccount().getAccountIdentifier().equals(proposal.getTransferringAccountIdentifier()));
        assertTrue(transaction.getTransferringAccount().getAccountType().toString().equals(proposal.getTransferringAccountType()));
        assertTrue(transaction.getTransferringAccount().getAccountFullIdentifier().equals(proposal.getTransferringAccountFullIdentifier()));
        assertTrue(transaction.getAcquiringAccount().getAccountIdentifier().equals(proposal.getAcquiringAccountIdentifier()));
        assertTrue(transaction.getAcquiringAccount().getAccountType().toString().equals(proposal.getAcquiringAccountType()));
        assertTrue(transaction.getAcquiringAccount().getAccountFullIdentifier().equals(proposal.getAcquiringAccountFullIdentifier()));
        
        transaction.getBlocks().forEach(b->{
                assertTrue(transactionBlock_1.getType().equals(b.getType()));
                assertTrue(transactionBlock_1.getTransaction().getAcquiringAccount().getAccountIdentifier().equals(b.getTransaction().getAcquiringAccount().getAccountIdentifier()));
                assertTrue(transactionBlock_1.getStartBlock().equals(b.getStartBlock()));
                assertTrue(transactionBlock_1.getEndBlock().equals(b.getEndBlock()));
                assertTrue(transactionBlock_1.getYear().equals(b.getYear()));
            });

       
    }

    @Test
    void validateTransactionProposal() {
        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.ALLOWANCE);
        block.setStartBlock(1200L);
        block.setEndBlock(1250L);
        block.setYear(2021);

        TransactionNotification proposal = new TransactionNotification();
        proposal.setIdentifier("UK100099");
        proposal.setType(TransactionType.IssueAllowances);
        proposal.setUnitType(UnitType.ALLOWANCE);
        proposal.setExecutionDate(LocalDateTime.now());
        proposal.setQuantity(block.getEndBlock() - block.getStartBlock() + 1);
        proposal.setStarted(new Date());
        proposal.setTransferringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT.toString());
        proposal.setTransferringAccountIdentifier(10000987L);
        proposal.setTransferringAccountFullIdentifier("UK-100-10000987-0-73");
        proposal.setAcquiringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT.toString());
        proposal.setAcquiringAccountIdentifier(10000987L);
        proposal.setAcquiringAccountFullIdentifier("UK-100-10000987-0-73");

        proposal.setBlocks(Arrays.asList(block));

        BusinessCheckResult success = new BusinessCheckResult();
        BusinessCheckResult failure = new BusinessCheckResult() {{
            setErrors(Arrays.asList(new BusinessCheckError(1, "Message")));
        }};

        // Issuance success
        when(transactionValidationService.validateTransaction(any())).thenReturn(success);
        when(transactionRepository.save(any())).thenReturn(transactionService.toTransaction(proposal));

        BusinessCheckResult result = transactionService.validateTransactionProposal(proposal);
        assertTrue(result.success());
        verify(splitBlocksService, times(0)).splitUnitBlocks(any());

        // Issuance failure
        when(transactionValidationService.validateTransaction(any())).thenReturn(failure);
        result = transactionService.validateTransactionProposal(proposal);
        assertFalse(result.success());

        // Transfer success
        proposal.setType(TransactionType.CentralTransferAllowances);
        when(transactionValidationService.validateTransaction(any())).thenReturn(success);
        when(transactionRepository.save(any())).thenReturn(transactionService.toTransaction(proposal));
        result = transactionService.validateTransactionProposal(proposal);
        assertTrue(result.success());
        verify(splitBlocksService, times(1)).splitUnitBlocks(any());

        // Transfer failure
        when(transactionValidationService.validateTransaction(any())).thenReturn(failure);
        result = transactionService.validateTransactionProposal(proposal);
        assertFalse(result.success());

    }

}
