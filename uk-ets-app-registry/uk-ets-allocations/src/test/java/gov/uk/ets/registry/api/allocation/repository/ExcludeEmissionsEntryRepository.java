package gov.uk.ets.registry.api.allocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.registry.api.allocation.ExcludeEmissionsEntry;

//Used only for testing purposes
public interface ExcludeEmissionsEntryRepository extends JpaRepository<ExcludeEmissionsEntry, Long> {

}
