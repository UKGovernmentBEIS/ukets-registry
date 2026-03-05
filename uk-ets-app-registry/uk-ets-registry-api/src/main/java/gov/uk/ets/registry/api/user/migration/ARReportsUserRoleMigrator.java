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

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.AR_REPORTS_USER_ROLE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }
}
