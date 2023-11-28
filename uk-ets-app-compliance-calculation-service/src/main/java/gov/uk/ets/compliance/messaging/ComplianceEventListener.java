package gov.uk.ets.compliance.messaging;

import gov.uk.ets.compliance.domain.ComplianceState;
import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.domain.DynamicCompliance;
import gov.uk.ets.compliance.domain.events.base.ComplianceEvent;
import gov.uk.ets.compliance.domain.events.base.DynamicComplianceEvent;
import gov.uk.ets.compliance.domain.events.base.StaticComplianceRequestEvent;
import gov.uk.ets.compliance.outbox.ComplianceOutboxService;
import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventBase;
import gov.uk.ets.compliance.service.DynamicComplianceException;
import gov.uk.ets.compliance.service.DynamicComplianceService;
import gov.uk.ets.compliance.service.StaticComplianceService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@KafkaListener(topics = "${kafka.compliance.events.in.topic:compliance.events.in.topic}",
        containerFactory = "complianceListenerContainerFactory")
@RequiredArgsConstructor
@Service
@Log4j2
public class ComplianceEventListener {

    private final DynamicComplianceService dynamicComplianceService;

    private final StaticComplianceService staticComplianceService;
    
    private final ComplianceOutboxService outboxService;

    /**
     * Receives a compliance event and proceeds with its processing.
     *
     * @param event the incoming compliance event
     */
    @KafkaHandler
    @Transactional
    public void processComplianceOutcome(ComplianceEvent event) {
        log.info("received compliance event: {}", event);

        try {
            if (!event.isValidCompliantEvent()) {
                throw new DynamicComplianceException("Incoming compliance event fields validation error");
            }

            ComplianceOutgoingEventBase outgoingEvent;

            if (event instanceof StaticComplianceRequestEvent) {
                outgoingEvent = processStaticComplianceEvent((StaticComplianceRequestEvent) event);
            } else {
                outgoingEvent = processDynamicComplianceEvent((DynamicComplianceEvent) event);
            }

            if (outgoingEvent.isValidOutgoingEvent()) {
                log.info("creating compliance outbox event: {}", outgoingEvent);
                outboxService.create(outgoingEvent);
            } else {
                throw new DynamicComplianceException("Outgoing compliance event fields validation error");
            }

        } catch (Exception e) {
            createComplianceCalculationErrorEvent(event, e.getMessage());
        }
    }

    private StaticComplianceRetrievedEvent processStaticComplianceEvent(StaticComplianceRequestEvent event) {
        ComplianceState complianceState = staticComplianceService.processEvent(event);
        return createStaticComplianceRetrievedEvent(event, complianceState);
    }

    private ComplianceCalculatedEvent processDynamicComplianceEvent(DynamicComplianceEvent event) {
        DynamicCompliance dynamicCompliance = dynamicComplianceService.processEvent(event);
        return createComplianceCalculatedEvent(event, dynamicCompliance);
    }

    private StaticComplianceRetrievedEvent createStaticComplianceRetrievedEvent(ComplianceEvent event, ComplianceState complianceState) {
        return StaticComplianceRetrievedEvent.builder()
            .originatingEventId(event.getEventId())
            .compliantEntityId(event.getCompliantEntityId())
            .dateRequested(event.getDateRequested())
            .status(complianceState.getStatus())
            .dateTriggered(LocalDateTime.now())
            .build();
    }

    private ComplianceCalculatedEvent createComplianceCalculatedEvent(ComplianceEvent event,  DynamicCompliance dynamicCompliance) {
       return ComplianceCalculatedEvent.builder()
            .originatingEventId(event.getEventId())
            .compliantEntityId(event.getCompliantEntityId())
            .status(dynamicCompliance.getState().getDynamicStatus())
            .dateTriggered(LocalDateTime.now())
            .build();
    }

    private void createComplianceCalculationErrorEvent(ComplianceEvent event, String msg) {
        ComplianceCalculationErrorEvent complianceCalculationErrorEvent = ComplianceCalculationErrorEvent.builder()
            .originatingEventId(event.getEventId())
            .compliantEntityId(event.getCompliantEntityId())
            .status(ComplianceStatus.ERROR)
            .dateTriggered(LocalDateTime.now())
            .message(msg)
            .build();
        log.error("creating compliance calculation error outbox event: {}", complianceCalculationErrorEvent);
        outboxService.create(complianceCalculationErrorEvent);
    }
}
