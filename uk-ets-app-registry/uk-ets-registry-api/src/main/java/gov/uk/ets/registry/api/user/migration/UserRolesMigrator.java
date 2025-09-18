package gov.uk.ets.registry.api.user.migration;

import static java.util.stream.Collectors.toList;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserRolesMigrator implements Migrator {

    private final UserAdministrationService userAdministrationService;
    private final IamUserRoleRepository iamUserRoleRepository;
    private final UserRepository userRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    public void migrate() {
        migrate(false);
    }

    @Transactional
    public void migrate(boolean isForced) {
        log.info("Starting migration of keycloak roles");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.USER_ROLES_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[User Roles Migrator], has already been performed, skipping.");
            return;
        }
        log.info("Migration -> isForced: {}", isForced);
        // retrieve all roles form keycloak for each role insert in db (if not found)
        List<IamUserRole> allRegistryRoles = userAdministrationService.getClientRoles()
            .stream()
            .map(this::fetchOrInsertRole)
            .collect(toList());
        userRepository.findAll()
            .forEach(user -> {
                log.debug("----------- REGISTRY USER: {}", user.toString());
                try {
                    // this is needed for the case of registry user deleted from keycloak
                    UserRepresentation keycloakUser =
                        serviceAccountAuthorizationService.getUser(user.getIamIdentifier());

                    log.debug("----------- KEYCLOAK USER: {}", keycloakUser.getId());
                    // retrieve all user roles and add them to the registry
                    userAdministrationService.getUserClientRoles(user.getIamIdentifier())
                        .forEach(keycloakRole -> {
                                IamUserRole registryRole = findRegistryRole(allRegistryRoles, keycloakRole.getId());
                                user.addUserRole(registryRole);
                            }
                        );
                } catch (NotFoundException e) {
                    log.warn("User with id: {} not found in keycloak, skipping role migration ",
                        user.getIamIdentifier());
                }
            });

        log.info("Migration of keycloak roles completed");
    }

    private IamUserRole fetchOrInsertRole(RoleRepresentation r) {
        return iamUserRoleRepository
            .findByIamIdentifier(r.getId())
            .orElseGet(() -> iamUserRoleRepository.save(new IamUserRole(r.getId(), r.getName())));
    }

    private IamUserRole findRegistryRole(List<IamUserRole> allRegistryRoles, String keycloakRoleId) {
        return allRegistryRoles.stream()
            .filter(iamRole -> iamRole.getIamIdentifier().equals(keycloakRoleId))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Role not found"));
    }
}
