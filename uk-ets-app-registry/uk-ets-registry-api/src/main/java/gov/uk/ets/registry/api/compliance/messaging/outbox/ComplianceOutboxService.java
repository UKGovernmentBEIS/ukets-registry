package gov.uk.ets.registry.api.compliance.messaging.outbox;

import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ComplianceOutboxService {
    private final ComplianceOutboxRepository repository;
    private final Mapper mapper;
    private final ComplianceEventProducer producer;

    /**
     * Creates an outbox entry with status PENDING.
     * The payload contains the event data that will be sent from the system.
     */
    @Transactional
    public void create(ComplianceOutgoingEventBase event) {
        ComplianceOutbox outboxEvent = ComplianceOutbox.builder()
            .generatedOn(event.getDateTriggered())
            .compliantEntityId(event.getCompliantEntityId())
            .eventId(event.getEventId())
            .status(ComplianceOutboxStatus.PENDING)
            .type(event.getType())
            .payload(mapper.convertToJson(event))
            .build();

        repository.save(outboxEvent);
    }

    /**
     * Retrieves all outbox entries with status PENDING.
     * Sends the message.
     * Updates tha outbox entry to status SENT.
     */
    @Transactional
    public void processEvents() {
        List<ComplianceOutbox> outboxEntries =
            repository.findByStatusOrderByGeneratedOnAsc(ComplianceOutboxStatus.PENDING);

        outboxEntries.forEach(entry -> {
            ComplianceOutgoingEventBase event = mapper.convertToPojo(entry.getPayload(),
                ComplianceOutgoingEventBase.class);
            log.info("Sending compliance event {}", event);
            //send event
            producer.send(event);
            // and update status transactionally
            entry.setStatus(ComplianceOutboxStatus.SENT);
        });
    }
}
