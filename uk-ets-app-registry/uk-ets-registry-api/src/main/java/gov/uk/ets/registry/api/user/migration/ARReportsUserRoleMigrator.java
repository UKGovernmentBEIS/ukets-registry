package gov.uk.ets.registry.api.user.migration;

import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.UserRoleDetails;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class ARReportsUserRoleMigrator implements Migrator {

    private final ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    private final UserRepository userRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Override
    @Transactional
    @Retryable(
            retryFor = RuntimeException.class,
            maxAttempts = 10,
            backoff = @Backoff(
                    delay = 3000,
                    multiplier = 2,
                    maxDelay = 120000
            )
    )
    public void migrate() {

        log.info("Starting migration of reports-user role to Enrolled ARs");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.AR_REPORTS_USER_ROLE_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[AR reports user role Migrator], has already been performed, skipping.");
            return;
        }

        final List<UserRoleDetails> userRoleDetails = userRepository.findUsersByStatusAndRoleExcludingReportsUser(UserStatus.ENROLLED,
                List.of(UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral(),
                        UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                        UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                        UserRole.READONLY_ADMINISTRATOR.getKeycloakLiteral())
        );
        userRoleDetails.forEach(userRoleDetail ->
                reportRequestAddRemoveRoleService.requestReportsApiAddRole(userRoleDetail.getIamIdentifier()));

        updateMigrationHistory();
        log.info("Migration of reports-user role to Enrolled ARs completed");
    }

    @Recover
    public void recover(RuntimeException ex) {
        log.error("Migration of reports-user role failed after 10 retries", ex);
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.AR_REPORTS_USER_ROLE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }
}
