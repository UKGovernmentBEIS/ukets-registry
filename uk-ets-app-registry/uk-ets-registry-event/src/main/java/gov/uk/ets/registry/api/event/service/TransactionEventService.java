package gov.uk.ets.registry.api.event.service;

import gov.uk.ets.registry.api.auditevent.DomainEvent;
import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.auditevent.domain.types.TransactionEventType;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TransactionEventService {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Creates and publishes all types of events.
     *
     * @param identifier  The unique identifier
     * @param urid        The user that initiated the event
     * @param description The comments that the user added
     * @param domainObject The type of the event to be created and published
     */
    public void createAndPublishEvent(String identifier, String urid, String description,
                                      Object domainObject, TransactionEventType eventType) {
        applicationEventPublisher.publishEvent(
            DomainEvent.builder()
                .who(urid)
                .when(new Date())
                .what(eventType.getEventAction())
                .domainObject(DomainObject.create(domainObject.getClass(), identifier))
                .description(description)
                .build()
        );
    }
}
