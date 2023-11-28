package gov.uk.ets.registry.api.compliance.messaging.outbox;

import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import java.io.Serializable;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class is intentionally package private, the only entry point to this producer is through
 * the {@link ComplianceEventService}.
 */
@Log4j2
@Service
class ComplianceEventProducer {

    private final String complianceOutcomeTopic;

    private final KafkaTemplate<String, Serializable> complianceApiKafkaTemplate;

    public ComplianceEventProducer(
        @Value("${kafka.compliance.events.in.topic:compliance.events.in.topic}") String complianceOutcomeTopic,
        @Qualifier("complianceApiKafkaTemplate")
            KafkaTemplate<String, Serializable> complianceApiKafkaTemplate) {
        this.complianceOutcomeTopic = complianceOutcomeTopic;
        this.complianceApiKafkaTemplate = complianceApiKafkaTemplate;
    }

    /**
     * Partitions messages by compliantEntityId, so as to preserve the order of messages on the consumer side.
     */
    @Transactional
    public void send(ComplianceOutgoingEventBase event) {
        log.info("Sending a compliance event {}", event);
        try {
            complianceApiKafkaTemplate.send(complianceOutcomeTopic, String.valueOf(event.getCompliantEntityId()), event)
                .get();
        } catch (Exception e) {
            log.error("compliance event failed to be send: {}", event);
            throw new RuntimeException(e);
        }
    }
}
