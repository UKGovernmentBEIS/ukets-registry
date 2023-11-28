package gov.uk.ets.registry.api.user.messaging.handlers;

import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.OperationType;
import gov.uk.ets.registry.api.user.messaging.ResourceType;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakClientRoleEventHandler implements KeycloakEventHandler {

    private final IamUserRoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return event.getResourceType() != null &&
            event.getResourceType() == ResourceType.CLIENT_ROLE &&
            event.getOperationType() == OperationType.DELETE;
    }

    @Override
    @Transactional
    public void handle(KeycloakEvent event) {
        String roleIamIdentifier = StringUtils.substringAfter(event.getResourcePath(), "roles-by-id/");
        roleRepository.findByIamIdentifier(roleIamIdentifier)
            .ifPresentOrElse(
                iamUserRole -> processDeletedRole(roleIamIdentifier, iamUserRole),
                () -> log.warn("Role with id '{}' not present in database, skipping processing", roleIamIdentifier)
            );
    }

    /**
     * Delete all role mappings in registry for the role deleted in keycloak, then delete the actual role.
     */
    private void processDeletedRole(String roleIamIdentifier, IamUserRole deletedRole) {
        List<User> users = userRepository.findAllByUserRoles_Role_IamIdentifier(roleIamIdentifier);
        users.forEach(u -> u.removeUserRole(deletedRole));
        roleRepository.delete(deletedRole);
    }
}
