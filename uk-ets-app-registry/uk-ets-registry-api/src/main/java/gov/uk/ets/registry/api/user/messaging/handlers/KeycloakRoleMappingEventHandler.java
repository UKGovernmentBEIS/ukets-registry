package gov.uk.ets.registry.api.user.messaging.handlers;

import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.messaging.AdminEventRepresentation;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.messaging.OperationType;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakRoleMappingEventHandler implements KeycloakEventHandler {

    private final KeycloakEventHelper helper;
    private final UserRepository userRepository;
    private final IamUserRoleRepository roleRepository;
    private final EventService eventService;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        boolean canHandleEvent = helper.isClientRoleMappingEvent(event);
        return canHandleEvent;
    }

    /**
     * Updates registry database user role mappings. Creates the role if not found in database.
     */
    @Override
    @Transactional
    public void handle(KeycloakEvent event) {
        log.info("Handling event id: {}", event.getId());
        AdminEventRepresentation representation = helper.deserializeRepresentationValues(event.getRepresentation());
        String userIamIdentifier = helper.extractIamIdentifier(event.getResourcePath());
        // retrieve iam id of role
        String roleIamIdentifier = representation.getId();
        // retrieve user from db by iam id
        User user = userRepository.findByIamIdentifier(userIamIdentifier);
        if (user == null) {
            log.warn("User with iam id: '{}' not found in registry database", userIamIdentifier);
            return;
        }
        // retrieve role from db by iam id (create role if not found)
        IamUserRole iamUserRole = roleRepository.findByIamIdentifier(roleIamIdentifier)
            .orElseGet(() -> roleRepository.save(new IamUserRole(roleIamIdentifier, representation.getName())));

        boolean adminRole = helper.isAdminRoleMapping(representation.getName());

        // if operation type is create: create new role mapping for user/role pair
        if (event.getOperationType() == OperationType.CREATE) {
            log.info("Adding role {} for user: {}", iamUserRole.getRoleName(), user.getUrid());
            user.addUserRole(iamUserRole);
            // Send an audit event in case of an admin role addition
            if (adminRole) {
                eventService.createAndPublishEvent(user.getUrid(), null, iamUserRole.getRoleName(),
                    EventType.USER_ROLE_ADDED, "Add User Role");
            }
        }
        //  if operation type is delete: remove role mapping for user/role pair
        if (event.getOperationType() == OperationType.DELETE) {
            log.info("Removing role {} for user: {}", iamUserRole.getRoleName(), user.getUrid());
            user.removeUserRole(iamUserRole);
            // Send an audit event in case of an admin role deletion
            if (adminRole) {
                eventService.createAndPublishEvent(user.getUrid(), null, iamUserRole.getRoleName(),
                    EventType.USER_ROLE_REMOVED, "Remove User Role");
            }
        }
    }
}
