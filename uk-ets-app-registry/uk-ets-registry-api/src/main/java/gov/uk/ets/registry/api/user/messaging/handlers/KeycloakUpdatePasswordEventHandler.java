package gov.uk.ets.registry.api.user.messaging.handlers;

import static gov.uk.ets.registry.api.user.messaging.KeycloakEventType.UPDATE_PASSWORD;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.profile.domain.PasswordChangeSuccessNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakUpdatePasswordEventHandler implements KeycloakEventHandler {

    private final GroupNotificationClient groupNotificationClient;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return event.getType() != null && event.getType() == UPDATE_PASSWORD && event.getUserId() != null;
    }

    /**
     * For specific user events coming form keycloak, sends email notifications and/or publishes domain events.
     */
    @Transactional
    public void handle(KeycloakEvent event) {
        String usernameField = "username";
        groupNotificationClient
            .emitGroupNotification(
                new PasswordChangeSuccessNotification(event.getDetails().get(usernameField)));
    }
}
