package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface ActivityTypeRepository extends JpaRepository<ActivityType, Long>  {

    Set<ActivityType> findByInstallation_id(Long installationId);
}
