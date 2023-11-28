package gov.uk.ets.compliance.messaging;

import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventBase;
import gov.uk.ets.compliance.service.DynamicComplianceException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;


@Log4j2
@Service
public class ComplianceEventProducer {

    private final String complianceOutcomeTopic;

    private final KafkaTemplate<String, Serializable> complianceKafkaTemplate;

    public ComplianceEventProducer(
            @Value("${kafka.compliance.outcome.topic:compliance.outcome.topic}") String complianceOutcomeTopic,
            @Qualifier("complianceKafkaTemplate") KafkaTemplate<String, Serializable> complianceKafkaTemplate) {
        this.complianceOutcomeTopic = complianceOutcomeTopic;
        this.complianceKafkaTemplate = complianceKafkaTemplate;
    }

    /**
     * Partitions messages by compliantEntityId, so as to preserve the order of messages on the consumer side.
     */
    @Transactional
    public void send(ComplianceOutgoingEventBase event) {
        log.info("Sending a compliance calculated event {}", event);
        try {
            complianceKafkaTemplate.send(complianceOutcomeTopic, String.valueOf(event.getCompliantEntityId()), event)
                    .get();
        } catch (Exception e) {
            log.error("ComplianceOutgoingEventBase message failed to be send: {}", event);
            throw new DynamicComplianceException(e);
        }
    }
}
