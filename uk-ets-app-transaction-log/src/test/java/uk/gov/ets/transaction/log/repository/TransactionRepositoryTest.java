package uk.gov.ets.transaction.log.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;
import uk.gov.ets.transaction.log.helper.BaseJpaTest;


public class TransactionRepositoryTest extends BaseJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        Transaction transaction = new Transaction();
        transaction.setIdentifier("UK100051");
        transaction.setStatus(TransactionStatus.ACCEPTED);
        transaction.setType(TransactionType.TransferAllowances);
        transaction.setUnitType(UnitType.ALLOWANCE);

        entityManager.persist(transaction);
    }

    @Test
    public void findByIdentifier() {
        Optional<Transaction> transaction = transactionRepository.findByIdentifier("UK100051");
        assertTrue(transaction.isPresent());
    }

    @Test
    public void shouldRetrieveTransactionsOnlyAfterSpecifiedDate() {
        Transaction transaction1 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100052", TransactionStatus.COMPLETED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 0));
        Transaction transaction2 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100053", TransactionStatus.COMPLETED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 1));
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);

        List<Transaction> transactions =
            transactionRepository
                .findByStartedAfterAndStatusEquals(convertToDate(LocalDateTime.of(2020, 7, 1, 0, 0, 0)),
                    TransactionStatus.COMPLETED);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getIdentifier()).isEqualTo("UK100053");
    }

    @Test
    public void shouldNotRetrieveTransactionsOtherThanCompleted() {
        Transaction transaction1 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100052", TransactionStatus.TERMINATED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 1));
        Transaction transaction2 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100053", TransactionStatus.COMPLETED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 1));
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);

        List<Transaction> transactions =
            transactionRepository
                .findByStartedAfterAndStatusEquals(convertToDate(LocalDateTime.of(2020, 7, 1, 0, 0, 0)),
                    TransactionStatus.COMPLETED);

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getIdentifier()).isEqualTo("UK100053");
    }

    @Test
    public void shouldRetrieveAllTransactionsWhenFirstReconciliation() {
        Transaction transaction1 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100052", TransactionStatus.COMPLETED,
                LocalDateTime.of(2018, 7, 1, 0, 0, 1));
        Transaction transaction2 =
            createTransactionWithIdStatusAndAccountsAtDateStarted("UK100053", TransactionStatus.COMPLETED,
                LocalDateTime.of(2020, 7, 1, 0, 0, 1));
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);

        List<Transaction> transactions =
            transactionRepository
                .findByStatus(TransactionStatus.COMPLETED);

        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getIdentifier()).isEqualTo("UK100052");
    }

}
