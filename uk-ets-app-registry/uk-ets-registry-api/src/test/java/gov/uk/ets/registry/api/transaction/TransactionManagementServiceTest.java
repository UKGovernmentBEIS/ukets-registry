package gov.uk.ets.registry.api.transaction;

import static gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.auditevent.domain.types.TransactionEventType;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.reports.ReportRequestService;
import gov.uk.ets.registry.api.common.reports.ReportRoleMappingService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilterFactory;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.repository.SearchableTransactionRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionManagementService;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchCriteria;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TransactionManagementServiceTest {
    @Mock
    private TransactionFilterFactory transactionFilterFactory;

    @Mock
    private SearchableTransactionRepository searchableTransactionRepository;

    @Mock
    private TransactionSearchCriteria criteria;

    @Mock
    private Pageable pageable;

    @Mock
    private TransactionFilter filter;

    private TransactionManagementService transactionManagementService;

    @Mock
    private AccountService accountService;

    @Mock
    private TaskService taskService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private ARAccountAccessRepository accountAccessRepository;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @Mock
    private ITLNoticeManagementService itlNoticeManagementService;

    @Mock
    private TransactionReversalService transactionReversalService;

    @Mock
    private ReportRequestService reportRequestService;
    
    @Mock
    private ReportRoleMappingService reportRoleMappingService;

    @Mock
    private AccountAccessService accountAccessService;

    @BeforeEach
    public void setup() {
        transactionManagementService = new TransactionManagementService(transactionFilterFactory,
            searchableTransactionRepository,
            accountService,
            taskService,
            transactionService,
            authorizationService,
            accountAccessRepository,
            userService,
            eventService,
            itlNoticeManagementService,
            transactionReversalService,
            reportRequestService,
            reportRoleMappingService,
            accountAccessService
        );
    }

    @Test
    void search() {
        // given
        given(transactionFilterFactory.createTransactionFilter(any())).willReturn(filter);
        // when
        transactionManagementService.search(criteria, pageable, false);
        // then
        then(searchableTransactionRepository).should(times(1)).search(filter, pageable);
        then(searchableTransactionRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Convert Transaction object to TransactionSummary object, expected to succeed")
    void getTransactionSummary() {
        given(accountAccessService.checkAccountAccess(any())).willReturn(true);
        long taskIdentifier = 1000000L;
        given(taskService.getTaskIdentifier(any())).willReturn(taskIdentifier);

        String transferringAccountFullIdentifier = "UK-100-10000047-2-14";
        given(accountService.getAccountDisplayName(transferringAccountFullIdentifier))
            .willReturn("Transferring Account Name");

        String acquiringAccountFullIdentifier = "JP-100-123456-0-12";
        given(accountService.getAccountDisplayName(acquiringAccountFullIdentifier))
            .willReturn("Acquiring Account Name");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setIdentifier("GB100002");
        transaction.setLastUpdated(new Date());
        transaction.setStatus(COMPLETED);
        transaction.setQuantity(110L);
        transaction.setBlocks(new ArrayList<>());
        transaction.setResponseEntries(new ArrayList<>());
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountFullIdentifier(transferringAccountFullIdentifier);
        transaction.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountFullIdentifier(acquiringAccountFullIdentifier);
        transaction.setAcquiringAccount(acquiringAccount);
        transaction.setExecutionDate(LocalDateTime.now());

        assertNotNull(transactionManagementService.getTransactionSummary(transaction));
    }
    
    @Test
    @DisplayName("Convert Transaction object with empty execution date to TransactionSummary object, expected to succeed")
    void getTransactionSummaryNullExecutionDate() {
        given(accountAccessService.checkAccountAccess(any())).willReturn(true);
        long taskIdentifier = 1000000L;
        given(taskService.getTaskIdentifier(any())).willReturn(taskIdentifier);

        String transferringAccountFullIdentifier = "UK-100-10000047-2-14";
        given(accountService.getAccountDisplayName(transferringAccountFullIdentifier))
            .willReturn("Transferring Account Name");

        String acquiringAccountFullIdentifier = "JP-100-123456-0-12";
        given(accountService.getAccountDisplayName(acquiringAccountFullIdentifier))
            .willReturn("Acquiring Account Name");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setIdentifier("GB100002");
        transaction.setLastUpdated(new Date());
        transaction.setStatus(COMPLETED);
        transaction.setQuantity(110L);
        transaction.setBlocks(new ArrayList<>());
        transaction.setResponseEntries(new ArrayList<>());
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountFullIdentifier(transferringAccountFullIdentifier);
        transaction.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountFullIdentifier(acquiringAccountFullIdentifier);
        transaction.setAcquiringAccount(acquiringAccount);

        assertNotNull(transactionManagementService.getTransactionSummary(transaction));
    }

    @Test
    @DisplayName("Test case for a transaction summary with an external acquiring account, expected to succeed")
    void getTransactionSummaryWithExternalAcquiringAccount() {
        given(accountAccessService.checkAccountAccess(any())).willReturn(true);
        String transferringAccountFullIdentifier = "UK-100-10000047-2-14";
        given(accountService.getAccountDisplayName(transferringAccountFullIdentifier))
            .willReturn("Transferring Account Name");

        String acquiringAccountFullIdentifier = "JP-100-10000047-0-12";
        given(accountService.getAccountDisplayName(acquiringAccountFullIdentifier))
            .willReturn("Acquiring Account Name");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setIdentifier("GB100002");
        transaction.setLastUpdated(new Date());
        transaction.setStatus(COMPLETED);
        transaction.setQuantity(110L);
        transaction.setBlocks(new ArrayList<>());
        transaction.setResponseEntries(new ArrayList<>());
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountFullIdentifier(transferringAccountFullIdentifier);
        transaction.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccountInfo = new AccountBasicInfo();
        acquiringAccountInfo.setAccountFullIdentifier(acquiringAccountFullIdentifier);
        transaction.setAcquiringAccount(acquiringAccountInfo);
        transaction.setExecutionDate(LocalDateTime.now());

        //Since the acquiring account is an external account, the following repository call will return null
        given(accountService.getAccountFullIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier())).willReturn(null);
        TransactionSummary transactionSummary = transactionManagementService.getTransactionSummary(transaction);

        assertTrue(transactionSummary.isExternalAcquiringAccount());
    }
    
    @Test
    void getTransactionHistoryVisibleToARsOfTheAcquiringAccount() {
    	User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setUrid("UK111111111111");
    	Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)).thenReturn(true);
    	
    	final String transactionIdentifier = "XXXXXX";
    	
    	AuditEventDTO systemDto1 = new AuditEventDTO(String.valueOf(transactionIdentifier), TransactionEventType.TRANSACTION_DELAYED.getEventAction(),
        		"system event description 1", "system event creator 1", "system", new Date());
    	AuditEventDTO systemDto2 = new AuditEventDTO(String.valueOf(transactionIdentifier), TransactionEventType.TRANSACTION_COMPLETED.getEventAction(),
        		"system event description 2", "system event creator 2", "system", new Date());
    	List<AuditEventDTO> transactionSystemEvents = new ArrayList<>();
    	transactionSystemEvents.addAll(List.of(systemDto1, systemDto2));
    	given(eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(String.valueOf(transactionIdentifier),
                List.of(Transaction.class.getName(), TransactionService.class.getName()))).willReturn(transactionSystemEvents);
    	
    	Transaction transaction = new Transaction();
    	transaction.setIdentifier(transactionIdentifier);
    	AccountBasicInfo transferringAccount = new AccountBasicInfo();
    	transferringAccount.setAccountIdentifier(1L);
    	transaction.setTransferringAccount(transferringAccount);
    	
    	AccountAccess transferringAccountAccess = new AccountAccess();
    	User user = new User();
        user.setUrid("UK222222222222");
        transferringAccountAccess.setUser(user);
        transferringAccountAccess.setState(AccountAccessState.ACTIVE);
        transferringAccountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        
    	given(transactionService.getTransaction(transactionIdentifier)).willReturn(transaction);
    	given(userService.getCurrentUser()).willReturn(currentUser);
    	given(accountAccessRepository.fetchARs(Mockito.anyLong(), Mockito.eq(AccountAccessState.ACTIVE))).willReturn(List.of(transferringAccountAccess));
    	
    	List<AuditEventDTO> transactionHistory = transactionManagementService.getTransactionHistory(transactionIdentifier);
    	
    	assertEquals(1, transactionHistory.size());
    	assertEquals(0, transactionHistory.stream().filter(history -> TransactionEventType.TRANSACTION_DELAYED.getEventAction().equals(history.getDomainAction())).count());
    }
    
    @Test
    void getTransactionHistoryVisibleToActiveARsOfTheTransferringAccount() {
    	User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setUrid("UK222222222222");
    	Mockito.when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)).thenReturn(true);
    	
    	final String transactionIdentifier = "XXXXXX";
    	
    	AuditEventDTO systemDto1 = new AuditEventDTO(String.valueOf(transactionIdentifier), TransactionEventType.TRANSACTION_DELAYED.getEventAction(),
        		"system event description 1", "system event creator 1", "system", new Date());
    	AuditEventDTO systemDto2 = new AuditEventDTO(String.valueOf(transactionIdentifier), TransactionEventType.TRANSACTION_COMPLETED.getEventAction(),
        		"system event description 2", "system event creator 2", "system", new Date());
    	List<AuditEventDTO> transactionSystemEvents = new ArrayList<>();
    	transactionSystemEvents.addAll(List.of(systemDto1, systemDto2));
    	given(eventService.getSystemEventsByDomainIdOrderByCreationDateDesc(String.valueOf(transactionIdentifier),
                List.of(Transaction.class.getName(), TransactionService.class.getName()))).willReturn(transactionSystemEvents);
    	
    	Transaction transaction = new Transaction();
    	transaction.setIdentifier(transactionIdentifier);
    	AccountBasicInfo transferringAccount = new AccountBasicInfo();
    	transferringAccount.setAccountIdentifier(1L);
    	transaction.setTransferringAccount(transferringAccount);
    	
    	AccountAccess transferringAccountAccess = new AccountAccess();
    	User user = new User();
        user.setUrid("UK222222222222");
        transferringAccountAccess.setUser(user);
        transferringAccountAccess.setState(AccountAccessState.ACTIVE);
        transferringAccountAccess.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        
    	given(transactionService.getTransaction(transactionIdentifier)).willReturn(transaction);
    	given(userService.getCurrentUser()).willReturn(currentUser);
    	given(accountAccessRepository.fetchARs(Mockito.anyLong(), Mockito.eq(AccountAccessState.ACTIVE))).willReturn(List.of(transferringAccountAccess));
    	
    	List<AuditEventDTO> transactionHistory = transactionManagementService.getTransactionHistory(transactionIdentifier);
    	
    	assertEquals(2, transactionHistory.size());
    }
}
