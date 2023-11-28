package uk.gov.ets.transaction.log.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckResult;
import uk.gov.ets.transaction.log.domain.AccountBasicInfo;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.TransactionBlock;
import uk.gov.ets.transaction.log.domain.TransactionHistory;
import uk.gov.ets.transaction.log.domain.TransactionResponse;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.domain.type.KyotoAccountType;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.messaging.types.TransactionAnswer;
import uk.gov.ets.transaction.log.messaging.types.TransactionNotification;
import uk.gov.ets.transaction.log.repository.TransactionRepository;
import uk.gov.ets.transaction.log.repository.UnitBlockRepository;

@Service
@AllArgsConstructor
@Log4j2
public class TransactionService {

    private final TransactionRepository transactionRepository;
    
    private final UnitBlockRepository unitBlockRepository;
    
    private final SplitUnitBlocksService splitBlocksService;
    
    private final KafkaTemplate<String,TransactionAnswer> kafkaTemplate;

    private final TransactionValidationService transactionValidationService;

    private final LimitService limitService;

    public static final String TXLOG_ORIGINATING_TRANSACTION_ANSWER_TOPIC = "txlog.originating.transaction.answer.topic";

    @Transactional
    public void acceptTransactionProposal(TransactionNotification transactionProposal) {
        log.info("Received transaction proposal: {}", transactionProposal);
        if (!transactionValidationService.performPreliminaryChecks(transactionProposal).success()) {
            log.info("The message {} will not be processed.", transactionProposal);
            return;
        }
        BusinessCheckResult result = validateTransactionProposal(transactionProposal);
        sendUkTLTransactionAnswer(transactionProposal.getIdentifier(), result);
    }

    protected BusinessCheckResult validateTransactionProposal(TransactionNotification transactionProposal) {
        log.info("Validating {} proposal {}" ,transactionProposal.getType(), transactionProposal.getIdentifier());
        //Convert to Transaction Object.
        Transaction proposedTransaction = this.toTransaction(transactionProposal);

        BusinessCheckResult result = transactionValidationService.validateTransaction(transactionProposal);
        TransactionStatus status = result.success() ? TransactionStatus.COMPLETED :
            TransactionStatus.TERMINATED;

        proposedTransaction = completeTransaction(proposedTransaction, status, result);

        if (!TransactionType.IssueAllowances.equals(proposedTransaction.getType())) {
            long numOfSplits = splitBlocksService.splitUnitBlocks(transactionProposal);
            log.info("{} SPLIT(S) INVOLVED",numOfSplits);
        }
        //Finalization
        if (result.success()) {
            if (TransactionType.IssueAllowances.equals(proposedTransaction.getType())) {
                log.info("ISSUANCE NO SPLIT INVOLVED");
                //Insert Transaction Unit Blocks as Unit Blocks
                insertUnitBlocks(proposedTransaction);
                limitService.consumeLimit(proposedTransaction.getQuantity());
            } else {
                //Now we are ready to update unit blocks ownership.
                updateUnitBlocksOwnership(proposedTransaction);
            }
        }
        return result;
    }


    protected Transaction completeTransaction(Transaction proposedTransaction, TransactionStatus status, BusinessCheckResult result) {
        TransactionHistory completedStatusHistory = new TransactionHistory();
        completedStatusHistory.setStatus(status);
        completedStatusHistory.setTransaction(proposedTransaction);
        completedStatusHistory.setDate(new Date());
        
        //Save transaction to DB
        List<TransactionHistory> history = Optional.ofNullable(proposedTransaction.getHistoryEntries()).
                orElse(new ArrayList<>());
        history.add(completedStatusHistory);
        proposedTransaction.setHistoryEntries(history);
        proposedTransaction.setStatus(status);

        if(!CollectionUtils.isEmpty(result.getErrors())) {
            List<TransactionResponse> responses = result.getErrors().stream().map(error -> {
                TransactionResponse response = new TransactionResponse();
                response.setDateOccurred(new Date());
                response.setErrorCode(Long.valueOf(error.getCode()));
                response.setDetails(error.getMessage());
                response.setTransaction(proposedTransaction);
                return response;
            }).collect(Collectors.toList());
            proposedTransaction.setResponseEntries(responses);
        }

        return transactionRepository.save(proposedTransaction);
    }
    
    /**
     * Updates the ownership of the unit blocks to the one specified by the Transaction Proposal.
     *  
     * @param transactionProposal The transaction proposal.
     */
    protected List<UnitBlock> updateUnitBlocksOwnership(Transaction transactionProposal) {
        final List<UnitBlock> blocks = new ArrayList<>();
        transactionProposal.getBlocks().forEach(t -> {
            List<UnitBlock> proposalBlocks = unitBlockRepository.
                    findIncludedBlocksByStartBlockGreaterThanEqualAndEndBlockLessThanEqual(t.getStartBlock(), t.getEndBlock());
            proposalBlocks.forEach(b -> {
                b.setAccountIdentifier(transactionProposal.getAcquiringAccount().getAccountIdentifier());
                b.setLastModifiedDate(new Date());
            });
            blocks.addAll(unitBlockRepository.saveAll(proposalBlocks));
        });
        return blocks;
    }

    /**
     * Inserts unit blocks to the DB specified by the Transaction Proposal.
     *  
     * @param transactionProposal The transaction proposal.
     */
    protected List<UnitBlock> insertUnitBlocks(Transaction transactionProposal) {
        final List<UnitBlock> blocks = new ArrayList<>();
        //Insert Transaction Unit Blocks as Unit Blocks
        transactionProposal.getBlocks().
        stream().
        map(this::toUnitBlock).
            forEach(b -> blocks.add(unitBlockRepository.save(b)));  
        return blocks;
    }
    
    /**
     * Send a reply concerning a Transaction proposal back to the message bus.
     * 
     * @param transactionIdentifier the unique business identifier.
     * @param result the business check result.
     */
    private void sendUkTLTransactionAnswer(String transactionIdentifier, BusinessCheckResult result) {
        TransactionStatus status = result.success() ? TransactionStatus.CHECKED_NO_DISCREPANCY :
            TransactionStatus.CHECKED_DISCREPANCY;
        kafkaTemplate.send(TXLOG_ORIGINATING_TRANSACTION_ANSWER_TOPIC, 
                TransactionAnswer.
                builder().
                transactionIdentifier(transactionIdentifier).
                transactionStatusCode(status.getCode()).
                errors(result.getErrors()).
                build());
    }
    
    protected UnitBlock toUnitBlock(TransactionBlock transactionBlock) {
        UnitBlock block = new UnitBlock();
        block.setAccountIdentifier(transactionBlock.getTransaction().getAcquiringAccount().getAccountIdentifier());
        block.setAcquisitionDate(transactionBlock.getTransaction().getStarted());
        block.setLastModifiedDate(new Date());
        block.setType(transactionBlock.getType());
        block.setStartBlock(transactionBlock.getStartBlock());
        block.setEndBlock(transactionBlock.getEndBlock());
        block.setYear(transactionBlock.getYear());
        
        return block;
    }
    
    protected Transaction toTransaction(TransactionNotification transactionProposal) {
        Transaction transaction = new Transaction();
        transaction.setIdentifier(transactionProposal.getIdentifier());
        
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountFullIdentifier(transactionProposal.getAcquiringAccountFullIdentifier());
        acquiringAccount.setAccountType(KyotoAccountType.parse(transactionProposal.getAcquiringAccountType()));
        acquiringAccount.setAccountIdentifier(transactionProposal.getAcquiringAccountIdentifier());
        transaction.setAcquiringAccount(acquiringAccount);
        
        transactionProposal.getBlocks().forEach(b -> {
            b.setTransaction(transaction);
            b.setId(null);
        });
        transaction.setBlocks(transactionProposal.getBlocks());
        transaction.setExecutionDate(transactionProposal.getExecutionDate());
        transaction.setLastUpdated(transactionProposal.getLastUpdated());
        transaction.setQuantity(transactionProposal.getQuantity());
        transaction.setStarted(transactionProposal.getStarted());
        
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountFullIdentifier(transactionProposal.getTransferringAccountFullIdentifier());
        transferringAccount.setAccountType(KyotoAccountType.parse(transactionProposal.getTransferringAccountType()));
        transferringAccount.setAccountIdentifier(transactionProposal.getTransferringAccountIdentifier());
        transaction.setTransferringAccount(transferringAccount);
        
        transaction.setType(transactionProposal.getType());
        transaction.setUnitType(transactionProposal.getUnitType());
        
        return transaction;
    }
}
