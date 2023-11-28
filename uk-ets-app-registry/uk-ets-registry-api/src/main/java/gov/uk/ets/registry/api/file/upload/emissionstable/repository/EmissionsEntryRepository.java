package gov.uk.ets.registry.api.file.upload.emissionstable.repository;

import gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for emission entries.
 */
public interface EmissionsEntryRepository extends JpaRepository<EmissionsEntry, Long> {

	/**
	 * Retrieves all entries for a compliant entity on a specific year.
	 *
	 * @param compliantEntityId
	 *            the compliant entity for this entry
	 * @param year
	 *            the specified year for this entry
	 * @return all entries for this entity and this specific year
	 */
	List<EmissionsEntry> findAllByCompliantEntityIdAndYear(
	        Long compliantEntityId, Long year);

	@Query("select new gov.uk.ets.registry.api.compliance.web.model.VerifiedEmissionsDTO(e.compliantEntityId,e.year,cast(e.emissions as string),e.uploadDate) "
		    + "from CompliantEntity c inner join EmissionsEntry e on e.compliantEntityId = c.identifier "
			+ "where c.identifier = ?1 "
		    + "group by e.compliantEntityId,e.year,e.emissions,e.uploadDate "
		    + "having e.uploadDate = (select max(m.uploadDate) from EmissionsEntry m where e.compliantEntityId=m.compliantEntityId and e.year=m.year) "
		    + "order by e.year asc")
	List<VerifiedEmissionsDTO> findLatestByCompliantEntityIdentifier(Long compliantEntityId);

    @Query("select e from EmissionsEntry e " +
        "where e.compliantEntityId =  ?1 and e.year < ?2 and e.emissions >= 0")
    List<EmissionsEntry> findNonEmptyEntriesBeforeYear(Long compliantEntityId, long year);

    List<EmissionsEntry> findByCompliantEntityIdAndYearBefore(Long compliantEntityId, long year);
    
    @Query("select e from EmissionsEntry e " +
        "where e.compliantEntityId =  ?1 and e.year > ?2 and e.emissions >= 0")
    List<EmissionsEntry> findNonEmptyEntriesAfterYear(Long compliantEntityId, long year);
    
	@Query("select e.emissions as e "
		    + "from Account a inner join a.compliantEntity c inner join EmissionsEntry e on e.compliantEntityId = c.identifier "
			+ "where a.identifier = ?1 "
		    + "group by e.compliantEntityId,e.year,e.emissions,e.uploadDate "
		    + "having e.uploadDate = (select max(m.uploadDate) from EmissionsEntry m where e.compliantEntityId=m.compliantEntityId and e.year=m.year) "
		    + "order by e.year asc")
	List<Long> findTotalVerifiedEmissionsByAccountIdentifier(Long compliantEntityId);
}
