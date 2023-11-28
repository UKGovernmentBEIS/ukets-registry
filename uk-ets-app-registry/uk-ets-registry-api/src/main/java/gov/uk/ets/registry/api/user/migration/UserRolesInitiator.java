package gov.uk.ets.registry.api.user.migration;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.LockableTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * This code runs on startup, but we also wanted a way to avoid running on every instance of the application.
 * That is why a scheduled job is created (manually) so we can use sched-lock functionality.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Profile("!integrationTest")
public class UserRolesInitiator implements CommandLineRunner {

    private static final String SCHED_LOCK_NAME = "UserRolesMigratorSchedulerLock";
  
    private final UserRolesMigrator migrator;
    
    @Autowired
    private SchedulerHelper helper;

    @Override
    public void run(String... args) throws Exception {
        LockableTaskScheduler lockableTaskScheduler = helper.createScheduler(SCHED_LOCK_NAME);
        lockableTaskScheduler.schedule(migrator::migrate, Instant.now());
    }
}
