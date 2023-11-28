package uk.gov.ets.transaction.log.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import uk.gov.ets.transaction.log.domain.UnitBlock;
import uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary;
import uk.gov.ets.transaction.log.domain.type.UnitType;

/**
 * Repository for unit blocks.
 */
public interface UnitBlockRepository extends JpaRepository<UnitBlock, Long>, QuerydslPredicateExecutor<UnitBlock> {

    /**
     * Returns the unit blocks belonging to a particular account.
     *
     * @param accountIdentifier The (business) account identifier.
     * @return some unit blocks.
     */
    List<UnitBlock> findByAccountIdentifier(Long accountIdentifier);
    // @formatter:off
    /**
     * Find UnitBlocks that somehow overlap with the specified start & end.
     * Examples of OverlappingBlocks:
     *                                start |                                       end |
     *                                        |*************************|
     *                                                     |*********|
     *                              |***************************************|
     *                       |**********|
     *                                                                                     |********************|
     *
     * @param start the starting block
     * @param end   the ending block
     * @return the list of blocks found
     */
    // @formatter:on
    List<UnitBlock> findOverlappingBlocksByEndBlockGreaterThanEqualAndStartBlockLessThanEqual(Long start, Long end);

    /**
     * Get blocks by serials.
     *
     * @param start the start block
     * @param end   the end block
     * @return the list of unit blocks found
     */
    List<UnitBlock> findIncludedBlocksByStartBlockGreaterThanEqualAndEndBlockLessThanEqual(Long start, Long end);

    @Query("select new uk.gov.ets.transaction.log.messaging.types.ReconciliationEntrySummary(" +
        "accountIdentifier," +
        "type," +
        "sum (endBlock - startBlock + 1)) " +
        "from UnitBlock " +
        "group by accountIdentifier, type " +
        "order by accountIdentifier, type")
    List<ReconciliationEntrySummary> retrieveEntriesForAllAccounts();

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

    /**
     * Returns the unit block that contains the provided serial numbers.
     * @param startBlock The start block serial number.
     * @param endBlock The end block serial number.
     * @return a unit block.
     */
    Optional<UnitBlock> findByStartBlockLessThanEqualAndEndBlockGreaterThanEqualAndAccountIdentifierEquals(
        Long startBlock, Long endBlock, Long accountIdentifier);

}
