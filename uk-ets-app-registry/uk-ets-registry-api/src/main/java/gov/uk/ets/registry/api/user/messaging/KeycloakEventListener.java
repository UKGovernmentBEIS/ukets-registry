package gov.uk.ets.registry.api.user.messaging;

import gov.uk.ets.registry.api.user.messaging.handlers.KeycloakEventHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@KafkaListener(
    containerFactory = "keycloakEventConsumerFactory",
    topics = "keycloak.events.in")
@RequiredArgsConstructor
@Log4j2
public class KeycloakEventListener {

    private final List<KeycloakEventHandler> eventHandlers;

    /**
     * Each Handler's handle is called in turn (if it can handle the event).
     */
    @KafkaHandler
    @Transactional
    public void processKeycloakEvent(KeycloakEvent event) {
        log.info("Received Keycloak event id: {}", event.getId());
        eventHandlers.stream()
            .filter(h -> {
                try {
                    return h.canHandle(event);
                } catch (Exception e) {
                    log.error("Error processing keycloak event canHandle stacktrace :", e);
                    return false;
                }
            })
            .forEach(h -> {
                try {
                    h.handle(event);
                } catch (Exception e) {
                    log.error("Error processing keycloak event stacktrace :", e);
                }

            });
    }
}
