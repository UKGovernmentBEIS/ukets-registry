package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for transaction history entries.
 */
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    // nothing to implement here
}
