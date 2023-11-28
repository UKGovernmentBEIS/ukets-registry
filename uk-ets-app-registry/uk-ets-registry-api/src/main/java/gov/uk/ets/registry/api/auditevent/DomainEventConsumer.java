package gov.uk.ets.registry.api.auditevent;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.auditevent.domain.DomainEventEntity;
import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@KafkaListener(
    containerFactory = "domainEventConsumerFactory",
    topics = "domain.event.topic",
    groupId = "domain.event.consumer.group"
)
@RequiredArgsConstructor
public class DomainEventConsumer {

    private final DomainEventEntityRepository domainEventRepository;

    @KafkaHandler
    public void processDomainEvent(DomainEvent<DomainObject> domainEvent) {
        log.info("Audit event came: {}", domainEvent);
        domainEventRepository.save(DomainEventEntity.builder()
            .creationDate(domainEvent.getWhen())
            .creator(domainEvent.getWho())
            .creatorType(domainEvent.getWho() != null ? "user" : "system")
            .description(domainEvent.getDescription())
            .domainId(domainEvent.getDomainObject().domainId())
            .domainType(domainEvent.getDomainObject().domainType())
            .domainAction(domainEvent.getWhat())
            .build()
        );
    }
}
