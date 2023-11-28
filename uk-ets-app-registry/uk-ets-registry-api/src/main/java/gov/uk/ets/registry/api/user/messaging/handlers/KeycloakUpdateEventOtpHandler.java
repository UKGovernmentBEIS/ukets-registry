package gov.uk.ets.registry.api.user.messaging.handlers;

import static gov.uk.ets.registry.api.user.messaging.KeycloakEventType.UPDATE_TOTP;

import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.OtpSetNotification;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakUpdateEventOtpHandler implements KeycloakEventHandler {

    private final GroupNotificationClient groupNotificationClient;
    private final UserRepository userRepository;
    private final KeycloakEventHelper helper;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return event.getType() != null && event.getType() == UPDATE_TOTP && event.getUserId() != null;
    }

    /**
     * Publishes event and sends notification in case of update otp updated/activated.
     */
    @Transactional
    public void handle(KeycloakEvent event) {
        String usernameField = "username";
        User user = userRepository.findByIamIdentifier(event.getUserId());
        helper.publishKeycloakEvent(user.getUrid(),
            event.getDetails().getOrDefault(usernameField, "username not available"),
            EventType.OTP_UPDATED,
            "New OTP has been activated");
        groupNotificationClient
            .emitGroupNotification(new OtpSetNotification(event.getDetails().get(usernameField)));

    }
}
