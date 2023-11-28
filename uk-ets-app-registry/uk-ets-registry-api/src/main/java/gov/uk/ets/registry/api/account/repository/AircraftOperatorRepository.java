package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Data repository for aircraft operators.
 */
public interface AircraftOperatorRepository extends JpaRepository<AircraftOperator, Long> {

    @Query(value = "select a.identifier from AircraftOperator a")
    Set<String> findAllAircraftOperators();

    @Query("select id from AircraftOperator where identifier = ?1")
    Long getCompliantEntityId(Long identifier);
}
