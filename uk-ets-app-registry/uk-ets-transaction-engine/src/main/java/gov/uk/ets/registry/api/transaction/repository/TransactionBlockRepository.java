package gov.uk.ets.registry.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;

/**
 * Repository for transaction blocks.
 */
public interface TransactionBlockRepository extends JpaRepository<TransactionBlock, Long> , QuerydslPredicateExecutor<TransactionBlock> {

    /**
     * Retrieves the transaction blocks of the provided transaction.
     * @param transactionIdentifier The unique transaction business identifier.
     * @return some blocks
     */
    @SuppressWarnings("java:S100")
    List<TransactionBlock> findByTransaction_Identifier(String transactionIdentifier);
    
    /**
     * Retrieves the distinct unit types of the transactions in the specified status and acquiring account before the lastUpdated date
     * @param acquiringAccountIdentifier the identifier of the account
     * @param statuses the transaction statuses
     * @param lastUpdated the last update date
     * @return the distinct unit types
     */
    @Query("select distinct tb.type from TransactionBlock tb where tb.transaction.acquiringAccount.accountIdentifier = ?1 and tb.transaction.status in ?2 and  tb.transaction.lastUpdated < ?3 ")
    Set<UnitType> findUnitTypesByAcquiringAccountAndStatusInAndLastUpdatedBefore(
          Long acquiringAccountIdentifier,
          Iterable<TransactionStatus> statuses,
          Date lastUpdated);
}
