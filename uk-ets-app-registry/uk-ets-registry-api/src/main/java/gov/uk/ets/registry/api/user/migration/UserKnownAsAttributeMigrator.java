package gov.uk.ets.registry.api.user.migration;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import javax.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserKnownAsAttributeMigrator implements Migrator {

    private final UserRepository userRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    public void migrate() {
        log.info("Starting migration of known_as attribute");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.USER_KNOWN_AS_ATTRIBUTE_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[User Known As Attribute Migrator], has already been performed, hence, it is skipping it.");
            return;
        }
        List<User> migratedUsers = userRepository.findByKnownAsNotNull();
        if (migratedUsers == null) {
            log.debug("MigratedUsers List<User> Collection has not data! Migration canceled.");
            return;
        }
        // for each registry user retrieve the respective user representation from keycloak,
        // and insert the known_as attribute (if it exists) in the DB
        userRepository.findAll()
            .forEach(user -> {
                log.debug("----------- REGISTRY USER: {}", user.toString());
                try {
                    UserRepresentation keycloakUser =
                        serviceAccountAuthorizationService.getUser(user.getIamIdentifier());
                    log.debug("----------- KEYCLOAK USER: {}", keycloakUser.getId());
                    if (keycloakUser.getAttributes() != null 
                            && keycloakUser.getAttributes().get("alsoKnownAs") != null
                            && !keycloakUser.getAttributes().get("alsoKnownAs").isEmpty()) {
                        user.setKnownAs(keycloakUser.getAttributes().get("alsoKnownAs").get(0));
                        if (user.getDisclosedName() == null || user.getDisclosedName() != null 
                                && !user.getDisclosedName().equals("Registry Administrator")
                                && !StringUtils.isEmpty(user.getKnownAs())) {
                            user.setDisclosedName(user.getKnownAs());
                        }
                        userRepository.save(user);
                        log.debug("Inserted known_as attribute value for user: {}", user.getUrid());
                    }
                } catch (NotFoundException e) {
                    log.warn("User with id: {} not found in keycloak, skipping migration ",
                        user.getIamIdentifier());
                }
            });

        log.info("Migration of known_as attribute completed");
    }

}
