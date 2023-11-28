package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.signing.service.SigningVerificationService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.repository.TaskTransactionRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.SignedTransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.processor.ExcessAllocationProcessor;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class TransactionWithTaskServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private TaskTransactionRepository taskTransactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private EventService eventService;

    @Mock
    private AccountConversionService accountConversionService;

    @Mock
    private TransactionAccountService transactionAccountService;

    @Mock
    private SigningVerificationService signingVerificationService;

    @Mock
    private ITLNoticeManagementService itlNoticeManagementService;

    @Mock
    private TaskEventService taskEventService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private  Mapper mapper;

    private TransactionWithTaskService transactionWithTaskService;



    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        transactionWithTaskService =
            new TransactionWithTaskService(transactionService, taskRepository, accountService, taskTransactionRepository,
                userService, authorizationService, eventService, accountConversionService, transactionAccountService,
                signingVerificationService, itlNoticeManagementService, taskEventService, transactionRepository, mapper);
    }

    @Test
    void test_getTransactionTaskDetails() {
        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();

        Transaction transaction = new Transaction();
        List<TransactionBlock> blocks = new ArrayList<>();
        TransactionBlock block1 = new TransactionBlock();
        block1.setYear(2021);
        block1.setTransaction(transaction);
        block1.setType(UnitType.ALLOWANCE);
        block1.setStartBlock(10000011541L);
        block1.setEndBlock(10000011740L);
        block1.setOriginatingCountryCode("UK");
        block1.setApplicablePeriod(CommitmentPeriod.CP2);
        block1.setOriginalPeriod(CommitmentPeriod.CP2);
        blocks.add(block1);
        transaction.setBlocks(blocks);
        transaction.setType(TransactionType.TransferAllowances);

        AccountBasicInfo transferringAccountBasicInfo = new AccountBasicInfo();
        transferringAccountBasicInfo.setAccountFullIdentifier("UK-100-1016-0-17");
        transferringAccountBasicInfo.setAccountIdentifier(1016L);
        transferringAccountBasicInfo.setAccountRegistryCode("UK");
        transferringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transaction.setTransferringAccount(transferringAccountBasicInfo);

        AccountDTO transferringAccount = new AccountDTO();
        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        accountDetailsDTO.setAccountStatus(AccountStatus.OPEN);
        accountDetailsDTO.setAccountType(AccountType.UK_AUCTION_DELIVERY_ACCOUNT.name());
        accountDetailsDTO.setName(AccountType.UK_AUCTION_DELIVERY_ACCOUNT.getLabel());
        accountDetailsDTO.setAccountNumber("UK-100-1016-0-17");
        transferringAccount.setAccountDetails(accountDetailsDTO);
        transferringAccount.setBalance(200L);
        Mockito.when(accountService.getAccountDTO(transaction.getTransferringAccount().getAccountIdentifier()))
            .thenReturn(transferringAccount);

        AccountInfo transferringAccountInfo = new AccountInfo(transferringAccountBasicInfo.getAccountIdentifier(),
            transferringAccountBasicInfo.getAccountFullIdentifier(), "", "",
            transferringAccountBasicInfo.getAccountRegistryCode());
        Mockito
            .when(accountConversionService.getAccountInfo(transaction.getTransferringAccount().getAccountIdentifier(),
                transaction.getTransferringAccount().getAccountFullIdentifier(), transferringAccount))
            .thenReturn(transferringAccountInfo);

        AccountBasicInfo acquiringAccountBasicInfo = new AccountBasicInfo();
        acquiringAccountBasicInfo.setAccountFullIdentifier("UK-100-1015-0-22");
        acquiringAccountBasicInfo.setAccountIdentifier(1015L);
        acquiringAccountBasicInfo.setAccountRegistryCode("UK");
        acquiringAccountBasicInfo.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transaction.setAcquiringAccount(acquiringAccountBasicInfo);

        AccountDTO acquiringAccount = new AccountDTO();
        AccountDetailsDTO acquiringAccountDetailsDTO = new AccountDetailsDTO();
        acquiringAccountDetailsDTO.setAccountStatus(AccountStatus.OPEN);
        acquiringAccountDetailsDTO.setAccountType(AccountType.UK_GENERAL_HOLDING_ACCOUNT.name());
        acquiringAccountDetailsDTO.setName(AccountType.UK_GENERAL_HOLDING_ACCOUNT.getLabel());
        acquiringAccountDetailsDTO.setAccountNumber("UK-100-1015-0-22");
        acquiringAccount.setAccountDetails(acquiringAccountDetailsDTO);
        Mockito.when(accountService.getAccountDTO(transaction.getAcquiringAccount().getAccountIdentifier()))
            .thenReturn(acquiringAccount);

        AccountInfo acquiringAccountInfo = new AccountInfo(acquiringAccountBasicInfo.getAccountIdentifier(),
            acquiringAccountBasicInfo.getAccountFullIdentifier(), "", "",
            acquiringAccountBasicInfo.getAccountRegistryCode());
        Mockito.when(accountConversionService.getAccountInfo(transaction.getAcquiringAccount().getAccountIdentifier(),
            transaction.getAcquiringAccount().getAccountFullIdentifier(), acquiringAccount))
            .thenReturn(acquiringAccountInfo);

        Mockito.when(transactionAccountService.isTrustedAccount(transferringAccountInfo.getIdentifier(),
            acquiringAccountInfo, acquiringAccountInfo.getRegistryCode()))
            .thenReturn(true);

        List<TransactionBlockSummary> transactionBlockSummaries = new ArrayList<>();
        TransactionBlockSummary transactionBlockSummary = new TransactionBlockSummary();
        transactionBlockSummary.setType(UnitType.ALLOWANCE);
        transactionBlockSummary.setQuantity("200");
        transactionBlockSummaries.add(transactionBlockSummary);
        Mockito.when(transactionService.getTransactionBlockSummariesFromBlocks(transaction.getBlocks(),
            transferringAccount.getBalance()))
            .thenReturn(transactionBlockSummaries);

        TransactionTaskDetailsDTO transactionTaskDetails =
            transactionWithTaskService.getTransactionTaskDetails(taskDetailsDTO, transaction);
        Assert.assertNotNull(transactionTaskDetails);
        Assert.assertEquals(TransactionType.TransferAllowances, transactionTaskDetails.getTrType());
        Assert.assertEquals(transaction.getTransferringAccount().getAccountIdentifier(),
            transactionTaskDetails.getTransferringAccount().getIdentifier());
        Assert.assertEquals(transaction.getAcquiringAccount().getAccountIdentifier(),
            transactionTaskDetails.getAcquiringAccount().getIdentifier());
        Assert.assertFalse(transactionTaskDetails.getTransactionBlocks().isEmpty());
    }
    
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void proposeMultipleTransactions(boolean isAdmin) {
        // given
        String transferringAccountFullIdentifier = "UK-100-10000088-0-9";
        String nerTransactionIdentifier = "UK100289";
        String natTransactionIdentifier = "UK100290";
        
        SignedTransactionSummary nerSummary = new SignedTransactionSummary();
        nerSummary.setTransferringAccountFullIdentifier(transferringAccountFullIdentifier);
        nerSummary.setTransferringAccountIdentifier(10000088L);
        nerSummary.setAcquiringAccountFullIdentifier("UK-100-10000002-0-51");
        nerSummary.setAcquiringAccountIdentifier(10000002L);
        nerSummary.setAllocationType(AllocationType.NER);
        nerSummary.setIdentifier(nerTransactionIdentifier);
        nerSummary.setType(TransactionType.ExcessAllocation);
        nerSummary.setAdditionalAttributes(Map.of(ExcessAllocationProcessor.RELATED_NAT_TRANSACTION_IDENTIFER,"UK100290"));
        
        SignedTransactionSummary natSummary = new SignedTransactionSummary();
        natSummary.setTransferringAccountFullIdentifier(transferringAccountFullIdentifier);
        natSummary.setTransferringAccountIdentifier(10000088L);
        natSummary.setAcquiringAccountFullIdentifier("UK-100-10000003-0-46");
        natSummary.setAcquiringAccountIdentifier(10000003L);
        natSummary.setAllocationType(AllocationType.NAT);
        natSummary.setIdentifier(natTransactionIdentifier);
        natSummary.setType(TransactionType.ExcessAllocation);
        natSummary.setAdditionalAttributes(Map.of(ExcessAllocationProcessor.IS_TRIGGERED_BY_NER_FINALIZATION,Boolean.TRUE));
        
        List<SignedTransactionSummary> proposals = new ArrayList<>();
        proposals.add(natSummary);
        proposals.add(nerSummary);

        
        BusinessCheckResult nerResult = new BusinessCheckResult();
        nerResult.setTransactionIdentifier(nerSummary.getIdentifier());
        nerResult.setApprovalRequired(isAdmin);
        
        BusinessCheckResult natResult = new BusinessCheckResult();
        natResult.setTransactionIdentifier(natSummary.getIdentifier());
        natResult.setApprovalRequired(true);
        
        User currentUser = new User();
        currentUser.setUrid("UK873533391953");
        
        //Mocking methods
        Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)).thenReturn(isAdmin);
        Mockito.when(transactionService.proposeSignedTransaction(nerSummary,isAdmin)).thenReturn(nerResult);
        Mockito.when(transactionService.proposeSignedTransaction(natSummary,true)).thenReturn(natResult);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        
        // when
        BusinessCheckResult result = transactionWithTaskService.proposeMultipleTransactions(proposals);
        
        // then
        assertNotNull(result);
        assertEquals(isAdmin, result.getApprovalRequired());
        if (isAdmin) {
            assertNotNull(result.getRequestIdentifier());
        } else {
            assertNull(result.getRequestIdentifier());
        }
        
        InOrder order = inOrder(eventService, transactionService);

        ArgumentCaptor<SignedTransactionSummary> summaryCaptor = ArgumentCaptor.forClass(SignedTransactionSummary.class);
        ArgumentCaptor<Boolean> hasExtendedScopeCaptor = ArgumentCaptor.forClass(Boolean.class);
        //Verify the call to proposeSignedTransaction
        order.verify(transactionService,times(2)).proposeSignedTransaction(summaryCaptor.capture(), hasExtendedScopeCaptor.capture());
        
        assertEquals(summaryCaptor.getAllValues().get(0),nerSummary);
        assertEquals(summaryCaptor.getAllValues().get(1),natSummary);
        assertEquals(hasExtendedScopeCaptor.getAllValues().get(1),isAdmin);
        assertEquals(hasExtendedScopeCaptor.getAllValues().get(0),true);
        
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        if (isAdmin) {
            verify(taskRepository, Mockito.times(1)).save(taskCaptor.capture());
        } else {
            verify(taskRepository, Mockito.times(0)).save(taskCaptor.capture());
        }


    }
    
    
    @Test
    void proposeMultipleTransactionsByAuthrorizedRepsWithSinglePersonRequired() {
        // given
        String transferringAccountFullIdentifier = "UK-100-10000088-0-9";
        
        SignedTransactionSummary nerSummary = new SignedTransactionSummary();
        nerSummary.setTransferringAccountFullIdentifier(transferringAccountFullIdentifier);
        nerSummary.setTransferringAccountIdentifier(10000088L);
        nerSummary.setAcquiringAccountFullIdentifier("UK-100-10000002-0-51");
        nerSummary.setAcquiringAccountIdentifier(10000002L);
        nerSummary.setAllocationType(AllocationType.NER);
        nerSummary.setIdentifier("UK100289");
        nerSummary.setType(TransactionType.ExcessAllocation);
        nerSummary.setAdditionalAttributes(Map.of(ExcessAllocationProcessor.RELATED_NAT_TRANSACTION_IDENTIFER,"UK100290"));
        
        SignedTransactionSummary natSummary = new SignedTransactionSummary();
        natSummary.setTransferringAccountFullIdentifier(transferringAccountFullIdentifier);
        natSummary.setTransferringAccountIdentifier(10000088L);
        natSummary.setAcquiringAccountFullIdentifier("UK-100-10000003-0-46");
        natSummary.setAcquiringAccountIdentifier(10000003L);
        natSummary.setAllocationType(AllocationType.NAT);
        natSummary.setIdentifier("UK100290");
        natSummary.setType(TransactionType.ExcessAllocation);
        natSummary.setAdditionalAttributes(Map.of(ExcessAllocationProcessor.IS_TRIGGERED_BY_NER_FINALIZATION,Boolean.TRUE));
        
        List<SignedTransactionSummary> proposals = new ArrayList<>();
        proposals.add(nerSummary);
        proposals.add(natSummary);

        
        BusinessCheckResult nerResult = new BusinessCheckResult();
        nerResult.setTransactionIdentifier(nerSummary.getIdentifier());
        nerResult.setApprovalRequired(false);
        
        BusinessCheckResult natResult = new BusinessCheckResult();
        natResult.setTransactionIdentifier(natSummary.getIdentifier());
        natResult.setApprovalRequired(true);
        
        User currentUser = new User();
        currentUser.setUrid("UK873533391953");
        
        //Mocking methods
        Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)).thenReturn(false);
        Mockito.when(transactionService.proposeSignedTransaction(nerSummary,false)).thenReturn(nerResult);
        Mockito.when(transactionService.proposeSignedTransaction(natSummary,true)).thenReturn(natResult);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        
        // when
        BusinessCheckResult result = transactionWithTaskService.proposeMultipleTransactions(proposals);
        
        // then
        assertNotNull(result);
        assertEquals(false, result.getApprovalRequired());
        assertNull(result.getRequestIdentifier());
        
        InOrder order = inOrder(eventService, transactionService);

        ArgumentCaptor<SignedTransactionSummary> summaryCaptor = ArgumentCaptor.forClass(SignedTransactionSummary.class);
        ArgumentCaptor<Boolean> hasExtendedScopeCaptor = ArgumentCaptor.forClass(Boolean.class);
        
        //Verify the call to proposeSignedTransaction
        order.verify(transactionService,times(2)).proposeSignedTransaction(summaryCaptor.capture(), hasExtendedScopeCaptor.capture());
        
        assertEquals(summaryCaptor.getAllValues().get(0),nerSummary);
        assertEquals(summaryCaptor.getAllValues().get(1),natSummary);
        assertEquals(hasExtendedScopeCaptor.getAllValues().get(0),false);
        assertEquals(hasExtendedScopeCaptor.getAllValues().get(1),true);
        
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository, Mockito.times(0)).save(taskCaptor.capture());


    }
}
