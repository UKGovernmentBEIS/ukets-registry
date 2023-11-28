package gov.uk.ets.registry.api.user.migration;


import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import javax.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Log4j2
public class UserEmailMigrator implements Migrator {
    private final UserRepository userRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Transactional
    public void migrate() {
        log.info("Starting migration of keycloak db users email to registry");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.USER_EMAIL_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[User Email Migrator], has already been performed, skipping.");
            return;
        }
        userRepository.findAll()
                .forEach(user -> {
                    try {
                        UserRepresentation keycloakUser =
                                serviceAccountAuthorizationService.getUser(user.getIamIdentifier());
                        log.debug("----------- KEYCLOAK USER: {}", keycloakUser.getId());
                        user.setEmail(keycloakUser.getEmail());
                        userRepository.save(user);
                    } catch (NotFoundException e) {
                        log.warn("User with id: {} not found in keycloak, skipping migration ",
                                user.getIamIdentifier());
                    }
                });
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.USER_EMAIL_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of keycloak db users email to registry completed");
    }
}
