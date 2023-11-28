package gov.uk.ets.registry.api.compliance.repository;

import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for emissions' exclusion status.
 */
public interface ExcludeEmissionsRepository extends JpaRepository<ExcludeEmissionsEntry, Long> {

    /**
     * Retrieves exclusion status for a compliant entity on a specific year.
     *
     * @param compliantEntityId the compliant entity for this entry
     * @param year the specified year for this entry
     * @return entry for this entity and this specific year
     */
    ExcludeEmissionsEntry findByCompliantEntityIdAndYear(Long compliantEntityId, Long year);
    
    /**
     * Retrieves exclusion statuses for a compliant entity.
     *
     * @param compliantEntityId the compliant entity for this entry
     * @return a list of exclusion 
     */
    List<ExcludeEmissionsEntry> findByCompliantEntityId(Long compliantEntityId);

    /**
     * Retrieves excluded entries before a specified year for the given compliant entity.
     *
     * @param compliantEntityId the compliant entity for this entry
     * @param year the specified year
     * @return a list of entries 
     */
    @Query("select e from ExcludeEmissionsEntry e " +
        "where e.compliantEntityId =  ?1 and e.year < ?2 and e.excluded = TRUE")
    List<ExcludeEmissionsEntry> findExcludedEntriesBeforeYear(Long compliantEntityId, long year);

    /**
     * Retrieves excluded entries after a specified year for the given compliant entity.
     *
     * @param compliantEntityId the compliant entity for this entry
     * @param year the specified year
     * @return a list of entries 
     */
    @Query("select e from ExcludeEmissionsEntry e " +
        "where e.compliantEntityId =  ?1 and e.year > ?2 and e.excluded = TRUE")
    List<ExcludeEmissionsEntry> findExcludedEntriesAfterYear(Long compliantEntityId, long year);
}
