package gov.uk.ets.registry.api.event.service;

import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import java.util.Date;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.auditevent.DomainEvent;
import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class EventService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final DomainEventEntityRepository domainEventRepository;

    /**
     * Publish an event
     *
     * @param domainEvent the event
     */
    public void publishEvent(DomainEvent domainEvent) {
        applicationEventPublisher.publishEvent(domainEvent);
    }

    /**
     * Creates and publishes all types of events.
     *
     * @param identifier  The unique identifier
     * @param urid        The user that initiated the event
     * @param description The comments that the user added
     * @param eventType   The type of the event to be created and published
     */
    public void createAndPublishEvent(String identifier, String urid, String description,
                                      EventType eventType, String action) {
        applicationEventPublisher.publishEvent(
            DomainEvent.builder()
                .who(urid)
                .when(new Date())
                .what(action)
                .domainObject(DomainObject.create(eventType.getClazz(), identifier))
                .description(description)
                .build()
        );
    }

    /**
     * Finds the domain events for the specified identifier for Non Admin users.
     *
     * @param identifier the business identifier of the domain entity
     * @return A list of {@link AuditEventDTO} objects.
     */
    public List<AuditEventDTO> getEventsByDomainIdForNonAdminUsers(String identifier, List<String> domainTypes) {
        return domainEventRepository.getAuditEventsByDomainIdOrderByCreationDateDesc(identifier, false, domainTypes);
    }

    /**
     * Finds the domain events for the specified identifier for Admin users.
     *
     * @param identifier the business identifier of the domain entity
     * @return A list of {@link AuditEventDTO} objects.
     */
    public List<AuditEventDTO> getEventsByDomainIdForAdminUsers(String identifier, List<String> domainTypes) {
        return domainEventRepository.getAuditEventsByDomainIdOrderByCreationDateDesc(identifier, true, domainTypes);
    }

    /**
     * Finds the system domain events for the specified domainId.
     * @param domainId the business identifier of the domain entity
     * @return a list of {@link AuditEventDTO} objects
     */
    public List<AuditEventDTO> getSystemEventsByDomainIdOrderByCreationDateDesc(String domainId, List<String> domainTypes) {
        return domainEventRepository.getSystemEventsByDomainIdOrderByCreationDateDesc(domainId, domainTypes);
    }

    /**
     * Finds the domain events for the specified user id.
     *
     * @param urid the identifier of the user entity
     * @return A list of {@link AuditEventDTO} objects.
     */
    public List<AuditEventDTO> getAuditEventsByCreatorOrderByCreationDateDesc(String urid) {
        return domainEventRepository.getAuditEventsByCreatorOrderByCreationDateDesc(urid);
    }
}
