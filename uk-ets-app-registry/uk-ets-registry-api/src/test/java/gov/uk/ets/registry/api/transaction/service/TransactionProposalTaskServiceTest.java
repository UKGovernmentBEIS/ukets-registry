package gov.uk.ets.registry.api.transaction.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import gov.uk.ets.registry.api.account.authz.AccountAuthorizationService;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.web.model.AllTransactionTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionProposalTaskServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    @Mock
    private AccountAuthorizationService accountAuthorizationService;

    @Mock
    private TransactionWithTaskService transactionWithTaskService;

    @Mock
    private EventService eventService;
    
    @Mock
    private Set<TransactionTypeTaskService> transactionTypeTaskServices;
    
    @Mock
    private User currentUser;
    
    @InjectMocks
    private TransactionProposalTaskService cut;
    
    private AllTransactionTaskDetailsDTO task;
    
    private final String NER_TransactionIdentifier = "UK100277";
    private final String NAT_TransactionIdentifier = "UK100278";
    
    @BeforeEach
    void setUp() {

        TaskDetailsDTO taskDetails = new TaskDetailsDTO();
        task = new AllTransactionTaskDetailsDTO(taskDetails,TransactionType.ExcessAllocation,"reference");
        task.setTransactionIdentifiers(List.of(NAT_TransactionIdentifier,NER_TransactionIdentifier));
        
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)).thenReturn(false);
        

        Transaction returnNERTransaction = new Transaction();
        returnNERTransaction.setIdentifier(NER_TransactionIdentifier);
        returnNERTransaction.setType(TransactionType.ExcessAllocation);
        returnNERTransaction.setAttributes("{\"AllocationYear\":2022,\"AllocationType\":\"NER\"}");
        when(transactionService.getTransaction(NER_TransactionIdentifier)).thenReturn(returnNERTransaction);
        when(transactionService.getTransactionWithBlocks(NER_TransactionIdentifier)).thenReturn(returnNERTransaction);
        
        Transaction returnNATTransaction = new Transaction();
        returnNATTransaction.setIdentifier(NAT_TransactionIdentifier);
        returnNATTransaction.setType(TransactionType.ExcessAllocation);
        returnNATTransaction.setAttributes("{\"AllocationYear\":2022,\"AllocationType\":\"NAT\"}");
        when(transactionService.getTransaction(NAT_TransactionIdentifier)).thenReturn(returnNATTransaction);
        when(transactionService.getTransactionWithBlocks(NAT_TransactionIdentifier)).thenReturn(returnNATTransaction);
        
        BusinessCheckResult finaliseResult = new BusinessCheckResult();
        when(transactionService.finaliseTransaction(NER_TransactionIdentifier,TaskOutcome.REJECTED,false)).thenReturn(finaliseResult);
    }    
    
    @Test
    void shouldRejectAllLinkedTransactions() {
        TaskOutcome taskOutcome = TaskOutcome.REJECTED;
        String comment = "A comment";
        TaskCompleteResponse complete = cut.complete(task, taskOutcome, comment);

        assertThat(complete).isNotNull();

        InOrder order = inOrder(transactionService);
        order.verify(transactionService).finaliseTransaction(NER_TransactionIdentifier, taskOutcome, false);
        order.verify(transactionService).finaliseTransaction(NAT_TransactionIdentifier, taskOutcome, false);

        order.verifyNoMoreInteractions();
    }
}
