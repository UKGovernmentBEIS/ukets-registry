package gov.uk.ets.registry.api.compliance.messaging;

import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceCalculatedEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceCalculationErrorEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceResponseEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.StaticComplianceRetrievedEvent;
import gov.uk.ets.registry.api.compliance.service.ComplianceIncomingEventsHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@KafkaListener(topics = "${kafka.compliance.outcome.topic:compliance.outcome.topic}",
    containerFactory = "complianceApiListenerContainerFactory")
@RequiredArgsConstructor
@Service
@Log4j2
public class ComplianceEventListener {

    private final ComplianceIncomingEventsHandler handler;

    @KafkaHandler
    @Transactional
    public void processComplianceOutcome(ComplianceResponseEvent event) {
        log.info("Received ComplianceResponseEvent {}", event);
        if (event instanceof ComplianceCalculatedEvent) {
            handler.processDynamicComplianceOutcome(event);
        } else if (event instanceof ComplianceCalculationErrorEvent) {
            handler.processComplianceCalculationError((ComplianceCalculationErrorEvent) event);
        } else if (event instanceof StaticComplianceRetrievedEvent) {
            handler.processStaticComplianceOutcome(event);
        }
    }
}
