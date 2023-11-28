package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

/**
 * Repository for transactions.
 */
public interface TransactionRepository
    extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {

    /**
     * Counts all transactions with transaction type and transferring account identifier and with status not in
     * statuses.
     *
     * @param type                              The transaction type
     * @param transferringAccountFullIdentifier The transferring account full identifier
     * @param statuses                          The statuses that the transactions should not have.
     * @return The count of the transactions with status other than statuses.
     */
    Long countByTypeAndTransferringAccountAccountFullIdentifierAndStatusNotIn(TransactionType type,
                                                                              String transferringAccountFullIdentifier,
                                                                              List<TransactionStatus> statuses);

    /**
     * Counts all transactions with transaction type and with status not in
     * statuses.
     *
     * @param type     The transaction type
     * @param statuses The statuses that the transactions should not have.
     * @return The count of the transactions with status other than statuses.
     */
    Long countByTypeAndStatusNotIn(TransactionType type, List<TransactionStatus> statuses);

    /**
     * Counts all transactions with type, statuses and allocation types.
     *
     * @param type The transaction type
     * @param statuses The statuses that the transactions should have.
     * @param allocationTypes The allocationType that the transactions should have.
     * @return The count of the transactions.
     */
    @Query(value = "select count(1) from transaction t where t.type = ?1 and t.status not in ?2 and cast(t.attributes as json) ->> 'AllocationType' in ?3",
        nativeQuery = true)
    Long countByTypeAndStatuesNotInAndAllocationTypes(String type, List<String> statuses, List<String> allocationTypes);

    /**
     * Counts all transactions with type, statuses, allocation types and year.
     *
     * @param type The transaction type
     * @param statuses The statuses that the transactions should have.
     * @param allocationTypes The allocationType that the transactions should have.
     * @param allocationYear The allocation year.
     * @return The count of the transactions.
     */
    @Query(value = "select count(1) from transaction t where t.type = ?1 and t.status not in ?2 and cast(t.attributes as json) ->> 'AllocationType' in ?3 and cast(t.attributes as json) ->> 'AllocationYear' = ?4",
        nativeQuery = true)
    Long countByTypeAndStatuesNotInAndAllocationTypesAndAllocationYears(String type,
                                                                        List<String> statuses,
                                                                        List<String> allocationTypes,
                                                                        String allocationYear);

    /**
     * Counts all transactions that their status does not match with one of the statuses
     *
     * @param statuses The statuses that the transactions should not have.
     * @return The count of the transactions with status other than statuses.
     */
    Long countByStatusNotIn(List<TransactionStatus> statuses);

    /**
     * Returns all the transactions that their status matches with one of the statuses.
     *
     * @param statuses The statuses that the transactions should be in.
     * @param page The page object.
     * @return The transactions in batches.
     */
    Slice<Transaction> findAllByStatusInOrderByLastUpdatedDesc(List<TransactionStatus> statuses, Pageable page);

    /**
     * Retrieves the next transaction business identifier.
     *
     * @return a number
     */
    @Query(value = "select nextval('transaction_identifier_seq')", nativeQuery = true)
    Long getNextTransactionBusinessIdentifier();

    /**
     * Retrieves a transaction based on its unique business identifier.
     *
     * @param identifier The unique transaction business identifier (e.g. GB100023).
     * @return a transaction
     */
    Transaction findByIdentifier(String identifier);

    /**
     * Retrieves a transaction based on its blocks business identifier.
     *
     * @param identifier The unique transaction business identifier (e.g. GB100023).
     * @return a transaction
     */
    @Query(value = "select t from Transaction t join fetch t.blocks where t.identifier = ?1")
    Transaction findTransactionWithBlocksByIdentifier(String identifier);

    /**
     * Retrieves the delayed transactions.
     *
     * @param status      The status.
     * @param currentTime The current time.
     * @return some transactions.
     */
    List<Transaction> findByStatusEqualsAndExecutionDateBefore(TransactionStatus status, LocalDateTime currentTime);

    /**
     * Retrieve any transactions based on their transferring account identifier, transaction type and status.
     *
     * @param accountIdentifier   the transferring account identifier.
     * @param type                the transaction type.
     * @param transactionStatuses the transaction statuses.
     * @return an list of transactions.
     */
    List<Transaction> findByTransferringAccount_AccountIdentifierAndTypeAndStatusNotIn(
        Long accountIdentifier, TransactionType type, List<TransactionStatus> transactionStatuses);

    /**
     * Retrieve any transactions based on their transferring account identifier, transaction type and status.
     *
     * @param accountIdentifier   the transferring account identifier.
     * @param type                the transaction type.
     * @param transactionStatuses the transaction statuses.
     * @return an list of transactions.
     */
    List<Transaction> findByTransferringAccount_AccountIdentifierAndTypeAndStatus(
        Long accountIdentifier, TransactionType type, TransactionStatus transactionStatus);

    /**
     * Retrieve any transactions based on their acquiring account identifier, transaction type and status.
     *
     * @param accountIdentifier   the acquiring account identifier.
     * @param type                the transaction type.
     * @param transactionStatuses the transaction statuses.
     * @return an list of transactions.
     */
    List<Transaction> findByAcquiringAccount_AccountIdentifierAndTypeAndStatus(
        Long accountIdentifier, TransactionType type, TransactionStatus transactionStatus);
    
    /**
     * Counts transactions for a related (transferring or acquiring) account
     * and specific statuses.
     *
     * @param accountIdentifier   the account identifier. Will act as  both transferring or Acquiring
     * @param transactionStatuses the transaction statuses.
     * @return a transactions Count .
     */
    @Query(value = "select count(t) from Transaction t " +
        " where (t.acquiringAccount.accountIdentifier = :accountIdentifier  or t.transferringAccount.accountIdentifier = :accountIdentifier )  " +
        " and t.status in :transactionStatuses")
    Long countByRelatedAccountAndAndStatusIn(
        @Param("accountIdentifier") Long accountIdentifier,
        @Param("transactionStatuses") List<TransactionStatus> transactionStatuses);


    /**
     * Retrieve any transactions based on their transaction type and signature state.
     *
     * @param transactionTypes the list of transaction types.
     * @return a list of transactions.
     */
    List<Transaction> findByTypeNotInAndSignatureIsNull(List<TransactionType> transactionTypes);

}
