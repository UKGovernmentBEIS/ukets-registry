package gov.uk.ets.registry.api.user.migration;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockConfigurationExtractor;
import net.javacrumbs.shedlock.core.LockManager;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SchedulerHelper {
    private static final int LOCK_AT_MOST_SECONDS = 60;
    private static final int LOCK_AT_LEAST_SECONDS = 2;
    
    private final LockProvider lockProvider;
    private final TaskScheduler taskScheduler;
    
    public LockableTaskScheduler createScheduler(String shedlockName) {
        Instant createdAt = Instant.now();
        Duration lockAtMostFor = Duration.ofSeconds(LOCK_AT_MOST_SECONDS);
        Duration lockAtLeastFor = Duration.ofSeconds(LOCK_AT_LEAST_SECONDS);

        LockConfiguration lockConfiguration =
            new LockConfiguration(createdAt, shedlockName, lockAtMostFor, lockAtLeastFor);
        LockConfigurationExtractor lockConfigurationExtractor = (Runnable task) -> Optional.of(lockConfiguration);
        LockManager lockManager = new DefaultLockManager(lockProvider, lockConfigurationExtractor);
        return new LockableTaskScheduler(taskScheduler, lockManager);
    }
}
