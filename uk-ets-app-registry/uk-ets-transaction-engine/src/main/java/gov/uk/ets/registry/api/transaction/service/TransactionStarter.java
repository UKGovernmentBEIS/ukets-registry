package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.domain.QTransaction;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@AllArgsConstructor
@Log4j2
public class TransactionStarter {

    /**
     * Repository for transactions.
     */
    private TransactionRepository transactionRepository;

    /**
     * Service for transactions.
     */
    private TransactionService transactionService;

    /**
     * Service for accounts.
     */
    private TransactionAccountService transactionAccountService;

    /**
     * Starts the delayed transactions.
     */
    @Transactional
    public void startDelayedTransactions() {

        List<Transaction> transactions = transactionRepository.findByStatusEqualsAndExecutionDateBefore(
                TransactionStatus.DELAYED, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(1));

        if (CollectionUtils.isEmpty(transactions)) {
            log.info("No eligible delayed transactions found");
            return;
        }

        for (Transaction transaction : transactions) {
            log.info("Starting delayed transaction {}", transaction.getIdentifier());
            AccountStatus acquiringAccountStatus = transactionAccountService
                    .populateAcquiringAccountStatus(transaction.getAcquiringAccount().getAccountFullIdentifier());

            if (!transaction.getType().isExternal()
                    && acquiringAccountStatus != null
                    && AccountStatus.isClosedOrHasClosureRequests(acquiringAccountStatus)) {
                transactionService.handleTransactionFailure(transaction);
                continue;
            }
            try {
                transactionService.startTransaction(transaction);
            } catch (Exception exception) {
                log.warn("Transaction {} failed to start", transaction.getIdentifier(), exception);
                transactionService.handleTransactionFailure(transaction);
            }
        }
    }

    /**
     * Checks for transactions that are unexpectedly stopped.
     */
    @Transactional
    public void checkForStoppedTransactions(int maxPeriodInHours) {
        Date startedDate = Date.from(LocalDateTime.now().minusHours(maxPeriodInHours).atZone(ZoneId.systemDefault()).toInstant());
        List<Transaction> stoppedTransactions = StreamSupport.stream(transactionRepository.findAll(
                                QTransaction.transaction.started.before(startedDate)
                                        .and(QTransaction.transaction.status.eq(TransactionStatus.PROPOSED)))
                        .spliterator(), false)
                .collect(Collectors.toList());
        for (Transaction transaction : stoppedTransactions) {
            transactionService.handleTransactionFailure(transaction);
        }
    }

}
