package gov.uk.ets.registry.api.lock;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistryTaskLogRepository extends JpaRepository<RegistryTaskLog, String>  {

    Optional<RegistryTaskLog> findFirstByOrderByExecutedDateDesc();

}
