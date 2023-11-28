package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Repository for unit blocks.
 */
public interface UnitBlockRepository extends JpaRepository<UnitBlock, Long>, QuerydslPredicateExecutor<UnitBlock> {

    /**
     * Returns the unit blocks which are reserved for a specific transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     * @return some unit blocks.
     */
    List<UnitBlock> findByReservedForTransaction(String transactionIdentifier);

    /**
     * Returns the unit blocks which are reserved for a specific Replacement transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     * @return some unit blocks.
     */
    List<UnitBlock> findByReservedForReplacement(String transactionIdentifier);

    /**
     * Returns the unit blocks belonging to a particular account.
     *
     * @param accountIdentifier The account identifier.
     * @return some unit blocks.
     */
    List<UnitBlock> findByAccountIdentifier(Long accountIdentifier);

    /**
     * Releases the units reserved for this transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     */
    @Modifying
    @Query("update UnitBlock set reservedForTransaction = null where reservedForTransaction = ?1")
    void releaseReservedBlocks(String transactionIdentifier);

    /**
     * Releases the units reserved for this Replacement transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     */
    @Modifying
    @Query("update UnitBlock set reservedForReplacement = null where reservedForReplacement = ?1")
    void releaseReservedForReplacementBlocks(String transactionIdentifier);

    /**
     * Acquires the reserved units for this transaction.
     *
     * @param transactionIdentifier The transaction identifier.
     */
    @Modifying
    @Query("update UnitBlock set reservedForTransaction = null, accountIdentifier = ?2, acquisitionDate = ?3 where reservedForTransaction = ?1")
    void acquireReservedBlocks(String transactionIdentifier, Long accountIdentifier, Date acquisitionDate);

    /**
     * Retrieves a specific range of unit blocks
     *
     * @param startBlock    The starting block
     * @param endBlock      The ending block
     * @return some unit blocks
     */
    @Query("select ub from UnitBlock ub where ub.startBlock >=?1 and ub.endBlock <=?2 and ub.originatingCountryCode = ?3 and ub.type in ?4")
    List<UnitBlock> findBySerialBlockRange(Long startBlock, Long endBlock,String originatingCountryCode, List<UnitType> unitTypes);

    /**
     * Retrieves the unit blocks for the specific project number and unit type that is not reserver for replacement
     *
     * @param unitType      The unit type
     * @param projectNumber The project number
     * @return the unit blocks
     */
    @Query("select ub from UnitBlock ub where ub.type = ?1 and ub.projectNumber = ?2 and ub.reservedForReplacement is null and (ub.replaced = false or ub.replaced is null)")
    List<UnitBlock> findByUnitTypeAndProject(UnitType unitType, String projectNumber);

    /**
     * Retrieves an exact range of unit blocks.
     *
     * @param startBlock    The starting block
     * @param endBlock      The ending block
     * @return some unit blocks
     */
    Optional<UnitBlock> findByStartBlockEqualsAndEndBlockEquals(Long startBlock, Long endBlock);

    /**
     * Retrieves the next unit block start number.
     * @return a number
     */
    @Query(value = "select nextval('issuance_seq')", nativeQuery = true)
    Long getNextAvailableSerialNumber();

    /**
     * Set the next unit block start number.
     * @return a number
     */
    @Query(value = "select setval('issuance_seq', ?1, false)", nativeQuery = true)
    Long updateNextAvailableSerialNumber(Long newBlockStart);

    /**
     * Calculates the available quantity of an account.
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @return the quantity.
     */
    @Query(
        "select sum(ub.endBlock - ub.startBlock + 1) " +
        "  from UnitBlock ub                         " +
        " where ub.accountIdentifier = ?1            " +
        "   and ub.type = ?2                         ")
    Long calculateAvailableQuantity(Long accountIdentifier, UnitType unitType);

}
