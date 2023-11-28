package gov.uk.ets.registry.api.transaction.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionProcessState;
import gov.uk.ets.registry.api.transaction.domain.TransactionProcessStateTransition;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.messaging.UKTLTransactionAnswer;
import gov.uk.ets.registry.api.transaction.processor.ExcessAllocationProcessor;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessETSTransactionServiceTest {
    private ProcessETSTransactionService processETSTransactionService;

    @Mock
    private TransactionService transactionService;
    @Mock
    private EventService eventService;
    @Mock
    private TransactionMessageService transactionMessageService;
    @Mock
    ComplianceEventService complianceEventService;
    @Mock
    AccountRepository accountRepository;
    @Mock
    AccountService accountService;
    @Mock
    TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        processETSTransactionService = new ProcessETSTransactionService(transactionService, eventService,
            transactionMessageService, complianceEventService, accountRepository, accountService,
            taskRepository);
    }

    @Test
    void process() {
        // given
        UKTLTransactionAnswer uktlTransactionAnswer = new UKTLTransactionAnswer();
        uktlTransactionAnswer.setTransactionIdentifier("uk123123");
        uktlTransactionAnswer.setTransactionStatusCode(10);
        uktlTransactionAnswer.setErrors(List.of(new BusinessCheckError(12, "dummy error")));

        Transaction transaction = new Transaction();
        transaction.setIdentifier(uktlTransactionAnswer.getTransactionIdentifier());
        transaction.setStatus(TransactionStatus.PROPOSED);
        TransactionProcessStateTransition transition = TransactionProcessStateTransition
            .builder()
            .transaction(transaction)
            .nextState(TransactionProcessState.TERMINATE)
            .nextStatus(TransactionStatus.CHECKED_DISCREPANCY)
            .build();
        given(transactionService
            .getTransactionProcessStateTransition(uktlTransactionAnswer.getTransactionIdentifier(),
                uktlTransactionAnswer.getTransactionStatusCode()))
            .willReturn(transition);

        // when
        processETSTransactionService.process(uktlTransactionAnswer);

        // then
        InOrder order = inOrder(eventService, transactionService);
        order.verify(eventService).createAndPublishEvent(eq(transaction.getIdentifier()),
            eq("UKTL"),
            nullable(String.class),
            eq(EventType.TRANSACTION_UKTL_REPLY),
            eq(transition.getNextState().getDescription()));

        ArgumentCaptor<TransactionResponseDTO> dtoCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        order.verify(transactionService).saveTransactionResponse(dtoCaptor.capture());
        TransactionResponseDTO dto = dtoCaptor.getValue();
        assertEquals(transaction.getIdentifier(), dto.getTransaction().getIdentifier());
        assertEquals(uktlTransactionAnswer.getErrors(), dto.getErrors());

        ArgumentCaptor<TransactionProcessStateTransition> transitionCaptor =
            ArgumentCaptor.forClass(TransactionProcessStateTransition.class);
        order.verify(transactionService).process(transitionCaptor.capture());
        assertEquals(transition, transitionCaptor.getValue());
    }
    
    @Test
    void processInternalTransferSuccess() {
        // given
        UKTLTransactionAnswer uktlTransactionAnswer = new UKTLTransactionAnswer();
        uktlTransactionAnswer.setTransactionIdentifier("UK123123");
        uktlTransactionAnswer.setTransactionStatusCode(2);

        Transaction transaction = new Transaction();
        transaction.setIdentifier(uktlTransactionAnswer.getTransactionIdentifier());
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.InternalTransfer);
        TransactionProcessStateTransition transition = TransactionProcessStateTransition
            .builder()
            .transaction(transaction)
            .nextState(TransactionProcessState.FINALISE)
            .nextStatus(TransactionStatus.CHECKED_NO_DISCREPANCY)
            .build();
        given(transactionService
            .getTransactionProcessStateTransition(uktlTransactionAnswer.getTransactionIdentifier(),
                uktlTransactionAnswer.getTransactionStatusCode()))
            .willReturn(transition);
        
        // when
        processETSTransactionService.process(uktlTransactionAnswer);

        // then
        InOrder order = inOrder(eventService, transactionService);
        order.verify(eventService).createAndPublishEvent(eq(transaction.getIdentifier()),
            eq("UKTL"),
            nullable(String.class),
            eq(EventType.TRANSACTION_UKTL_REPLY),
            eq(transition.getNextState().getDescription()));

        ArgumentCaptor<TransactionResponseDTO> dtoCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        order.verify(transactionService).saveTransactionResponse(dtoCaptor.capture());
        TransactionResponseDTO dto = dtoCaptor.getValue();
        assertEquals(transaction.getIdentifier(), dto.getTransaction().getIdentifier());
        assertEquals(uktlTransactionAnswer.getErrors(), dto.getErrors());

        ArgumentCaptor<TransactionProcessStateTransition> transitionCaptor =
            ArgumentCaptor.forClass(TransactionProcessStateTransition.class);
        order.verify(transactionService).process(transitionCaptor.capture());
        assertEquals(transition, transitionCaptor.getValue());
        
        //Nothing else to do
        order.verifyNoMoreInteractions();
    }

    
    @Test
    void processNatOnlySuccess() {
        // given
        UKTLTransactionAnswer uktlTransactionAnswer = new UKTLTransactionAnswer();
        uktlTransactionAnswer.setTransactionIdentifier("UK123123");
        uktlTransactionAnswer.setTransactionStatusCode(2);

        Transaction transaction = new Transaction();
        transaction.setIdentifier(uktlTransactionAnswer.getTransactionIdentifier());
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.ExcessAllocation);
        TransactionProcessStateTransition transition = TransactionProcessStateTransition
            .builder()
            .transaction(transaction)
            .nextState(TransactionProcessState.FINALISE)
            .nextStatus(TransactionStatus.CHECKED_NO_DISCREPANCY)
            .build();
        given(transactionService
            .getTransactionProcessStateTransition(uktlTransactionAnswer.getTransactionIdentifier(),
                uktlTransactionAnswer.getTransactionStatusCode()))
            .willReturn(transition);
        
        // when
        processETSTransactionService.process(uktlTransactionAnswer);

        // then
        InOrder order = inOrder(eventService, transactionService);
        order.verify(eventService).createAndPublishEvent(eq(transaction.getIdentifier()),
            eq("UKTL"),
            nullable(String.class),
            eq(EventType.TRANSACTION_UKTL_REPLY),
            eq(transition.getNextState().getDescription()));

        ArgumentCaptor<TransactionResponseDTO> dtoCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        order.verify(transactionService).saveTransactionResponse(dtoCaptor.capture());
        TransactionResponseDTO dto = dtoCaptor.getValue();
        assertEquals(transaction.getIdentifier(), dto.getTransaction().getIdentifier());
        assertEquals(uktlTransactionAnswer.getErrors(), dto.getErrors());

        ArgumentCaptor<TransactionProcessStateTransition> transitionCaptor =
            ArgumentCaptor.forClass(TransactionProcessStateTransition.class);
        order.verify(transactionService).process(transitionCaptor.capture());
        assertEquals(transition, transitionCaptor.getValue());
        
        //Nothing else to do
        order.verifyNoMoreInteractions();
    }
    
    @Test
    void processNerFailure() {
        // given
        String relatedNAT_ReturnExcessAllowanceIdentifier = "UK123124";
        UKTLTransactionAnswer uktlTransactionAnswer = new UKTLTransactionAnswer();
        uktlTransactionAnswer.setTransactionIdentifier("UK123123");
        uktlTransactionAnswer.setTransactionStatusCode(10);
        uktlTransactionAnswer.setErrors(List.of(new BusinessCheckError(12, "dummy error")));

        Transaction transaction = new Transaction();
        transaction.setIdentifier(uktlTransactionAnswer.getTransactionIdentifier());
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.ExcessAllocation);
        try {
            transaction.setAttributes(new ObjectMapper().writeValueAsString(Map.of(AllocationType.class.getSimpleName(),AllocationType.NER,
                AllocationYear.class.getSimpleName(), 2023,
                ExcessAllocationProcessor.RELATED_NAT_TRANSACTION_IDENTIFER, relatedNAT_ReturnExcessAllowanceIdentifier)));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TransactionProcessStateTransition transition = TransactionProcessStateTransition
            .builder()
            .transaction(transaction)
            .nextState(TransactionProcessState.TERMINATE)
            .nextStatus(TransactionStatus.CHECKED_DISCREPANCY)
            .build();
        given(transactionService
            .getTransactionProcessStateTransition(uktlTransactionAnswer.getTransactionIdentifier(),
                uktlTransactionAnswer.getTransactionStatusCode()))
            .willReturn(transition);
        
        // when
        processETSTransactionService.process(uktlTransactionAnswer);

        // then
        InOrder order = inOrder(eventService, transactionService);
        order.verify(eventService).createAndPublishEvent(eq(transaction.getIdentifier()),
            eq("UKTL"),
            nullable(String.class),
            eq(EventType.TRANSACTION_UKTL_REPLY),
            eq(transition.getNextState().getDescription()));

        ArgumentCaptor<TransactionResponseDTO> dtoCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        order.verify(transactionService).saveTransactionResponse(dtoCaptor.capture());
        TransactionResponseDTO dto = dtoCaptor.getValue();
        assertEquals(transaction.getIdentifier(), dto.getTransaction().getIdentifier());
        assertEquals(uktlTransactionAnswer.getErrors(), dto.getErrors());

        ArgumentCaptor<TransactionProcessStateTransition> transitionCaptor =
            ArgumentCaptor.forClass(TransactionProcessStateTransition.class);
        order.verify(transactionService).process(transitionCaptor.capture());
        assertEquals(transition, transitionCaptor.getValue());
        
        
        //Terminate also the NAT return.
        ArgumentCaptor<String> natTransactionIdentifierCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TaskOutcome> taskOutcomeCaptor = ArgumentCaptor.forClass(TaskOutcome.class);
        ArgumentCaptor<Boolean> extendedScopeCaptor = ArgumentCaptor.forClass(Boolean.class);
        order.verify(transactionService).finaliseTransaction(natTransactionIdentifierCaptor.capture(),
            taskOutcomeCaptor.capture(),
            extendedScopeCaptor.capture());
        assertEquals(relatedNAT_ReturnExcessAllowanceIdentifier, natTransactionIdentifierCaptor.getValue());
        assertEquals(TaskOutcome.REJECTED, taskOutcomeCaptor.getValue());
        assertEquals(false, extendedScopeCaptor.getValue());
    }
    
    
    @Test
    void processNerSuccess() {
        // given
        String relatedNAT_ReturnExcessAllowanceIdentifier = "UK123124";
        UKTLTransactionAnswer uktlTransactionAnswer = new UKTLTransactionAnswer();
        uktlTransactionAnswer.setTransactionIdentifier("UK123123");
        uktlTransactionAnswer.setTransactionStatusCode(2);

        Transaction transaction = new Transaction();
        transaction.setIdentifier(uktlTransactionAnswer.getTransactionIdentifier());
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setType(TransactionType.ExcessAllocation);
        try {
            transaction.setAttributes(new ObjectMapper().writeValueAsString(Map.of(AllocationType.class.getSimpleName(),AllocationType.NER,
                AllocationYear.class.getSimpleName(), 2023,
                ExcessAllocationProcessor.RELATED_NAT_TRANSACTION_IDENTIFER, relatedNAT_ReturnExcessAllowanceIdentifier)));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TransactionProcessStateTransition transition = TransactionProcessStateTransition
            .builder()
            .transaction(transaction)
            .nextState(TransactionProcessState.FINALISE)
            .nextStatus(TransactionStatus.CHECKED_NO_DISCREPANCY)
            .build();
        given(transactionService
            .getTransactionProcessStateTransition(uktlTransactionAnswer.getTransactionIdentifier(),
                uktlTransactionAnswer.getTransactionStatusCode()))
            .willReturn(transition);
        
        Transaction natTransaction = new Transaction();
        natTransaction.setStatus(TransactionStatus.AWAITING_APPROVAL);
        natTransaction.setIdentifier(relatedNAT_ReturnExcessAllowanceIdentifier);
        given(transactionService
            .getTransaction(relatedNAT_ReturnExcessAllowanceIdentifier))
            .willReturn(natTransaction);
        
        // when
        processETSTransactionService.process(uktlTransactionAnswer);

        // then
        InOrder order = inOrder(eventService, transactionService);
        order.verify(eventService).createAndPublishEvent(eq(transaction.getIdentifier()),
            eq("UKTL"),
            nullable(String.class),
            eq(EventType.TRANSACTION_UKTL_REPLY),
            eq(transition.getNextState().getDescription()));

        ArgumentCaptor<TransactionResponseDTO> dtoCaptor = ArgumentCaptor.forClass(TransactionResponseDTO.class);
        order.verify(transactionService).saveTransactionResponse(dtoCaptor.capture());
        TransactionResponseDTO dto = dtoCaptor.getValue();
        assertEquals(transaction.getIdentifier(), dto.getTransaction().getIdentifier());
        assertEquals(uktlTransactionAnswer.getErrors(), dto.getErrors());

        ArgumentCaptor<TransactionProcessStateTransition> transitionCaptor =
            ArgumentCaptor.forClass(TransactionProcessStateTransition.class);
        order.verify(transactionService).process(transitionCaptor.capture());
        assertEquals(transition, transitionCaptor.getValue());
        
        //Find the nat transaction
        ArgumentCaptor<String> natTransactionIdentifierCaptor = ArgumentCaptor.forClass(String.class);
        order.verify(transactionService).getTransaction(natTransactionIdentifierCaptor.capture());
        assertEquals(relatedNAT_ReturnExcessAllowanceIdentifier, natTransactionIdentifierCaptor.getValue());
        
        //start the NAT transaction.
        ArgumentCaptor<Transaction> natTransactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        order.verify(transactionService).startTransaction(natTransactionCaptor.capture());
        assertEquals(natTransaction, natTransactionCaptor.getValue());
    }
}
