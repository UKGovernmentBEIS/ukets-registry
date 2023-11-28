package gov.uk.ets.registry.api.user.messaging.handlers;

import static gov.uk.ets.registry.api.user.messaging.KeycloakEventType.LOGIN_ERROR;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.LoginErrorNotification;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeycloakLoginErrorEventHandler implements KeycloakEventHandler {

    @Value("${application.url}")
    String applicationUrl;

    private final GroupNotificationClient groupNotificationClient;
    private final UserRepository userRepository;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final KeycloakEventHelper helper;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return event.getType() != null && event.getType() == LOGIN_ERROR && event.getUserId() != null;
    }

    /**
     * Publishes event/notification in case of login error.
     */
    @Transactional
    public void handle(KeycloakEvent event) {
        User user = userRepository.findByIamIdentifier(event.getUserId());
        if (user == null) {
            log.error("Invalid Login Attempt: The user with keycloak" +
                            " identifier {} does not exist in the registry database.",
                    event.getUserId());
            return;
        }
        helper.publishKeycloakEvent(user.getUrid(), event.getError(), EventType.INVALID_LOGIN_ATTEMPT,
            "Invalid login attempt");
        if (!UserStatus.SUSPENDED.equals(user.getState())) {
            UserRepresentation userRepresentation =
                serviceAccountAuthorizationService.getUser(event.getUserId());
            groupNotificationClient
                .emitGroupNotification(LoginErrorNotification.builder()
                        .emailAddress(userRepresentation.getEmail())
                        .url(applicationUrl)
                        .build());
        }
    }
}
