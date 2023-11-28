package gov.uk.ets.registry.api.compliance.repository;

import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaticComplianceStatusRepository extends JpaRepository<StaticComplianceStatus, Long> {

    Optional<StaticComplianceStatus> findByCompliantEntityIdentifierAndYear(Long compliantEntityId, Long year);

    List<StaticComplianceStatus> findByCompliantEntityIdentifierAndYearGreaterThanEqual(Long compliantEntityId,
                                                                                   Long startYear);

    List<StaticComplianceStatus> findByCompliantEntityIdentifierAndYearBetween(Long compliantEntityId, Long startYear
        , Long endYear);

}
