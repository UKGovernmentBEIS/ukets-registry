package gov.uk.ets.compliance.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.messaging.ComplianceEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class ComplianceOutboxService {
    private final ComplianceOutboxRepository repository;
    private final ObjectMapper objectMapper;
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
                .originatingEventId(event.getOriginatingEventId())
                .status(ComplianceOutboxStatus.PENDING)
                .type(event.getType())
                .payload(serializePayload(event))
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
            ComplianceOutgoingEventBase event = deserializePayload(entry.getPayload());
            log.info("Sending compliance event {}", event);
            //send event
            producer.send(event);
            // and update status transactionally
            entry.setStatus(ComplianceOutboxStatus.SENT);
        });
    }

    private String serializePayload(ComplianceOutgoingEventBase event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("unexpected error during json serialization", e);
        }
    }

    private ComplianceOutgoingEventBase deserializePayload(String event) {
        try {
            return objectMapper.readValue(event, ComplianceOutgoingEventBase.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("unexpected error during json deserialization", e);
        }
    }
}
