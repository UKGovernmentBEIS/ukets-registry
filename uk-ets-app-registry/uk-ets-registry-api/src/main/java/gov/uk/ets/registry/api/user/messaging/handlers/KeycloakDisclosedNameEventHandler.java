package gov.uk.ets.registry.api.user.messaging.handlers;

import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.messaging.AdminEventRepresentation;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.service.UserService;
import java.security.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakDisclosedNameEventHandler implements KeycloakEventHandler {

    private final KeycloakEventHelper helper;
    private final UserService userService;


    @Override
    public boolean canHandle(KeycloakEvent event) {
        return helper.isClientRoleMappingEvent(event);
    }

    /**
     * Handle the operation types that coming from the Keycloak admin events.
     * Such operations happen when adding/removing a new client role to a user,
     * e.g. CREATE for addition, DELETE for removal.
     *
     * @param event the Keycloak event.
     */
    @Override
    public void handle(KeycloakEvent event) {
        String iamIdentifier = helper.extractIamIdentifier(event.getResourcePath());
        AdminEventRepresentation representation = helper.deserializeRepresentationValues(event.getRepresentation());

        switch (event.getOperationType()) {
            case CREATE:
                UserRole userRole;
                try {
                    userRole = UserRole.fromKeycloakLiteral(representation.getName());
                } catch (InvalidParameterException e) {
                    log.warn("Role name {} is not mapped to any UserRole value", representation.getName());
                    break;
                }
                if (userRole != null && userRole.isRegistryAdministrator()) {
                    userService.updateUserDisclosedName(iamIdentifier, true);
                }
                break;
            case DELETE:
                try {
                    userRole = UserRole.fromKeycloakLiteral(representation.getName());
                } catch (InvalidParameterException e) {
                    log.warn("Role name {} is not mapped to any UserRole value", representation.getName());
                    break;
                }
                if (userRole.isRegistryAdministrator()) {
                    userService.updateUserDisclosedName(iamIdentifier, false);
                }
                break;
            default:
                break;
        }
    }
}
