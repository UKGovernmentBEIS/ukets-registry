package gov.uk.ets.registry.api.user.messaging;

import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakEventHelper {

    private final Mapper mapper;
    private final EventService eventService;

    public AdminEventRepresentation deserializeRepresentationValues(String representation) {
        AdminEventRepresentation[] representationArray = mapper.convertToPojo(representation,
            AdminEventRepresentation[].class);
        log.debug(representationArray);
        return representationArray[0];
    }

    public UserUpdateEventRepresentation deserializeUserUpdateEventRepresentationValues(String representation) {
        UserUpdateEventRepresentation userRepresentation =
                mapper.convertToPojo(representation, UserUpdateEventRepresentation.class);
        return userRepresentation;
    }

    public String extractIamIdentifier(String resourcePath) {
        return StringUtils.substringBetween(resourcePath, "/", "/") != null ?
            StringUtils.substringBetween(resourcePath, "/", "/") :
            StringUtils.EMPTY;
    }

    public boolean isClientRoleMappingEvent(KeycloakEvent event) {
        return event.getResourceType() != null && event.getResourceType() == ResourceType.CLIENT_ROLE_MAPPING;
    }

    public void publishKeycloakEvent(String id, String description, EventType type, String action) {
        eventService.createAndPublishEvent(id, id, description, type, action);
    }

    public boolean isAdminRoleMapping(String keycloakLiteral) {
        return UserRole.getAdminRoles().stream()
            .map(UserRole::getKeycloakLiteral)
            .anyMatch(literal -> literal.equals(keycloakLiteral));
    }
}
