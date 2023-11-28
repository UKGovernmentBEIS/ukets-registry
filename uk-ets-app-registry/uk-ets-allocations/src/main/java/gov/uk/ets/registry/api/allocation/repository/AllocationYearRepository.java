package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for allocation years.
 */
public interface AllocationYearRepository extends JpaRepository<AllocationYear, Long> {

    /**
     * Retrieves the allocation year, based on the provided year.
     * @param year The year.
     * @return the allocation year.
     */
    AllocationYear findByYear(Integer year);

    @Query(
        "select new gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary(" +
        "           ay.year, ay.initialYearlyCap, ay.consumedYearlyCap, ay.initialYearlyCap - ay.consumedYearlyCap - ay.pendingYearlyCap) " +
        "  from AllocationYear ay       " +
        "  join ay.period pe            " +
        "  join pe.phase ph             " +
        " where ph.id = (               " +
        "    select aph.id              " +
        "      from AllocationPhase aph " +
        "      join aph.periods ape     " +
        "      join ape.years aly       " +
        "     where aly.year = ?1)      ")
    List<IssuanceBlockSummary> getBlocks(Integer year);

    @Query("select a from AllocationYear a where a.period.phase.code = ?1 ")
    List<AllocationYear> findByPhaseCode(int phaseCode);

    /**
     * Calculates the year planned quantity (entitlement).
     * @param year The allocation year.
     * @return the entitlement.
     */
    @Query(
        "select sum(coalesce(en.entitlement, 0)) " +
        "      from AllocationEntry en           " +
        "      join en.allocationYear yr         " +
        "     where yr.year = ?1                 ")
    Long calculateYearlyEntitlement(Integer year);

    /**
     * Calculates the yearly allocated quantity.
     * @param year The allocation year.
     * @return the allocated quantity.
     */
    @Query(
        "select sum(coalesce(en.allocated, 0) - coalesce(en.returned, 0) - coalesce(en.reversed, 0)) " +
        "  from AllocationEntry en       " +
        "  join en.allocationYear yr     " +
        " where yr.year = ?1             ")
    Long calculateYearlyAllocated(Integer year);

    /**
     * Calculates the reversed allowances during this allocation year,
     * performed via Reversal of Allocations.
     * @param year The allocation year.
     * @return the reversed allowances.
     */
    @Query(
        "select sum(coalesce(en.reversed, 0)) " +
            "      from AllocationEntry en           " +
            "      join en.allocationYear yr         " +
            "     where yr.year = ?1                 ")
    Long calculateYearlyReversed(Integer year);

    /**
     * Calculates the returned allowances during this allocation year,
     * performed via Return of Excess Allocations.
     * @param year The allocation year.
     * @return the returned allowances.
     */
    @Query(
        "select sum(coalesce(en.returned, 0)) " +
            "      from AllocationEntry en           " +
            "      join en.allocationYear yr         " +
            "     where yr.year = ?1                 ")
    Long calculateYearlyReturned(Integer year);

}
