package gov.uk.ets.registry.api.allocation.repository;

import gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for allocation entries.
 */
public interface AllocationEntryRepository extends JpaRepository<AllocationEntry, Long> {

    @Query(
        "select new gov.uk.ets.registry.api.allocation.data.AllocationSummary(" +
        "           yr.year,                     " +
        "           coalesce(en.entitlement, 0), " +
        "           coalesce(en.allocated, 0) - coalesce(en.returned, 0) - coalesce(en.reversed, 0), " +
        "           st.status,                " +
        "           eee.excluded)                    " +
        "      from AllocationEntry en           " +
        "      join en.allocationYear yr         " +
        "      join yr.allocationStatuses st     " +
        "      left join CompliantEntity ce    " +
        "      on ce.id = en.compliantEntityId  " +
        "      left join ExcludeEmissionsEntry eee    " +
        "      on ce.identifier = eee.compliantEntityId and yr.year = eee.year    " +
        "     where en.compliantEntityId = ?1    " +
        "       and en.type = ?2                 " +
        "       and st.compliantEntityId = ?1    " +
        "     order by yr.year asc               ")
    List<AllocationSummary> retrieveAllocationEntries(Long compliantEntityId, AllocationType type);

    @Query(
        "select new gov.uk.ets.registry.api.allocation.data.AllocationSummary(" +
            "           yr.year,                     " +
            "           coalesce(en.entitlement, 0), " +
            "           coalesce(en.allocated, 0) - coalesce(en.returned, 0) - coalesce(en.reversed, 0), " +
            "           st.status,                " +
            "           eee.excluded," +
            "           en.type)                    " +
            "      from AllocationEntry en           " +
            "      join en.allocationYear yr         " +
            "      join yr.allocationStatuses st     " +
            "      left join CompliantEntity ce    " +
            "      on ce.id = en.compliantEntityId  " +
            "      left join ExcludeEmissionsEntry eee    " +
            "      on ce.identifier = eee.compliantEntityId and yr.year = eee.year    " +
            "     where en.compliantEntityId = ?1    " +
            "       and st.compliantEntityId = ?1    " +
            "     order by yr.year asc               ")
    List<AllocationSummary> retrieveAllocationEntries(Long compliantEntityId);

    @Query(
        "select new gov.uk.ets.registry.api.allocation.data.AllocationSummary(" +
        "           yr.year,                     " +
        "           coalesce(en.entitlement, 0), " +
        "           coalesce(en.allocated, 0) - coalesce(en.returned, 0) - coalesce(en.reversed, 0), " +
        "           st.status,                   " +
        "           eee.excluded)                   " +
        "      from AllocationEntry en           " +
        "      join en.allocationYear yr         " +
        "      join yr.allocationStatuses st     " +
        "      left join CompliantEntity ce    " +
        "      on ce.id = en.compliantEntityId  " +
        "      left join ExcludeEmissionsEntry eee    " +
        "      on ce.identifier = eee.compliantEntityId and yr.year = eee.year    " +
        "     where en.compliantEntityId = ?1    " +
        "       and en.type = ?2                 " +
        "       and st.compliantEntityId = ?1    " +
        "       and yr.year = ?3                 ")
    AllocationSummary retrieveAllocationEntry(Long compliantEntityId, AllocationType type, Integer year);

    @Query(
        "select new gov.uk.ets.registry.api.allocation.data.AllocationSummary(yr.year, st.status) " +
        "      from AllocationEntry en        " +
        "      join en.allocationYear yr      " +
        "      join yr.allocationStatuses st  " +
        "     where en.compliantEntityId = ?1 " +
        "       and st.compliantEntityId = ?1 " +
        "  group by yr.year, st.status        ")
    List<AllocationSummary> retrieveAllocationStatus(Long compliantEntityId);

    @SuppressWarnings("java:S100")
    AllocationEntry findByCompliantEntityIdAndTypeAndAllocationYear_Year(Long compliantEntityId, AllocationType type, Integer year);


    @Query(value = "select distinct ce.identifier from compliant_entity ce " +
                  "join allocation_entry al on ce.id = al.compliant_entity_id " +
                  "join  allocation_year ay on al.allocation_year_id = ay.id " +
                  "where ay.year in ?1 and al.type = ?2 ",nativeQuery = true)
    List<Long> retrieveAllocationEntriesByYearsAndStatus(List<Integer> allocationYears, String type);

    @Query(
        "select new gov.uk.ets.registry.api.allocation.data.AllocationSummary(" +
        "           en.compliantEntityId,          " +
        "           coalesce(en.entitlement, 0),   " +
        "           coalesce(en.allocated, 0) - coalesce(en.returned, 0) - coalesce(en.reversed, 0), " +
        "           st.status,                     " +
        "           eee.excluded)                  " +
        "      from AllocationEntry en          " +
        "      join en.allocationYear yr           " +
        "      join yr.allocationStatuses st       " +
        "      left join CompliantEntity ce    " +
        "      on ce.id = en.compliantEntityId  " +
        "      left join ExcludeEmissionsEntry eee    " +
        "      on ce.identifier = eee.compliantEntityId and yr.year = eee.year    " +
        "     where yr.year = ?1                   " +
        "       and en.type = ?2                   " +
        "       and st.compliantEntityId = en.compliantEntityId ")
    List<AllocationSummary> calculateAllocations(Integer year, AllocationType allocationType);

    @Query(nativeQuery = true, value =
        " select compliant_entity_id               " +
        "   from account                           " +
        "  where compliant_entity_id is not null   " +
        "    and account_status in ('SUSPENDED', 'CLOSED');")
    Set<Long> retrieveEntitiesWithInvalidAccountStatus();

    @Query(nativeQuery = true, value =
            " select compliant_entity_id               " +
            "   from account                           " +
            "  where compliant_entity_id is not null   " +
            "    and account_status in ('TRANSFER_PENDING');")
    Set<Long> retrieveEntitiesWithTransferPendingAccountStatus();

    @Query("select new gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary( " +
           " en.compliantEntityId, " +
           " coalesce(en.entitlement, 0), " +
           " coalesce(en.allocated, 0) - coalesce(en.returned, 0) - coalesce(en.reversed, 0), " +
           " en.type," +
           " eee.excluded)" +
           " from AllocationEntry en        " +
           " join en.allocationYear yr      " +
           " left join CompliantEntity ce    " +
           " on ce.id = en.compliantEntityId  " +
           " left join ExcludeEmissionsEntry eee    " +
           " on ce.identifier = eee.compliantEntityId and yr.year = eee.year    " +
           " where yr.year <= ?1            " +
           " and en.compliantEntityId = ?2 ")
    List<AllocationClassificationSummary> calculateAllocationClassification(Integer year, Long compliantEntityId);

    /**
     * Retrieves the installation / aircraft operator identifier.
     * @param compliantEntityId The installation / aircraft operator primary key.
     * @return the identifier.
     */
    @Query(value = "select identifier from compliant_entity where id = ?1", nativeQuery = true)
    Long retrieveIdentifier(Long compliantEntityId);

    @Query(value = "select count(id) from exclude_emissions_entry where compliant_entity_id = ?1 and year = ?2 and excluded is true", nativeQuery = true)
    Integer retrieveNumberOfExcludedEmissionsByCompliantEntityAndAllocationYear(Long compliantEntityIdentifier, Integer year);

    /**
     * Retrieves the full account identifier.
     * @param compliantEntityId The installation / aircraft operator primary key.
     * @return the full account identifier.
     */
    @Query(value = "select full_identifier from account where compliant_entity_id = ?1", nativeQuery = true)
    String retrieveAccountFullIdentifier(Long compliantEntityId);

    /**
     * Retrieves the compliant entity primary key.
     * @param accountIdentifier The account identifier.
     * @return the compliant entity key.
     */
    @Query(value = "select compliant_entity_id from account where identifier = ?1", nativeQuery = true)
    Long retrieveCompliantEntityId(Long accountIdentifier);

    /**
     * Updates the allocation classification.
     * @param compliantEntityId The compliant entity id.
     * @param classification The classification.
     */
    @Modifying
    @Query(value = "update compliant_entity set allocation_classification = ?2 where id = ?1", nativeQuery = true)
    void updateAllocationClassification(Long compliantEntityId, String classification);

    @Query(value = "select start_year from compliant_entity where id = ?1", nativeQuery = true)
    Integer retrieveEntityStartYear(Long compliantEntityId);

    @Query(value = "select end_year from compliant_entity where id = ?1", nativeQuery = true)
    Integer retrieveEntityEndYear(Long compliantEntityId);

}
