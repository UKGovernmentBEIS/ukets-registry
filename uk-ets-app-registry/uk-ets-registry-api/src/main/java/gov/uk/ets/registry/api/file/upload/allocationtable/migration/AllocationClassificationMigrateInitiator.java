package gov.uk.ets.registry.api.file.upload.allocationtable.migration;

import gov.uk.ets.registry.api.user.migration.SchedulerHelper;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Profile("!integrationTest")
public class AllocationClassificationMigrateInitiator implements CommandLineRunner {

    private static final String SCHED_LOCK_NAME = "AllocationClassificationMigratorSchedulerLock";

    private final SchedulerHelper helper;
    private final AllocationClassificationMigrator migrator;

    @Override
    public void run(String... args) throws Exception {
        LockableTaskScheduler lockableTaskScheduler = helper.createScheduler(SCHED_LOCK_NAME);
        lockableTaskScheduler.schedule(migrator::migrate, Instant.now());
    }
}
