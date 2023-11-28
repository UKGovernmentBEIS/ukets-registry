package gov.uk.ets.registry.api.user.messaging.handlers;

import static gov.uk.ets.registry.api.user.messaging.KeycloakEventType.CUSTOM_REQUIRED_ACTION;

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
public class KeycloakTermsAndConditionsEventHandler implements KeycloakEventHandler {

    private final KeycloakEventHelper helper;
    private final UserRepository userRepository;

    @Override
    public boolean canHandle(KeycloakEvent event) {
        return event.getType() != null && event.getType() == CUSTOM_REQUIRED_ACTION &&
            isTermsAndConditionsAction(event);
    }

    /**
     * Publishes event in case of t&c accepted event.
     */
    @Transactional
    public void handle(KeycloakEvent event) {
        String usernameField = "username";
        User user = userRepository.findByIamIdentifier(event.getUserId());
        helper.publishKeycloakEvent(user.getUrid(),
            event.getDetails().getOrDefault(usernameField, "username not available"),
            EventType.TERMS_AND_CONDITIONS_ACCEPTED,
            "Terms & Conditions have been accepted");
    }

    private boolean isTermsAndConditionsAction(KeycloakEvent event) {
        return event.getUserId() != null &&
            event.getDetails().get("custom_required_action").equals("terms_and_conditions");
    }
}
