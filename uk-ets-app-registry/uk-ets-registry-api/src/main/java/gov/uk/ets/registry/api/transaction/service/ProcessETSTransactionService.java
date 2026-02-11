package gov.uk.ets.registry.api.transaction.service;

import static java.time.ZoneOffset.UTC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionProcessState;
import gov.uk.ets.registry.api.transaction.domain.TransactionProcessStateTransition;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionSystem;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderEvent;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderReversalEvent;
import gov.uk.ets.registry.api.transaction.messaging.UKTLTransactionAnswer;
import gov.uk.ets.registry.api.transaction.processor.ExcessAllocationProcessor;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;



/**
 * Service for processing ETS transactions.
 */
@Service
@Log4j2
@AllArgsConstructor
public class ProcessETSTransactionService {

    private final TransactionService transactionService;
    private final EventService eventService;
    private final TransactionMessageService transactionMessageService;
    private final ComplianceEventService complianceEventService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final TaskRepository taskRepository;

    /**
     * Processes the transaction according to UKTL answer.
     *
     * @param uktlTransactionAnswer The UKTL answer data transfer object
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @EmitsGroupNotifications({GroupNotificationType.TRANSACTION_RECEIVED,
        GroupNotificationType.TRANSACTION_COMPLETION})
    public void process(UKTLTransactionAnswer uktlTransactionAnswer) {
        TransactionProcessStateTransition transition = transactionService
            .getTransactionProcessStateTransition(uktlTransactionAnswer.getTransactionIdentifier(),
                uktlTransactionAnswer.getTransactionStatusCode());

        Transaction transaction = transition.getTransaction();
        transactionMessageService.saveInboundMessage(transaction, transition.getNextStatus().name(),
            TransactionSystem.UKTL, uktlTransactionAnswer);

        if (transaction.getStatus().isFinal()) {
            log.info("Transaction {} is already in status {}. This Transaction Log message will be not be processed {}",
                transaction.getIdentifier(), transaction.getStatus(), uktlTransactionAnswer);
            uktlTransactionAnswer.setTransactionStatusCode(transaction.getStatus().getCode());
            return;
        }

        eventService.createAndPublishEvent(
            transaction.getIdentifier(),
            "UKTL",
            null,
            EventType.TRANSACTION_UKTL_REPLY,
            transition.getNextState().getDescription());

        transactionService.saveTransactionResponse(
            TransactionResponseDTO.builder()
                .transaction(transaction)
                .errors(uktlTransactionAnswer.getErrors())
                .build());

        transactionService.process(transition);

        handleComplianceEvents(transaction);

        if (TransactionType.BalanceInstallationTransferAllowances.equals(transaction.getType()) &&
            TransactionProcessState.FINALISE.equals(transition.getNextState())) {
            handleInstallationTransferActions(transaction.getTransferringAccount().getAccountIdentifier());
        }
        
        if (TransactionType.ExcessAllocation.equals(transaction.getType())) {
            handleRelatedNatReturnExcessAllocation(getRelatedNatReturnExcessAllocationTransaction(transaction),transition.getNextState());
        }
    }
    
    protected Optional<String> getRelatedNatReturnExcessAllocationTransaction(Transaction transaction) {

        if (Optional.ofNullable(transaction.getAttributes()).isPresent()) {
            try {
                @SuppressWarnings("unchecked")
                Map<String,Serializable> attrs = new ObjectMapper().readValue(transaction.getAttributes(), Map.class);
                String  natTransactionIdentifier = (String) attrs.get(ExcessAllocationProcessor.RELATED_NAT_TRANSACTION_IDENTIFER);
                if (Objects.nonNull(natTransactionIdentifier)) {
                    return Optional.of(natTransactionIdentifier);
                } else {
                    return Optional.empty();
                }
            } catch (JsonProcessingException exception) {
                throw new TransactionExecutionException(this.getClass(),
                    "Error when de-serialising additional transaction attributes.", exception);
            }
        }
        return Optional.empty();
    }

    private void handleRelatedNatReturnExcessAllocation(Optional<String> natTransactionIdentifierOptional, TransactionProcessState transactionProcessState) {

        if (natTransactionIdentifierOptional.isPresent()) {
            String natTransactionIdentifier = natTransactionIdentifierOptional.get();
            if (TransactionProcessState.FINALISE.equals(transactionProcessState)) {      
                //Check if there is a nat transaction with status AWAITING_APPROVAL 
                Transaction transaction = transactionService.getTransaction(natTransactionIdentifier);
                if (TransactionStatus.AWAITING_APPROVAL.equals(transaction.getStatus())) {
                    transactionService.startTransaction(transaction);                        
                }        
            } else {
                //Terminate also the NAT return.
                transactionService.finaliseTransaction(natTransactionIdentifier, TaskOutcome.REJECTED, false);
            }           
        } 
    
    }

    /**
     * Sends compliance events in case of Surrender or Reversal of Surrender transactions.
     */
    private void handleComplianceEvents(Transaction transaction) {
        if (transaction.getType() == TransactionType.SurrenderAllowances) {

            Long compliantEntityId = getComplientEntitytId(transaction.getTransferringAccount().getAccountIdentifier());

            complianceEventService.processEvent(SurrenderEvent.builder()
                .actorId("system") // TODO should we find the user who initiated the task of the transaction?
                .compliantEntityId(compliantEntityId)
                .dateTriggered(LocalDateTime.now(ZoneId.of("UTC")))
                .dateRequested(calculateRequestedDate(transaction))
                .amount(transaction.getQuantity())
                .build());
        } else if (transaction.getType() == TransactionType.ReverseSurrenderAllowances) {
            Long compliantEntityId = getComplientEntitytId(transaction.getAcquiringAccount().getAccountIdentifier());

            complianceEventService.processEvent(SurrenderReversalEvent.builder()
                .actorId("system") // TODO should we find the user who initiated the task of the transaction?
                .compliantEntityId(compliantEntityId)
                .dateTriggered(LocalDateTime.now(ZoneId.of("UTC")))
                .dateRequested(calculateRequestedDate(transaction))
                .amount(transaction.getQuantity())
                .build());
        }
    }

    /**
     *  UKETS-5850: If a task is not found related to the transaction, just use the current date time.
     */
    private LocalDateTime calculateRequestedDate(Transaction transaction) {
        return taskRepository.findByTransactionIdentifier(transaction.getIdentifier())
            .map(task -> LocalDateTime.ofInstant(task.getCompletedDate().toInstant(), UTC))
            .orElse(LocalDateTime.now(UTC));
    }

    private Long getComplientEntitytId(Long accountIdentifier) {
        Account account = accountRepository.findByAccountIdentifierWithCompliantEntity(accountIdentifier)
            .orElseThrow(() -> new IllegalStateException(
                String.format("Account with identifier %s not found", accountIdentifier)));
        return account.getCompliantEntity().getIdentifier();
    }

    /**
     * Removes ARs and closes the account after successful balance transfer transaction.
     */
    private void handleInstallationTransferActions(Long accountIdentifier) {
        accountService.handleInstallationTransferActions(accountIdentifier);
    }
}
