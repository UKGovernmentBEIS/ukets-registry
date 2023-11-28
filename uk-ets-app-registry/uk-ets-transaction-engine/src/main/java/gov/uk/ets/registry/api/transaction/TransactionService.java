package gov.uk.ets.registry.api.transaction;

import static java.lang.String.format;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summingLong;

import gov.uk.ets.registry.api.auditevent.domain.types.TransactionEventType;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.event.service.TransactionEventService;
import gov.uk.ets.registry.api.reconciliation.repository.ReconciliationRepository;
import gov.uk.ets.registry.api.reconciliation.type.ReconciliationStatus;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckErrorResult;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckService;
import gov.uk.ets.registry.api.transaction.checks.RequiredFieldException;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.TransactionConnection;
import gov.uk.ets.registry.api.transaction.domain.TransactionProcessState;
import gov.uk.ets.registry.api.transaction.domain.TransactionProcessStateTransition;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.SignedTransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockGroupByTuple;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionConnectionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionSystem;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.exception.TransactionExecutionException;
import gov.uk.ets.registry.api.transaction.lock.RegistryLockProvider;
import gov.uk.ets.registry.api.transaction.lock.RegistryLockType;
import gov.uk.ets.registry.api.transaction.messaging.ITLBlockConversionService;
import gov.uk.ets.registry.api.transaction.messaging.ITLConversionService;
import gov.uk.ets.registry.api.transaction.messaging.ITLOutgoingMessageService;
import gov.uk.ets.registry.api.transaction.messaging.TransactionNotification;
import gov.uk.ets.registry.api.transaction.messaging.UKTLOutgoingMessageService;
import gov.uk.ets.registry.api.transaction.processor.TransactionProcessor;
import gov.uk.ets.registry.api.transaction.service.ApprovalService;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountService;
import gov.uk.ets.registry.api.transaction.service.TransactionDelayService;
import gov.uk.ets.registry.api.transaction.service.TransactionMessageService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.transaction.service.TransactionResponseDTO;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProposalRequest;

/**
 * Transaction service.
 */
@Service
@Log4j2
@AllArgsConstructor
public class TransactionService {

    private static final String TRANSACTION_FAILED_ERROR = "The transaction could not start because of a system error";

    /**
     * The service for business checks.
     */
    private final BusinessCheckService businessCheckService;

    /**
     * The service for retrieving the appropriate transaction processor.
     */
    private final TransactionFactory transactionFactory;

    /**
     * The persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Service for sending messages to ITL.
     */
    private final ITLOutgoingMessageService itlOutgoingMessageService;

    /**
     * Service for sending messages to the UK ETS transaction log.
     */
    private final UKTLOutgoingMessageService uktlOutgoingMessageService;
    /**
     * Service for preparing messages for ITL communication.
     */
    private final ITLConversionService itlConversionService;

    /**
     * Conversion service for unit blocks.
     */
    private final ITLBlockConversionService itlBlockConversionService;

    /**
     * Service for signatures.
     */
    private final ApprovalService approvalService;

    /**
     * Service for transaction delays.
     */
    private final TransactionDelayService transactionDelayService;

    /**
     * Service for accounts.
     */
    private final TransactionAccountService transactionAccountService;

    /**
     * Repository for reconciliations.
     */
    private final ReconciliationRepository reconciliationRepository;

    /**
     * The application locks provider.
     */
    private final RegistryLockProvider registryLockProvider;

    /**
     * The transaction event service.
     */
    private final TransactionEventService transactionEventService;

    /**
     * Service for reversing transactions.
     */
    private final TransactionReversalService transactionReversalService;

    /**
     * Service for transaction messages.
     */
    private final TransactionMessageService transactionMessageService;

    /**
     * Entry point for proposing a transaction.
     *
     * @param transactionSummary The transaction details.
     * @param hasExtendedScope   Whether the initiator has extended scope.
     * @return a result object.
     */
    @Transactional
    public BusinessCheckResult proposeTransaction(TransactionSummary transactionSummary, boolean hasExtendedScope) {
        SignedTransactionSummary signedTransactionSummary = new SignedTransactionSummary();
        validateInput(transactionSummary);
        org.springframework.beans.BeanUtils.copyProperties(transactionSummary, signedTransactionSummary);
        return proposeSignedTransaction(signedTransactionSummary, hasExtendedScope);
    }

    /**
     * Entry point for proposing a transaction.
     *
     * @param transaction      The transaction details.
     * @param hasExtendedScope Whether the initiator has extended scope.
     * @return a result object.
     */
    @Transactional
    public BusinessCheckResult proposeSignedTransaction(SignedTransactionSummary transaction,
                                                        boolean hasExtendedScope) {
        transactionReversalService.prepareReversal(transaction);
        final TransactionType type = validateInput(transaction);

        BusinessCheckResult result = performChecks(transaction, hasExtendedScope);

        String transactionId = transaction.getIdentifier();
        if (StringUtils.isEmpty(transactionId) && !Constants.isInboundTransaction(transaction)) {
            transactionId = format("%s%s", Constants.getRegistryCode(type.isKyoto()),
                transactionPersistenceService.getNextIdentifier());
            transaction.setIdentifier(transactionId);
        }

        // Create transaction record
        TransactionProcessor processor = transactionFactory.getTransactionProcessor(type);
        Transaction newTransaction = processor.createInitialTransaction(transaction);
        if (transaction.getSignatureInfo() != null) {
            newTransaction.setSignature(transaction.getSignatureInfo().getSignature());
            newTransaction.setSignedData(transaction.getSignatureInfo().getData());
        }
        transactionPersistenceService.save(newTransaction);
        transactionPersistenceService.updateStatus(newTransaction, TransactionStatus.AWAITING_APPROVAL);

        // Proceed to transaction specific actions
        processor.propose(transaction);

        result.setTransactionIdentifier(transactionId);

        if (!approvalService.isApprovalRequired(transaction, hasExtendedScope)) {
            BusinessCheckResult finaliseResult =
                finaliseTransaction(transactionId, TaskOutcome.APPROVED, hasExtendedScope);
            result.setExecutionTime(finaliseResult.getExecutionTime());
            result.setExecutionDate(finaliseResult.getExecutionDate());
        }

        return result;
    }

    /**
     * Performs some common input validations.
     *
     * @param transaction The transaction details.
     * @return the transaction type.
     */
    private TransactionType validateInput(TransactionSummary transaction) {
        if (transaction == null) {
            throw new RequiredFieldException("Transaction input is required");
        }

        final TransactionType type = transaction.getType();
        if (type == null) {
            throw new RequiredFieldException("Transaction type is required");
        }

        final List<TransactionBlockSummary> blocks = transaction.getBlocks();
        if (CollectionUtils.isEmpty(blocks)) {
            throw new RequiredFieldException("At least one transaction block is required");
        }

        for (TransactionBlockSummary block : blocks) {
            if (block.getType() == null) {
                throw new RequiredFieldException("The unit block type is required");
            }
            if (block.getQuantity() == null && block.getStartBlock() == null && block.getEndBlock() == null) {
                throw new RequiredFieldException("The unit block quantity is required");
            }
            if (type.isKyoto() && (block.getOriginalPeriod() == null || block.getApplicablePeriod() == null)) {
                throw new RequiredFieldException("The original and applicable commitment periods are required");
            }
        }
        return type;
    }

    /**
     * Entry point for finalising a transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     * @param outcome               The outcome (approved, rejected).
     * @param hasExtendedScope      Whether the initiator has extended scope.
     */
    @EmitsGroupNotifications(GroupNotificationType.TRANSACTION_FINALISATION)
    @Transactional
    public BusinessCheckResult finaliseTransaction(String transactionIdentifier, TaskOutcome outcome,
                                                   boolean hasExtendedScope) {
        BusinessCheckResult result = new BusinessCheckResult();
        if (transactionIdentifier == null) {
            throw new RequiredFieldException("Transaction identifier is required");
        }
        if (outcome == null) {
            throw new RequiredFieldException("The outcome is required");
        }
        Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
        if (transaction == null) {
            throw new RequiredFieldException(format("No transaction found with identifier %s", transactionIdentifier));
        }
        TransactionProcessor processor = transactionFactory.getTransactionProcessor(transaction.getType());

        if (TaskOutcome.APPROVED.equals(outcome)) {
            boolean delayApplies = transactionDelayService.isTransactionValidForDelay(transaction);
            if (delayApplies) {

                processor.delay(transaction);

            } else {
                transaction.setExecutionDate(LocalDateTime.now());
                startTransaction(transaction);
            }
            final LocalDateTime executionDate = transaction.getExecutionDate();
            result.setExecutionTime(Utils.formatTime(executionDate));
            result.setExecutionDate(Utils.formatDay(executionDate));
        } else {
            processor.reject(transaction);
        }
        return result;
    }

    /**
     * Performs checks on the provided transaction.
     *
     * @param transaction      The transaction.
     * @param hasExtendedScope Whether the initiator has extended scope.
     * @return the result
     */
    public BusinessCheckResult performChecks(TransactionSummary transaction, boolean hasExtendedScope) {
        return performChecks(transaction, null, hasExtendedScope);
    }

    /**
     * Performs checks on the provided transaction.
     *
     * @param transaction      The transaction.
     * @param hasExtendedScope Whether the initiator has extended scope.
     * @return the result
     */
    public BusinessCheckResult performChecks(TransactionSummary transaction, BusinessCheckGroup group,
                                             boolean hasExtendedScope) {
        clarifyTransactionType(transaction);
        businessCheckService.performChecks(transaction, group, hasExtendedScope);
        BusinessCheckResult result = new BusinessCheckResult();
        result.setTransactionType(transaction.getType());
        result.setTransactionTypeDescription(transaction.getType().getDescription());
        if (group == null) {
            result.setApprovalRequired(approvalService.isApprovalRequired(transaction, hasExtendedScope));
        }
        return result;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EmitsGroupNotifications(GroupNotificationType.TRANSACTION_COMPLETION)
    public void handleTransactionFailure(Transaction transaction) {
        TransactionProcessor processor = transactionFactory.getTransactionProcessor(transaction.getType());
        processor.fail(transaction);
        transactionPersistenceService.save(transaction);
        saveTransactionResponse(
            TransactionResponseDTO.builder()
                .transaction(transaction)
                .errors(List.of(new BusinessCheckError(-1,
                    TRANSACTION_FAILED_ERROR)))
                .build());
        //Write a system event for the transaction status
        transactionEventService.createAndPublishEvent(transaction.getIdentifier(),
            null,
            StringUtils.EMPTY,
            this,
            TransactionEventType.TRANSACTION_FAILED);
    }

    /**
     * Starts a transaction.
     *
     * @param transaction The transaction.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void startTransaction(Transaction transaction) {
        if (transaction.getType().isKyoto()) {
            startKyotoTransaction(transaction);
        } else {
            startETSTransaction(transaction);
        }
    }

    /**
     * Starts a UK ETS transaction, a non Kyoto transaction.
     *
     * @param transaction The UK ETS transaction
     */
    private void startETSTransaction(Transaction transaction) {
        boolean reconciliationInProgress = false;
        try {
            registryLockProvider.acquirePessimisticReadLock(RegistryLockType.RECONCILIATION, true);
        } catch (javax.persistence.LockTimeoutException exception) {
            reconciliationInProgress = true;
        }
        if (reconciliationInProgress ||
            reconciliationRepository.findFirstByStatusIn(ReconciliationStatus.getPendingStatuses()).isPresent()) {
            if (transaction.getStatus() == TransactionStatus.DELAYED) {
                return; // Postpone the transaction to be started later.
            }
            throw new IllegalStateException("ETS Transactions cannot start while a reconciliation is in progress");
        }
        transaction.setStarted(new Date());
        transactionPersistenceService.updateStatus(transaction, TransactionStatus.PROPOSED);
        if (transaction.getBlocks() == null) {
            transaction.setBlocks(transactionPersistenceService.getTransactionBlocks(transaction.getIdentifier()));
        }
        final TransactionNotification transactionNotification = TransactionNotification.from(transaction);
        transactionMessageService.saveOutgoingMessage(transaction, transaction.getStatus().name(),
            TransactionSystem.UKTL, transactionNotification);
        uktlOutgoingMessageService.sendProposalRequest(transactionNotification);
    }

    /**
     * Starts a Kyoto transaction.
     *
     * @param transaction The Kyoto transaction
     */
    private void startKyotoTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setStarted(new Date());
        transactionPersistenceService.save(transaction);
        List<TransactionBlock> blocks = transactionPersistenceService.getTransactionBlocks(transaction.getIdentifier());
        final ProposalRequest proposalRequest = itlConversionService.prepareAcceptProposal(transaction, blocks);
        if (Constants.isInboundTransaction(transaction)) {
            NotificationRequest request = new NotificationRequest();
            request.setTransactionStatus(TransactionStatus.CHECKED_NO_DISCREPANCY.getCode());
            request.setTransactionIdentifier(transaction.getIdentifier());
            processReply(request);
        } else {
            transactionMessageService.saveOutgoingMessage(transaction, TransactionStatus.PROPOSED.name(),
                TransactionSystem.ITL, proposalRequest);
            itlOutgoingMessageService.sendProposalRequest(proposalRequest);
        }
    }

    /**
     * Manually cancels a delayed transaction.
     *
     * @param transactionIdentifier the transaction identifier to be cancelled
     * @param comment               a comment is mandatory when cancelling transactions
     * @param urid                  the urid of the user that requests to cancel the transaction
     * @return true wnen successfull returning a value instead of void is needed for
     * the @EmitGroupNotification to work
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.TRANSACTION_MANUALLY_CANCELLED)
    public boolean manuallyCancel(String transactionIdentifier, String comment, String urid) {
        Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
        if (!TransactionStatus.DELAYED.equals(transaction.getStatus())) {
            throw new IllegalStateException("Only transactions in 'Delayed' status can be manually cancelled");
        }
        // normally this will always be an internal transfer processor since only internal transfers can be delayed.
        TransactionProcessor processor = transactionFactory.getTransactionProcessor(transaction.getType());
        transactionPersistenceService.updateStatus(transaction, TransactionStatus.MANUALLY_CANCELLED);
        processor.manuallyCancel(transaction);
        transactionEventService.createAndPublishEvent(transaction.getIdentifier(),
            urid,
            comment,
            this,
            TransactionEventType.TRANSACTION_MANUALLY_CANCELED);
        transactionEventService
            .createAndPublishEvent(transaction.getTransferringAccount().getAccountIdentifier().toString(),
                urid,
                comment,
                this,
                TransactionEventType.TRANSACTION_MANUALLY_CANCELED);
        return true;
    }

    /**
     * Processes the reply from ITL.
     *
     * @param request The notification request.
     */
    @Transactional
    @EmitsGroupNotifications({GroupNotificationType.TRANSACTION_INBOUND,
        GroupNotificationType.TRANSACTION_INBOUND_COMPLETION})
    public void processReply(NotificationRequest request) {
        TransactionProcessStateTransition nextStep =
            getTransactionProcessStateTransition(request.getTransactionIdentifier(), request.getTransactionStatus());
        final Transaction transaction = nextStep.getTransaction();
        transactionMessageService.saveInboundMessage(transaction, nextStep.getNextStatus().name(),
            TransactionSystem.ITL, request);
        if (transaction.getStatus().isFinal()) {
            log.info("Transaction {} is already in status {}. This message will be not be processed {}",
                transaction.getIdentifier(), transaction.getStatus(), request);
            request.setTransactionStatus(transaction.getStatus().getCode());
            return;
        }
        TransactionStatus transactionStatus = process(nextStep);
        if (TransactionStatus.COMPLETED.equals(transactionStatus)) {
            final NotificationRequest notificationRequest = itlConversionService.
                prepareNotificationRequestForTransactionCompletion(request.getTransactionIdentifier());
            transactionMessageService.saveOutgoingMessage(transaction, transactionStatus.name(), TransactionSystem.ITL,
                notificationRequest);
            itlOutgoingMessageService.sendNotificationRequest(
                notificationRequest);
        }
        saveITLErrors(request, transaction, nextStep);
    }

    /**
     * Saves the ITL error message in database.
     *
     * @param request     The notification request.
     * @param transaction The transaction
     * @param nextStep    The {@link TransactionProcessStateTransition} transaction state transition information.
     */
    private void saveITLErrors(NotificationRequest request, Transaction transaction,
                               TransactionProcessStateTransition nextStep) {
        if (nextStep.getNextState().equals(TransactionProcessState.TERMINATE)) {
            List<BusinessCheckError> itlErrors = new ArrayList<>();
            Stream.of(request.getEvaluationResult()).forEach(er -> {
                BusinessCheckError error = new BusinessCheckError();
                error.setCode(er.getResponseCode());
                error.setMessage("Check DES ITL response codes for further information");
                itlErrors.add(error);
            });
            saveTransactionResponse(
                TransactionResponseDTO.builder()
                    .transaction(transaction)
                    .errors(itlErrors)
                    .build());
        }
    }

    /**
     * Performs actions according to the state that the transaction should transit.
     *
     * @param transition The {@link TransactionProcessStateTransition} transaction state transition information.
     */

    @Transactional(propagation = Propagation.MANDATORY)
    public TransactionStatus process(TransactionProcessStateTransition transition) {
        Transaction transaction = transition.getTransaction();
        TransactionProcessor processor = transactionFactory.getTransactionProcessor(transaction.getType());
        switch (transition.getNextState()) {
            case FINALISE:
                processor.finalise(transaction);
                //Write a system event for the transaction status
                transactionEventService.createAndPublishEvent(transaction.getIdentifier(),
                    null,
                    StringUtils.EMPTY,
                    this,
                    TransactionEventType.TRANSACTION_COMPLETED);
                break;
            case TERMINATE:
                processor.terminate(transaction);
                break;
            case CANCEL:
                processor.cancel(transaction);
                //Write a system event for the transaction status
                transactionEventService.createAndPublishEvent(transaction.getIdentifier(),
                    null,
                    StringUtils.EMPTY,
                    this,
                    TransactionEventType.TRANSACTION_CANCELLED);
        }
        return transaction.getStatus();
    }

    /**
     * Returns the {@link TransactionProcessStateTransition} information object according to the transaction identifier
     * and status code.
     *
     * @param transactionIdentifier The transaction business identifier
     * @param statusCode            The transaction status code
     * @return The {@link TransactionProcessStateTransition} information.
     */
    @Transactional(readOnly = true)
    public TransactionProcessStateTransition getTransactionProcessStateTransition(String transactionIdentifier,
                                                                                  int statusCode) {
        Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
        if (transaction == null) {
            throw new IllegalStateException("The transaction should exist during transaction processing");
        }
        TransactionStatus nextStatus = TransactionStatus.parse(statusCode);
        TransactionProcessState nextState = null;
        switch (nextStatus) {
            case STL_CHECKED_NO_DISCREPANCY:
            case CHECKED_NO_DISCREPANCY:
            case ACCEPTED:
            case COMPLETED:
                nextState = TransactionProcessState.FINALISE;
                break;
            case STL_CHECKED_DISCREPANCY:
            case CHECKED_DISCREPANCY:
            case REJECTED:
                nextState = TransactionProcessState.TERMINATE;
                break;
            case CANCELLED:
                nextState = TransactionProcessState.CANCEL;
                break;
            default:
        }
        return TransactionProcessStateTransition.builder()
            .transaction(transaction)
            .nextState(nextState)
            .nextStatus(nextStatus)
            .build();
    }

    /**
     * Returns the transaction and its blocks with the provided identifier.
     *
     * @param transactionIdentifier The unique transaction business identifier.
     * @return a transaction.
     */
    public Transaction getTransactionWithBlocks(String transactionIdentifier) {
        return transactionPersistenceService.getTransactionWithBlocks(transactionIdentifier);
    }

    /**
     * Returns the transaction with the provided identifier.
     *
     * @param transactionIdentifier The unique transaction business identifier
     * @return a transaction
     */
    public Transaction getTransaction(String transactionIdentifier) {
        return transactionPersistenceService.getTransaction(transactionIdentifier);
    }

    /**
     * Returns a list of transaction block summaries, by grouping transaction blocks.
     *
     * @param inputBlocks the transaction blocks.
     * @return a list of transaction block summaries.
     */
    public List<TransactionBlockSummary> getTransactionBlockSummariesFromBlocks(
        List<TransactionBlock> inputBlocks, Long transferringAccountBalance) {

        List<TransactionBlockSummary> transactionBlocks = new ArrayList<>();
        inputBlocks.forEach(transactionBlock -> {
            TransactionBlockSummary transferObject = new TransactionBlockSummary();
            try {
                BeanUtils.copyProperties(transferObject, transactionBlock);
                transactionBlocks.add(transferObject);
            } catch (IllegalAccessException | InvocationTargetException exc) {
                throw new ConversionException("Could not convert the transaction blocks to data transfer objects");
            }
        });

        Map<String, Set<String>> duplicatesMap =
            transactionBlocks.stream()
                .filter(o -> o.getEnvironmentalActivity() != null ||
                    o.getProjectNumber() != null)
                .collect(Collectors.groupingBy(o -> StringUtils.join(o.getType(),
                        o.getOriginalPeriod(),
                        o.getApplicablePeriod()),
                    mapping(o -> o.getEnvironmentalActivity() != null ?
                            o.getEnvironmentalActivity().name() :
                            o.getProjectNumber(),
                        Collectors.toSet())));

        transactionBlocks.forEach(tb -> {
            if (duplicatesMap.containsKey(StringUtils.join(tb.getType(),
                tb.getOriginalPeriod(),
                tb.getApplicablePeriod())) &&
                duplicatesMap.get(StringUtils.join(tb.getType(),
                    tb.getOriginalPeriod(),
                    tb.getApplicablePeriod())).size() > 1) {
                if (tb.getType().getRelatedWithProject()) {
                    tb.setProjectNumber(null);
                } else {
                    tb.setEnvironmentalActivity(null);
                }
            }
        });

        Map<TransactionBlockGroupByTuple, Long> transactionBlockGroups = transactionBlocks.stream()
            .collect(Collectors
                .groupingBy(block ->
                        new TransactionBlockGroupByTuple(block.getType(),
                            block.getOriginalPeriod(),
                            block.getApplicablePeriod(),
                            block.getEnvironmentalActivity(),
                            block.getSubjectToSop(),
                            block.getProjectNumber()),
                    summingLong(block -> block.getEndBlock() - block.getStartBlock() + 1)));

        return transactionBlockGroups.entrySet()
            .stream()
            .map(tbg -> TransactionBlockSummary
                .builder()
                .availableQuantity(transferringAccountBalance)
                .originalPeriod(tbg.getKey().getOriginalPeriod())
                .applicablePeriod(tbg.getKey().getApplicablePeriod())
                .environmentalActivity(tbg.getKey().getEnvironmentalActivity())
                .projectNumber(tbg.getKey().getProjectNumber())
                .quantity(format("%d", tbg.getValue()))
                .subjectToSop(tbg.getKey().getSubjectToSop())
                .type(tbg.getKey().getType())
                .build())
            .sorted(Comparator.comparing(TransactionBlockSummary::getType)
                .thenComparing(TransactionBlockSummary::getOriginalPeriod)
                .thenComparing(TransactionBlockSummary::getApplicablePeriod)
                .thenComparing(TransactionBlockSummary::getSubjectToSop))
            .collect(Collectors.toList());
    }

    /**
     * Clarifies the transaction types, for those transactions that share the same user interface.
     *
     * @param transaction The transaction.
     */
    private void clarifyTransactionType(TransactionSummary transaction) {
        AccountSummary transferringAccount = transactionAccountService.populateTransferringAccount(transaction);
        if (transferringAccount == null) {
            return;
        }
        boolean transferringAccountIsInsideTheRegistry =
            Constants.isInternalRegistry(transferringAccount.getRegistryCode());
        boolean acquiringAccountIsInsideTheRegistry =
            Constants.accountIsInternal(transaction.getAcquiringAccountFullIdentifier());

        if (TransactionType.ExternalTransfer.equals(transaction.getType())
            && transferringAccountIsInsideTheRegistry
            && acquiringAccountIsInsideTheRegistry) {
            transaction.setType(TransactionType.InternalTransfer);

        } else if (TransactionType.InternalTransfer.equals(transaction.getType()) &&
            !acquiringAccountIsInsideTheRegistry) {
            transaction.setType(TransactionType.ExternalTransfer);
        }
    }

    /**
     * Processes an incoming transaction.
     *
     * @param request The proposal request.
     */
    @Async
    @Transactional
    public void processIncomingTransaction(ProposalRequest request) {
        TransactionSummary transactionSummary = itlConversionService.parseAcceptProposal(request);
        BusinessCheckErrorResult check = null;
        BusinessCheckResult result = null;
        try {
            result = proposeTransaction(transactionSummary, false);

        } catch (BusinessCheckException exc) {
            check = exc.getBusinessCheckErrorResult();
            log.info("Inbound transaction {} rejected {}, {}", transactionSummary.getIdentifier(), exc.getMessage(),
                transactionSummary);

            // Save failed inbound transaction
            if (transactionSummary.getType().equals(TransactionType.ExternalTransfer)) {
                Transaction transaction = createFailedInboundTransaction(transactionSummary);
                transaction.setBlocks(getTransactionBlocks(transaction,
                    itlBlockConversionService.convert(request.getProposedTransaction().getProposalUnitBlocks())));
                handleInboundTransactionFailure(transaction, check);
            }
        }
        final NotificationRequest notificationRequest =
            itlConversionService.prepareNotificationRequest(transactionSummary.getIdentifier(), check);
        if (result != null) {
            Transaction transaction = transactionPersistenceService.getTransaction(result.getTransactionIdentifier());
            transactionMessageService.saveOutgoingMessage(transaction, transaction.getStatus().name(),
                TransactionSystem.ITL, notificationRequest);
        }
        itlOutgoingMessageService.sendNotificationRequest(notificationRequest);
    }

    /**
     * Stores the transaction errros and warnings if exist.
     *
     * @param dto The transaction response DTO
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void saveTransactionResponse(TransactionResponseDTO dto) {
        transactionPersistenceService.saveTransactionResponse(dto);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void handleInboundTransactionFailure(Transaction transaction, BusinessCheckErrorResult errors) {
        transaction.setStatus(TransactionStatus.TERMINATED);
        transactionPersistenceService.save(transaction);
        saveTransactionResponse(
            TransactionResponseDTO.builder()
                .transaction(transaction)
                .errors(errors.getErrors())
                .build());
        //Write a system event for the transaction status
        transactionEventService.createAndPublishEvent(transaction.getIdentifier(),
            null,
            StringUtils.EMPTY,
            this,
            TransactionEventType.TRANSACTION_FAILED);
    }

    public Transaction createFailedInboundTransaction(TransactionSummary transactionSummary) {
        Transaction transaction = new Transaction();
        try {
            BeanUtils.copyProperties(transaction, transactionSummary);
            transaction.setBlocks(null);
            transaction.setQuantity(transactionSummary.calculateQuantity());
            transaction.setStatus(TransactionStatus.TERMINATED);
            transaction.setLastUpdated(new Date());
            transaction.setUnitType(transactionSummary.calculateUnitTypes());

            AccountBasicInfo acquiringAccount = new AccountBasicInfo();
            acquiringAccount.setAccountFullIdentifier(transactionSummary.getAcquiringAccountFullIdentifier());
            acquiringAccount.setAccountRegistryCode(transactionSummary.getAcquiringAccountRegistryCode());
            acquiringAccount.setAccountType(transactionSummary.getAcquiringAccountType());
            acquiringAccount.setAccountIdentifier(transactionSummary.getAcquiringAccountIdentifier());
            transaction.setAcquiringAccount(acquiringAccount);

            AccountBasicInfo transferringAccount = new AccountBasicInfo();
            transferringAccount.setAccountFullIdentifier(transactionSummary.getTransferringAccountFullIdentifier());
            transferringAccount.setAccountRegistryCode(transactionSummary.getTransferringRegistryCode());
            transferringAccount.setAccountType(transactionSummary.getTransferringAccountType());
            transferringAccount.setAccountIdentifier(transactionSummary.getTransferringAccountIdentifier());
            transaction.setTransferringAccount(transferringAccount);

        } catch (IllegalAccessException | InvocationTargetException exc) {
            throw new TransactionExecutionException(this.getClass(), "Error when creating a new transaction", exc);
        }
        return transaction;
    }

    private List<TransactionBlock> getTransactionBlocks(Transaction transaction,
                                                        List<TransactionBlockSummary> transactionBlockSummaryList) {
        List<TransactionBlock> blocks = new ArrayList<>();

        transactionBlockSummaryList.forEach(transactionBlockSummary -> {
            TransactionBlock block = new TransactionBlock();
            block.setYear(transactionBlockSummary.getYear());
            block.setTransaction(transaction);
            block.setType(transactionBlockSummary.getType());
            block.setStartBlock(transactionBlockSummary.getStartBlock());
            block.setEndBlock(transactionBlockSummary.getEndBlock());
            block.setOriginatingCountryCode(transactionBlockSummary.getOriginatingCountryCode());
            block.setApplicablePeriod(transactionBlockSummary.getApplicablePeriod());
            block.setOriginalPeriod(transactionBlockSummary.getOriginalPeriod());

            blocks.add(block);
        });

        return blocks;
    }

    /**
     * Retrieves the transaction connection from the subject transaction, i.e. the transaction that reverses the original.
     *
     * @param transaction The reversal transaction.
     * @return the original transaction.
     */
    public TransactionConnection getTransactionConnectionFromReversal(Transaction transaction) {
        return transactionPersistenceService.getTransactionConnection(transaction,
            TransactionConnectionType.REVERSES, true);
    }

    /**
     * Retrieves the transaction connection from the object transaction, i.e. the transaction that is reversed.
     *
     * @param reversedTransaction The original transaction.
     * @return The reversal transaction.
     */
    public TransactionConnection getTransactionConnectionFromOriginal(Transaction reversedTransaction) {
        return transactionPersistenceService.getTransactionConnection(reversedTransaction,
            TransactionConnectionType.REVERSES, false);
    }

    /**
     * Populates connection summary, based on the type of the transaction, original or reversal.
     *
     * @param transaction The transaction.
     * @return A TransactionConnectionSummary object.
     */
    public TransactionConnectionSummary populateConnectionSummary(Transaction transaction) {

        TransactionConnection connection;
        TransactionConnectionSummary result = null;

        if (transaction.getType().getReversalAllowed()) {
            connection = getTransactionConnectionFromOriginal(transaction);
            if (connection != null) {
                result = TransactionConnectionSummary.builder()
                    .reversalIdentifier(connection.getSubjectTransaction().getIdentifier())
                    .reversalStatus(connection.getSubjectTransaction().getStatus())
                    .build();
            }
        }
        if (TransactionType.isReversalTransaction(transaction.getType())) {
            connection = getTransactionConnectionFromReversal(transaction);
            result = TransactionConnectionSummary.builder()
                .originalIdentifier(connection.getObjectTransaction().getIdentifier())
                .originalStatus(connection.getObjectTransaction().getStatus())
                .build();
        }
        return result;
    }
}
