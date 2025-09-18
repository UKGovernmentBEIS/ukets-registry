package gov.uk.ets.registry.api.lock;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RegistryTaskLockRepository extends JpaRepository<RegistryTaskLock, Long>  {

    @Modifying
    @Query(value =
        "update registry_task_lock set active = true, acquired_at = now(), acquired_by = ?1 " +
            "where active = false or (acquired_at + ?2 * Interval '1 second') < now();",
        nativeQuery = true)
    void acquireLock(String acquiredBy, int maxLockSeconds);

    Optional<RegistryTaskLock> findByAcquiredBy(String acquiredBy);
}
