package gov.uk.ets.registry.api.user.migration;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Profile("!integrationTest")
public class UserAgentCrcMigratorInitiator {

    private final UserAgentCrcMigrator migrator;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final MigratorHistoryRepository migratorHistoryRepository;

    /**
     * Runs every 30 seconds, but ShedLock ensures EXACTLY ONE execution.
     * The migrator only runs when Keycloak is fully ready.
     */
    @Scheduled(fixedDelay = 30000)
    @SchedulerLock(name = "UserAgentCrcMigratorInitiator",
                   lockAtMostFor = "60m",  // max time the lock is held
                   lockAtLeastFor = "1s")  // ensures lock is written even for fast tasks
    public void runWhenKeycloakIsReady() {

        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.USER_AGENT_CRC_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[UserAgentCrcMigrator] has already been performed, skipping.");
            return;
        }

        log.info("UserAgentCrcMigratorScheduled: Checking Keycloak readiness...");

        try {
            // If this succeeds, Keycloak is fully initialized
            serviceAccountAuthorizationService.obtainAccessToken();
        } catch (Exception ex) {
            log.warn("UserAgentCrcMigratorScheduled: Keycloak not ready yet. Will retry...");
            return; // Do NOT run migrator yet
        }

        log.info("UserAgentCrcMigratorInitiator: Keycloak is ready. Running UserAgentCrcMigrator...");
        try {
            migrator.call();
            log.info("UserAgentCrcMigratorInitiator: Migration completed successfully.");
        } catch (Throwable ex) {
            log.error("UserAgentCrcMigratorInitiator: Migration failed.", ex);
        }
    }
}
