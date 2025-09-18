package gov.uk.ets.registry.api.transaction.repository;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper;
import gov.uk.ets.registry.api.helper.persistence.TransactionModelTestHelper.CreateETSTransactionCommand;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@PostgresJpaTest
class TransactionRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TransactionRepository transactionRepository;

    TransactionModelTestHelper helper;

    @BeforeEach
    void init() {
        helper = new TransactionModelTestHelper(entityManager);
    }

    @Test
    void countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn() throws Exception {
        addTransaction("UK213123", TransactionStatus.FAILED);
        addTransaction("UK213143", TransactionStatus.AWAITING_APPROVAL);
        entityManager.flush();

        Long count = transactionRepository.countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn(
            TransactionType.CentralTransferAllowances, "transferring-test-account-key",
            TransactionStatus.getFinalStatuses());

        assertEquals((long)1, count);

        count = transactionRepository.countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn(
            TransactionType.CentralTransferAllowances, "transferring-test-account-key",
            List.of(TransactionStatus.FAILED, TransactionStatus.AWAITING_APPROVAL));

        assertEquals(0, count);
    }

    @Test
    void countByTypeAndStatusNotIn() throws Exception {
        addTransaction("UK213123", TransactionStatus.FAILED);
        addTransaction("UK213143", TransactionStatus.AWAITING_APPROVAL);
        entityManager.flush();

        Long count = transactionRepository.countByTypeAndStatusNotIn(TransactionType.CentralTransferAllowances, TransactionStatus.getFinalStatuses());
        assertEquals(1, count);
    }

    private void addTransaction(String identifier, TransactionStatus status) throws Exception {
        helper.addETSTransaction(CreateETSTransactionCommand.builder()
            .identifier(identifier)
            .status(status)
            .build());
    }

    @Test
    void testFindByStatusEqualsAndExecutionDateBefore() throws Exception {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        // Add transactions with different statuses and execution dates
        addTransaction("UK213111", TransactionStatus.DELAYED, currentTime.truncatedTo(ChronoUnit.MINUTES).minusMinutes(2));
        addTransaction("UK213112", TransactionStatus.DELAYED, currentTime.truncatedTo(ChronoUnit.MINUTES).plusMinutes(2));
        addTransaction("UK213113", TransactionStatus.COMPLETED, currentTime.truncatedTo(ChronoUnit.MINUTES).minusMinutes(2));
        addTransaction("UK213114", TransactionStatus.DELAYED, currentTime.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)); // Add edge case transaction
        entityManager.flush();

        // We add an extra minute to the repo method in order to simulate delayed scheduler's behavior
        List<Transaction> transactions = transactionRepository.findByStatusEqualsAndExecutionDateBefore(
                TransactionStatus.DELAYED, currentTime.truncatedTo(ChronoUnit.MINUTES).plusMinutes(2));

        // Check the results
        assertEquals(2, transactions.size()); // Expect two transactions to be returned
        assertTrue(transactions.stream().anyMatch(t -> t.getIdentifier().equals("UK213111")));
        assertTrue(transactions.stream().anyMatch(t -> t.getIdentifier().equals("UK213114")));
    }


    private void addTransaction(String identifier, TransactionStatus status, LocalDateTime executionDate) throws Exception {
        helper.addETSTransaction(CreateETSTransactionCommand.builder()
                .identifier(identifier)
                .status(status)
                .executionDate(executionDate)
                .build());
    }

}