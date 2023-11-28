package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.itl.notice.service.ITLNoticeManagementService;
import gov.uk.ets.registry.api.signing.service.SigningVerificationService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.TaskTransaction;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.repository.TaskTransactionRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.*;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionAttributes;
import gov.uk.ets.registry.api.transaction.domain.data.*;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryCode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.processor.ExcessAllocationProcessor;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;

import java.util.*;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Wrapper Service to create transactions with tasks within a single transaction.
 */
@Service
@AllArgsConstructor
public class TransactionWithTaskService {

    public static final String COMMENT_FORMAT = "%s (comment)";

    private final TransactionService transactionService;

    private final TaskRepository taskRepository;
    /**
     * Service for accounts.
     */
    private final AccountService accountService;

    private final TaskTransactionRepository taskTransactionRepository;

    private final UserService userService;
    /**
     * Service for authorization.
     */
    private final AuthorizationService authorizationService;

    private final EventService eventService;

    private final AccountConversionService accountConversionService;

    private final TransactionAccountService transactionAccountService;

    private final SigningVerificationService signingVerificationService;

    private final ITLNoticeManagementService itlNoticeManagementService;

    private final TaskEventService taskEventService;

    private final TransactionRepository transactionRepository;

    private final Mapper mapper;

    /**
     * Creates a transaction and its associated task.
     *
     * @param transaction The transaction with signature information.
     * @return The business check result.
     */
    //TODO Refactor this method in order to simplify the various nested if/else blocks
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.TRANSACTION_PROPOSAL)
    public BusinessCheckResult proposeTransaction(SignedTransactionSummary transaction) {
        signingVerificationService.verify(transaction.getSignatureInfo());
        BusinessCheckResult businessCheckResult = this.transactionService
            .proposeSignedTransaction(transaction, authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN));
        if (businessCheckResult.getApprovalRequired()) {
            Long accountIdentifier = transaction.getTransferringAccountIdentifier();
            if (TransactionType.IssueOfAAUsAndRMUs.equals(transaction.getType()) ||
                TransactionType.IssueAllowances.equals(transaction.getType())) {
                accountIdentifier = transaction.getAcquiringAccountIdentifier();
            }
            Long requestId =    createTransactionTask(businessCheckResult.getTransactionIdentifier(), accountIdentifier,
                transaction.getComment(), transaction.getAcquiringAccountFullIdentifier());
            businessCheckResult.setRequestIdentifier(requestId);
        }
        User currentUser = userService.getCurrentUser();
        String action = "Transaction proposal task requested";
        eventService.createAndPublishEvent(businessCheckResult.getTransactionIdentifier(), currentUser.getUrid(),
            transaction.getComment(), EventType.TRANSACTION_PROPOSAL, action);

        if (transaction.getType().equals(TransactionType.IssueOfAAUsAndRMUs)) {
            action = "Transaction proposal task";
            if (transaction.getComment() != null) {
                //Write a system event for the transaction comment
                eventService.createAndPublishEvent(transaction.getIdentifier(), null, transaction.getComment(),
                                                   EventType.TRANSACTION_TASK_COMMENT, String.format(COMMENT_FORMAT,
                                                                                                     action));
            }
        } else {
            action = "Transaction proposal task";
            String description = String.format("Transaction identifier %s. Acquiring account %s",
                transaction.getIdentifier(), transaction.getAcquiringAccountFullIdentifier());
            /* Adding event to Transferring Account */
            eventService.createAndPublishEvent(String.valueOf(transaction.getTransferringAccountIdentifier()),
                currentUser.getUrid(),
                description, EventType.ACCOUNT_TASK_REQUESTED, action);
            if (transaction.getComment() != null) {
                eventService.createAndPublishEvent(String.valueOf(transaction.getTransferringAccountIdentifier()),
                    currentUser.getUrid(),
                    transaction.getComment(), EventType.ACCOUNT_TASK_COMMENT, String.format(COMMENT_FORMAT, action));
                //Write a system event for the transaction comment
                eventService.createAndPublishEvent(transaction.getIdentifier(), null, transaction.getComment(),
                                                   EventType.TRANSACTION_PROPOSAL_COMMENT,
                                                   String.format(COMMENT_FORMAT, "Transaction proposal"));
            }
        }
        return businessCheckResult;
    }

    /**
     * Creates many transactions and its associated task if required.
     *
     * @param transaction The transaction with signature information.
     * @return The business check result.
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.TRANSACTION_PROPOSAL)
    public BusinessCheckResult proposeMultipleTransactions(List<SignedTransactionSummary> transactions) {
        
        if (transactions.stream().map(TransactionSummary::getTransferringAccountFullIdentifier).distinct().count() > 1) {
            throw new IllegalArgumentException("Only one transferring account  is supported");
        }
                
        if (!transactions.stream().map(TransactionSummary::getType).distinct().allMatch(t -> TransactionType.ExcessAllocation.equals(t))) {
            throw new IllegalArgumentException("Only ExcessAllocation is supported");
        }
        
        List<BusinessCheckResult> summariesCheckResults = new ArrayList<>();
        for (SignedTransactionSummary summary: transactions) {           
            signingVerificationService.verify(summary.getSignatureInfo());
            boolean hasExtendedScope = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
            
            //In case of Auth Rep the return to NAT should be triggered by the response of the NER so do not finalize
            if (!hasExtendedScope && isTriggeredByFinalization(summary)) {
                hasExtendedScope = true;
            }
            BusinessCheckResult summaryCheckResult = this.transactionService
                .proposeSignedTransaction(summary, hasExtendedScope);
            
            summariesCheckResults.add(summaryCheckResult);
            
            if (summaryCheckResult.getApprovalRequired().booleanValue()) {
                produceTransactionEvents(summary);
            }
        }
        
        if (summariesCheckResults.stream().map(BusinessCheckResult::getTransactionType).distinct().count() > 1) {
            throw new IllegalArgumentException("Only one transaction type is supportred");
        }
        
        Long accountIdentifier = transactions.stream().map(TransactionSummary::getTransferringAccountIdentifier).findFirst().orElseThrow();
        Optional<String> comment = transactions.stream().map(TransactionSummary::getComment).filter(Objects::nonNull).findFirst();
        
        BusinessCheckResult businessCheckResult = new BusinessCheckResult();
        String transactionsIds = transactions
            .stream()
            .map(t ->  t.getIdentifier() + " (" + t.getAllocationType() + ")")
            .collect(Collectors.joining(","));
        businessCheckResult.setTransactionIdentifier(transactionsIds);
        Optional<String> executionDate = summariesCheckResults.stream().map(BusinessCheckResult::getExecutionDate).filter(Objects::nonNull).findFirst();
        Optional<String> executionTime = summariesCheckResults.stream().map(BusinessCheckResult::getExecutionTime).filter(Objects::nonNull).findFirst();
        boolean isApprovalRequired = summariesCheckResults.stream().map(BusinessCheckResult::getApprovalRequired).filter(Objects::nonNull).allMatch(t -> t == Boolean.TRUE);
        businessCheckResult.setExecutionDate(executionDate.isPresent() ? executionDate.get() : null);
        businessCheckResult.setExecutionTime(executionTime.isPresent() ? executionTime.get() : null);
        businessCheckResult.setApprovalRequired(isApprovalRequired);
        
        List<TaskTransactionDTO>  batch = transactions.stream().map(t -> {
            TaskTransactionDTO transactionAcqAccountInfo = new TaskTransactionDTO();
            transactionAcqAccountInfo.setAcquiringAccountFullIdentifier(t.getAcquiringAccountFullIdentifier());
            transactionAcqAccountInfo.setTransactionIdentifier(t.getIdentifier());
            return transactionAcqAccountInfo;
        }).
            toList();
        //Create a task only if required
        if (businessCheckResult.getApprovalRequired().booleanValue()) {
            Long requestId = createMultipleTransactionTask(MultipleTransactionsTaskInfo.
                builder().
                accountIdentifier(accountIdentifier).
                comment(comment.isPresent() ? comment.get() : null).
                taskTransactions(batch).
                build()            
            );
            businessCheckResult.setRequestIdentifier(requestId);            
        }

        return businessCheckResult;
    }    
    
    /**
     * Returns true if there is an additional transaction attribute named 
     * TriggeredByFinalisation defined and has value true.
     * @param summary
     * @return
     */
    private boolean isTriggeredByFinalization(TransactionSummary summary) {
        if (Objects.nonNull(summary.getAdditionalAttributes()) &&
            Objects.nonNull(summary.getAdditionalAttributes().get(ExcessAllocationProcessor.IS_TRIGGERED_BY_NER_FINALIZATION))) {
            return (Boolean)summary.getAdditionalAttributes().get(ExcessAllocationProcessor.IS_TRIGGERED_BY_NER_FINALIZATION);
        }
        return false;
    }
    
    private void produceTransactionEvents(SignedTransactionSummary transaction) {
        User currentUser = userService.getCurrentUser();
        String action = "Transaction proposal task requested";
        eventService.createAndPublishEvent(transaction.getIdentifier(), currentUser.getUrid(),
            transaction.getComment(), EventType.TRANSACTION_PROPOSAL, action);

        if (transaction.getType().equals(TransactionType.IssueOfAAUsAndRMUs)) {
            action = "Transaction proposal task";
            if (transaction.getComment() != null) {
                //Write a system event for the transaction comment
                eventService.createAndPublishEvent(transaction.getIdentifier(), null, transaction.getComment(),
                                                   EventType.TRANSACTION_TASK_COMMENT, String.format(COMMENT_FORMAT,
                                                                                                     action));
            }
        } else {
            action = "Transaction proposal task";
            String description = String.format("Transaction identifier %s. Acquiring account %s",
                transaction.getIdentifier(), transaction.getAcquiringAccountFullIdentifier());
            /* Adding event to Transferring Account */
            eventService.createAndPublishEvent(String.valueOf(transaction.getTransferringAccountIdentifier()),
                currentUser.getUrid(),
                description, EventType.ACCOUNT_TASK_REQUESTED, action);
            if (transaction.getComment() != null) {
                eventService.createAndPublishEvent(String.valueOf(transaction.getTransferringAccountIdentifier()),
                    currentUser.getUrid(),
                    transaction.getComment(), EventType.ACCOUNT_TASK_COMMENT, String.format(COMMENT_FORMAT, action));
                //Write a system event for the transaction comment
                eventService.createAndPublishEvent(transaction.getIdentifier(), null, transaction.getComment(),
                                                   EventType.TRANSACTION_PROPOSAL_COMMENT,
                                                   String.format(COMMENT_FORMAT, "Transaction proposal"));
            }
        }//End if transaction.getType().equals(TransactionType.IssueOfAAUsAndRMUs)        
    }
    
    /**
     * Retrieves the task details for a Transaction Request task.
     *
     * @param taskDetailsDTO the generic task details.
     * @param transaction    the transaction details.
     * @return the Transaction Request task details.
     */
    public TransactionTaskDetailsDTO getTransactionTaskDetails(TaskDetailsDTO taskDetailsDTO,
                                                               Transaction transaction) {
        TransactionTaskDetailsDTO transactionTaskDetailsDTO =
            new TransactionTaskDetailsDTO(taskDetailsDTO, transaction.getType(), transaction.getReference());
        // Set transaction type
        transactionTaskDetailsDTO.setTransactionType(ProposedTransactionType.builder().type(transaction.getType())
            .description(transaction.getType().getDescription()).build());
        // Set ITL Notification
        if (transaction.getNotificationIdentifier() != null) {
            Optional<ItlNotificationSummary> itlNotificationOpt =
                    itlNoticeManagementService.getITLDetails(transaction.getNotificationIdentifier()).stream()
                            .map(itlNoticeDetailResult -> ItlNotificationSummary.builder()
                                    .commitPeriod(itlNoticeDetailResult.getCommitPeriod())
                                    .notificationIdentifier(itlNoticeDetailResult.getNotificationIdentifier())
                                    .projectNumber(itlNoticeDetailResult.getProjectNumber())
                                    .quantity(itlNoticeDetailResult.getTargetValue())
                                    .targetDate(itlNoticeDetailResult.getTargetDate())
                                    .build()
                            ).findAny();
            itlNotificationOpt.ifPresent(transactionTaskDetailsDTO::setItlNotification);
        }
        if (TransactionType.hasAllocationBasedAttribute(transaction.getType())) {
            transactionTaskDetailsDTO.setAllocationDetails(transaction.getAttributes());
        }
        // Set transaction transferring account
        AccountDTO transferringAccount =
            accountService.getAccountDTO(transaction.getTransferringAccount().getAccountIdentifier());
        AccountInfo transferringAccountInfo = accountConversionService
            .getAccountInfo(transaction.getTransferringAccount().getAccountIdentifier(),
                transaction.getTransferringAccount().getAccountFullIdentifier(), transferringAccount);
        if (TransactionType.isReversalTransaction(transaction.getType())) {
            transferringAccountInfo.setAccountType(AccountType.parse(transferringAccount.getAccountType()).getLabel());
        }
        transactionTaskDetailsDTO.setTransferringAccount(transferringAccountInfo);
        transactionTaskDetailsDTO.setTransactionConnectionSummary(transactionService.populateConnectionSummary(transaction));
        transactionTaskDetailsDTO.setAcquiringAccount(populateAcquiringAccount(transaction,transferringAccountInfo));
        // Set transaction block summaries
        transactionTaskDetailsDTO
            .setTransactionBlocks(transactionService.getTransactionBlockSummariesFromBlocks(transaction.getBlocks(),
                transferringAccount.getBalance()));

        // Maybe change this to get the task from request id in order to proceed
        // towards the subsequent findTaskTransactionsByTask query.
        Optional<TaskTransaction> optionalTaskTransaction = taskTransactionRepository.findTaskTransactionByTransactionIdentifier(transaction.getIdentifier());

        Set<String> transactionSet = new HashSet<>();
        transactionSet.add(transaction.getIdentifier());
        Transaction relatedTransaction = null;

        if (optionalTaskTransaction.isPresent()) {
            TaskTransaction retrievedTaskTransaction = optionalTaskTransaction.get();
            List<TaskTransaction> taskTransactions = taskTransactionRepository.findTaskTransactionsByTask(retrievedTaskTransaction.getTask());
            for (TaskTransaction taskTransaction : taskTransactions) {
                if (!transactionSet.contains(taskTransaction.getTransactionIdentifier())) {
                     relatedTransaction = transactionRepository.findByIdentifier(taskTransaction.getTransactionIdentifier());
                }
            }
        }

        if (relatedTransaction != null) {
            RegistryAccountType registryAccountType = decideRelatedTransactionAllocationType(transactionTaskDetailsDTO.getAcquiringAccount());
            if(registryAccountType != null) {
                return createExcessAllocationTaskDetailsDTO(taskDetailsDTO,transactionTaskDetailsDTO,transferringAccount,
                        transferringAccountInfo,transaction, relatedTransaction, registryAccountType);
            }
        }

        return transactionTaskDetailsDTO;
    }

    private Long createTransactionTask(String transactionIdentifier, Long accountIdentifier,
                                       String transactionComment, String acquiringAccountFullIdentifier) {
        TaskTransaction taskTransaction = new TaskTransaction();
        taskTransaction.setTransactionIdentifier(transactionIdentifier);
        Task task = new Task();
        task.setRequestId(taskRepository.getNextRequestId());
        task.setAccount(accountService.getAccount(accountIdentifier));
        task.setType(RequestType.TRANSACTION_REQUEST);
        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        Date insertDate = new Date();
        task.setInitiatedDate(insertDate);
        Optional.ofNullable(task.getTransactionIdentifiers()).orElseGet(ArrayList::new).add(taskTransaction);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        taskRepository.save(task);
        
        taskTransaction.setRecipientAccountNumber(acquiringAccountFullIdentifier);
        taskTransaction.setTask(task);
        taskTransactionRepository.save(taskTransaction);
        
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());
        String action = task.generateEventTypeDescription(" request submitted.");
        if (transactionComment != null) {
            eventService.createAndPublishEvent(task.getRequestId().toString(), currentUser.getUrid(),
                transactionComment, EventType.TASK_COMMENT, String.format(COMMENT_FORMAT, action));
        }

        return task.getRequestId();
    }

    protected Long createMultipleTransactionTask(MultipleTransactionsTaskInfo multipleTransactionsTaskInfo) {
        
        List<TaskTransaction> taskTransactions = multipleTransactionsTaskInfo.
            getTaskTransactions().
            stream().
            collect(Collectors.mapping(p ->  {
                TaskTransaction taskTransaction = new TaskTransaction();
                taskTransaction.setRecipientAccountNumber(p.getAcquiringAccountFullIdentifier());
                taskTransaction.setTransactionIdentifier(p.getTransactionIdentifier());
                return taskTransaction;
            }, Collectors.toList()));
        

        Task task = new Task();
        task.setRequestId(taskRepository.getNextRequestId());
        task.setAccount(accountService.getAccount(multipleTransactionsTaskInfo.getAccountIdentifier()));
        task.setType(RequestType.TRANSACTION_REQUEST);
        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        Date insertDate = new Date();
        task.setInitiatedDate(insertDate);
        task.setTransactionIdentifiers(taskTransactions);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        taskRepository.save(task);
        
        taskTransactions.forEach(t -> {
            t.setTask(task);
            taskTransactionRepository.save(t);
        });
        
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        if (Objects.nonNull(multipleTransactionsTaskInfo.getComment())) {
            String action = task.generateEventTypeDescription(" request submitted.");
            eventService.createAndPublishEvent(task.getRequestId().toString(), currentUser.getUrid(),
                multipleTransactionsTaskInfo.getComment(), EventType.TASK_COMMENT, String.format(COMMENT_FORMAT, action));
        }
        
        return task.getRequestId();
    }


    private AcquiringAccountInfo populateAcquiringAccount(Transaction transaction, AccountInfo transferringAccountInfo) {
// Set transaction acquiring account
        AccountDTO acquiringAccount = new AccountDTO();
        //Due to UKETSSD-164
        if(EnumSet.of(RegistryCode.GB ,RegistryCode.UK).contains(RegistryCode.parse(transaction.getAcquiringAccount().getAccountRegistryCode()))) {
            acquiringAccount =
                    accountService.getAccountDTO(transaction.getAcquiringAccount().getAccountIdentifier());
        }

        AccountInfo partialAcquiringAccountInfo = accountConversionService
                .getAccountInfo(transaction.getAcquiringAccount().getAccountIdentifier(),
                        transaction.getAcquiringAccount().getAccountFullIdentifier(), acquiringAccount);
        partialAcquiringAccountInfo.setRegistryCode(transaction.getAcquiringAccount().getAccountRegistryCode());
        boolean acquiringAccountInTrustedList = transactionAccountService
                .isTrustedAccount(transferringAccountInfo.getIdentifier(), partialAcquiringAccountInfo,
                        partialAcquiringAccountInfo.getRegistryCode());

        return AcquiringAccountInfo.acquiringAccountInfoBuilder()
                .identifier(partialAcquiringAccountInfo.getIdentifier())
                .fullIdentifier(partialAcquiringAccountInfo.getFullIdentifier())
                .accountName(partialAcquiringAccountInfo.getAccountName())
                .accountHolderName(partialAcquiringAccountInfo.getAccountHolderName())
                .isGovernment(acquiringAccount.isGovernmentAccount())
                .trusted(acquiringAccountInTrustedList).build();
    }

    private ExcessAllocationTransactionTaskDetailsDTO createExcessAllocationTaskDetailsDTO(TaskDetailsDTO taskDetailsDTO,
                                                                                           TransactionTaskDetailsDTO transactionTaskDetailsDTO,
                                                                                           AccountDTO transferringAccount,
                                                                                           AccountInfo transferringAccountInfo,
                                                                                           Transaction transaction,
                                                                                           Transaction relatedTransaction,
                                                                                           RegistryAccountType relatedTransactionRegistryAccountType
                                                                                          ) {
        ExcessAllocationTransactionTaskDetailsDTO excessAllocationTransactionTaskDetailsDTO =
                new ExcessAllocationTransactionTaskDetailsDTO(taskDetailsDTO, transaction.getType(), transaction.getReference());

        if (relatedTransactionRegistryAccountType.equals(RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT)) {
            excessAllocationTransactionTaskDetailsDTO.setNerAcquiringAccount(populateAcquiringAccount(relatedTransaction,transferringAccountInfo));
            excessAllocationTransactionTaskDetailsDTO.setNerQuantity(relatedTransaction.getQuantity());
            excessAllocationTransactionTaskDetailsDTO.setNerTransactionIdentifier(relatedTransaction.getIdentifier());
            excessAllocationTransactionTaskDetailsDTO.setNerTransactionBlocks(transactionService.getTransactionBlockSummariesFromBlocks(relatedTransaction.getBlocks(),
                    transferringAccount.getBalance()));
            excessAllocationTransactionTaskDetailsDTO.setNatAcquiringAccount(transactionTaskDetailsDTO.getAcquiringAccount());
            excessAllocationTransactionTaskDetailsDTO.setNatQuantity(transaction.getQuantity());
            excessAllocationTransactionTaskDetailsDTO.setNatTransactionIdentifier(transaction.getIdentifier());
            excessAllocationTransactionTaskDetailsDTO.setNatTransactionBlocks(transactionService.getTransactionBlockSummariesFromBlocks(transaction.getBlocks(),
                    transferringAccount.getBalance()));
        } else {
            excessAllocationTransactionTaskDetailsDTO.setNatAcquiringAccount(populateAcquiringAccount(relatedTransaction,transferringAccountInfo));
            excessAllocationTransactionTaskDetailsDTO.setNatQuantity(relatedTransaction.getQuantity());
            excessAllocationTransactionTaskDetailsDTO.setNatTransactionIdentifier(relatedTransaction.getIdentifier());
            excessAllocationTransactionTaskDetailsDTO.setNatTransactionBlocks(transactionService.getTransactionBlockSummariesFromBlocks(relatedTransaction.getBlocks(),
                    transferringAccount.getBalance()));
            excessAllocationTransactionTaskDetailsDTO.setNerAcquiringAccount(populateAcquiringAccount(transaction,transferringAccountInfo));;
            excessAllocationTransactionTaskDetailsDTO.setNerQuantity(transaction.getQuantity());
            excessAllocationTransactionTaskDetailsDTO.setNerTransactionIdentifier(transaction.getIdentifier());
            excessAllocationTransactionTaskDetailsDTO.setNerTransactionBlocks(transactionService.getTransactionBlockSummariesFromBlocks(transaction.getBlocks(),
                    transferringAccount.getBalance()));
        }

        excessAllocationTransactionTaskDetailsDTO.setTransactionType(ProposedTransactionType.builder().type(transaction.getType())
                .description(transaction.getType().getDescription()).build());
        excessAllocationTransactionTaskDetailsDTO.setTransferringAccount(transferringAccountInfo);
        excessAllocationTransactionTaskDetailsDTO.setTransactionConnectionSummary(transactionService.populateConnectionSummary(transaction));
        excessAllocationTransactionTaskDetailsDTO.setAllocationDetails(transaction.getAttributes());
        excessAllocationTransactionTaskDetailsDTO.setAcquiringAccount(transactionTaskDetailsDTO.getAcquiringAccount());
        excessAllocationTransactionTaskDetailsDTO.setTransactionBlocks(transactionTaskDetailsDTO.getTransactionBlocks());

        return excessAllocationTransactionTaskDetailsDTO;
    }

    private RegistryAccountType decideRelatedTransactionAllocationType(AcquiringAccountInfo firstTransactionAcquiringAccountInfo) {
        return switch (firstTransactionAcquiringAccountInfo.getAccountName()) {
            case "UK Allocation Account" -> RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT;
            case "UK New Entrants Reserve Account" -> RegistryAccountType.UK_ALLOCATION_ACCOUNT;
            default -> null;
        };
    }

}
