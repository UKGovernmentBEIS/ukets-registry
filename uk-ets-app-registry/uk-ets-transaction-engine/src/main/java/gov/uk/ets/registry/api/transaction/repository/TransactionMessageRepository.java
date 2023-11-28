package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.TransactionMessage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for transaction messages.
 */
public interface TransactionMessageRepository extends JpaRepository<TransactionMessage, Long> {
    // nothing to implement here.
}
