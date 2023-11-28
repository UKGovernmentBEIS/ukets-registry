package gov.uk.ets.compliance.repository;

import gov.uk.ets.compliance.domain.DynamicComplianceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DynamicComplianceRepository extends JpaRepository<DynamicComplianceEntity, Long> {
}
