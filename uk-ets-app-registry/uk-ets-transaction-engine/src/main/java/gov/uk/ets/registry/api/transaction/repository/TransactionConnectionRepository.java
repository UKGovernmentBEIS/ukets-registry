package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionConnection;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@SuppressWarnings("java:S100")
public interface TransactionConnectionRepository extends JpaRepository<TransactionConnection, Long> {

    Long countByObjectTransactionAndTypeAndSubjectTransaction_StatusIn(
        Transaction transaction, TransactionConnectionType type, List<TransactionStatus> status);

    /**
     * Find a transaction connection by the subject transaction, aka the reversal transaction.
     *
     * @param reversal the actual reversal transaction.
     * @param type the transaction connection type.
     * @return the transaction connection.
     */
    Optional<TransactionConnection> findBySubjectTransactionAndType(Transaction reversal, TransactionConnectionType type);

    /**
     * Find a transaction connection by the object transaction, aka the reversed transaction.
     *
     * @param reversed the actual reversed transaction.
     * @param type the transaction connection type.
     * @return the transaction connection.
     */
    Optional<TransactionConnection> findFirstByObjectTransactionAndTypeOrderByDateDesc(Transaction reversed,
                                                                                       TransactionConnectionType type);

}
