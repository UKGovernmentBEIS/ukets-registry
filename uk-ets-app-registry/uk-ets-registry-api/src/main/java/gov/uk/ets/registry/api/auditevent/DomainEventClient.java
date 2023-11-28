package gov.uk.ets.registry.api.auditevent;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DomainEventClient {

    @Value("${kafka.domain.event.topic}")
    private String domainEventTopic;

    private KafkaTemplate<String, DomainEvent<?>> auditCommandProducerTemplate;

    public DomainEventClient(KafkaTemplate<String, DomainEvent<?>> auditCommandProducerTemplate) {
        this.auditCommandProducerTemplate = auditCommandProducerTemplate;
    }

    public void emitDomainEvent(DomainEvent<?> domainEvent) {
        auditCommandProducerTemplate.send(domainEventTopic, domainEvent);
    }
}
