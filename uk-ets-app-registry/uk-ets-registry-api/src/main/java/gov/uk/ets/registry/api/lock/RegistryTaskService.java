package gov.uk.ets.registry.api.lock;

import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistryTaskService {

    private static final String APPLICATION_UUID = UUID.randomUUID().toString();
    private static final int MAX_LOCK_SECONDS = 60;
    private final RegistryTaskLockRepository lockRepository;
    private final RegistryTaskLogRepository logRepository;

    public boolean sameAsLatest(String identifier) {
        return logRepository.findFirstByOrderByExecutedDateDesc()
            .map(RegistryTaskLog::getId)
            .filter(id -> id.equals(identifier))
            .isPresent();
    }

    @Transactional
    public boolean acquireLock() {
        lockRepository.acquireLock(APPLICATION_UUID, MAX_LOCK_SECONDS);
        return lockRepository.findByAcquiredBy(APPLICATION_UUID).isPresent();
    }

    /**
     * Release the lock and mark the process identifier as done.
     *
     * @param identifier process identifier
     */
    @Transactional
    public void release(String identifier) {
        RegistryTaskLock registryTaskLock = lockRepository.findByAcquiredBy(APPLICATION_UUID)
            .orElseThrow(() -> new RuntimeException("Could not release RegistryTaskLock for owner: " + APPLICATION_UUID));

        RegistryTaskLog registryTaskLog = new RegistryTaskLog();
        registryTaskLog.setId(identifier);
        registryTaskLog.setExecutedDate(new Date());

        logRepository.save(registryTaskLog);

        registryTaskLock.setActive(false);
        registryTaskLock.setAcquiredAt(null);
        registryTaskLock.setAcquiredBy(null);

        lockRepository.save(registryTaskLock);
    }
}
