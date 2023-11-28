package uk.gov.ets.transaction.log.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import uk.gov.ets.transaction.log.domain.Transaction;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;

public interface TransactionRepository
    extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction>, TransactionCustomRepository {

    /**
     * Retrieves a transaction based on its unique business identifier.
     *
     * @param identifier The unique transaction business identifier (e.g. GB100023).
     * @return a transaction
     */
    Optional<Transaction> findByIdentifier(String identifier);
    
    /**
     * Retrieves transactions being in a specific status.
     */
    List<Transaction> findByStatus(TransactionStatus status);
}
