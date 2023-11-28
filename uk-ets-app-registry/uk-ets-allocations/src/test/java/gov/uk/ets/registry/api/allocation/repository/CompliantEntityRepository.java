package gov.uk.ets.registry.api.allocation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gov.uk.ets.registry.api.allocation.CompliantEntity;

/**
 * Data repository for compliant entities.
 */
public interface CompliantEntityRepository extends JpaRepository<CompliantEntity, Long> {

}
