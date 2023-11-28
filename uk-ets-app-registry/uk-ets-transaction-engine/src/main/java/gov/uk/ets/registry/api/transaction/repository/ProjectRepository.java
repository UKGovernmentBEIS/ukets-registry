package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for projects.
 */
public interface ProjectRepository extends JpaRepository<UnitBlock, Long> {

    /**
     * Retrieves the distinct projects of the account.
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @return the holdings.
     */
    @Query("select distinct projectNumber          " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type = ?2                       " +
            "  and reservedForTransaction is null  ")
    List<String> getProjects(Long accountIdentifier, UnitType unitType);

    /**
     * Retrieves the distinct projects of the account.
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @param commitmentPeriod The commitment period.
     * @return the holdings.
     */
    @Query("select distinct projectNumber  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type = ?2                       " +
            "  and applicablePeriod = ?3           " +
            "  and reservedForTransaction is null  ")
    List<String> getProjects(Long accountIdentifier, UnitType unitType, CommitmentPeriod commitmentPeriod);

    /**
     * Retrieves the distinct environmental activities of the account.
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @return the holdings.
     */
    @Query("select distinct environmentalActivity  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type = ?2                       " +
            "  and reservedForTransaction is null  ")
    List<EnvironmentalActivity> getEnvironmentalActivities(Long accountIdentifier, UnitType unitType);

    /**
     * Retrieves the distinct environmental activities of the account.
     * @param accountIdentifier The account identifier.
     * @param unitType The unit type.
     * @param commitmentPeriod The commitment period.
     * @return the holdings.
     */
    @Query("select distinct environmentalActivity  " +
            " from UnitBlock                       " +
            "where accountIdentifier = ?1          " +
            "  and type = ?2                       " +
            "  and applicablePeriod = ?3           " +
            "  and reservedForTransaction is null  ")
    List<EnvironmentalActivity> getEnvironmentalActivities(Long accountIdentifier, UnitType unitType, CommitmentPeriod commitmentPeriod);

}
