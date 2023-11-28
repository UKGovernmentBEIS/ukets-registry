package gov.uk.ets.registry.api.migration;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.javacrumbs.shedlock.core.DefaultLockManager;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockConfigurationExtractor;
import net.javacrumbs.shedlock.core.LockManager;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.scheduling.TaskScheduler;

/**
 * Interface to be implemented by all classes requiring a scheduled migration functionality.
 * Provides a default scheduled implementation that uses a LockableTaskScheduler.
 * 
 * @author P35036
 * @since v3.7.0
 */
public interface ScheduledMigrator {

    int LOCK_AT_MOST_SECONDS = 60;
    int LOCK_AT_LEAST_SECONDS = 2;
    
    /**
     * Schedule the provided migrator using the lockProvider & taskScheduler.
     */
    default void schedule(LockProvider lockProvider,TaskScheduler taskScheduler,Migrator migrator) 
        throws Exception {
        LockableTaskScheduler lockableTaskScheduler = createScheduler(lockProvider,taskScheduler);
        lockableTaskScheduler.schedule(migrator::migrate, Instant.now());
    }
    
    private LockableTaskScheduler createScheduler(LockProvider lockProvider,TaskScheduler taskScheduler) {
        LockConfiguration lockConfiguration =
            new LockConfiguration(Instant.now(), getSchedulerLockName(), 
                Duration.ofSeconds(LOCK_AT_MOST_SECONDS),
                Duration.ofSeconds(LOCK_AT_LEAST_SECONDS));
        LockConfigurationExtractor lockConfigurationExtractor = (Runnable task) -> Optional.of(lockConfiguration);
        LockManager lockManager = new DefaultLockManager(lockProvider, lockConfigurationExtractor);
        return new LockableTaskScheduler(taskScheduler, lockManager);
    }
    
    String getSchedulerLockName();
}
