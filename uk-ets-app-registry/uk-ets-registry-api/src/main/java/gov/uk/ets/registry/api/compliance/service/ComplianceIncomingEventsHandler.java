package gov.uk.ets.registry.api.compliance.service;


import java.util.Optional;

import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceCalculationErrorEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.incoming.ComplianceResponseEvent;
import gov.uk.ets.registry.api.compliance.messaging.outbox.ComplianceOutbox;
import gov.uk.ets.registry.api.compliance.messaging.outbox.ComplianceOutboxRepository;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplianceIncomingEventsHandler {

    private final AccountRepository accountRepository;
    private final StaticComplianceStatusRepository staticComplianceStatusRepository;
    private final CompliantEntityRepository compliantEntityRepository;
    private final EventService eventService;
    private final ComplianceOutboxRepository complianceOutboxRepository;

    /**
     * Update account with calculated compliance status.
     * TODO: should we emit a domain event?
     */
    public void processDynamicComplianceOutcome(ComplianceResponseEvent event) {
        Account account = accountRepository.findByCompliantEntityIdentifier(event.getCompliantEntityId())
            .orElseThrow(() -> new UkEtsException(
                String.format("Account related to compliant entity id %s not found", event.getCompliantEntityId())
            ));
        ComplianceStatus previousStatus = account.getComplianceStatus();
        account.setComplianceStatus(event.getStatus());
        createAndPublishEvent(event,previousStatus);
        
    }

    /**
     * Inserts/Updates the retrieved static compliance status in the relevant db table.
     * TODO: should we add a dateUpdated column in the table?
     * TODO: should we emit a domain event?
     */
    public void processStaticComplianceOutcome(ComplianceResponseEvent event) {
        // CAUTION: the reporting year is the year before the current!
        long year = event.getDateTriggered().getYear() - 1L;
        staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYear(event.getCompliantEntityId(),
                year)
            .ifPresentOrElse(
                staticComplianceStatus -> staticComplianceStatus.setComplianceStatus(event.getStatus()),
                () -> insertNewStaticComplianceStatus(event, year)
            );
    }

    public void processComplianceCalculationError(ComplianceCalculationErrorEvent event) {

        eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null, event.getMessage(),
                EventType.COMPLIANT_ENTITY_CALCULATION_ERROR, "Compliant Entity Calculation Error");
    }

    private void insertNewStaticComplianceStatus(ComplianceResponseEvent event, long year) {
        CompliantEntity compliantEntity =
            compliantEntityRepository.findByIdentifier(event.getCompliantEntityId())
                .orElseThrow(() -> new IllegalStateException(
                    String.format("Compliant entity not found for identifier: %s",
                        event.getCompliantEntityId())));

        StaticComplianceStatus staticComplianceStatus = new StaticComplianceStatus();
        staticComplianceStatus.setComplianceStatus(event.getStatus());
        staticComplianceStatus.setCompliantEntity(compliantEntity);
        staticComplianceStatus.setYear(year);
        staticComplianceStatusRepository.save(staticComplianceStatus);
    }
    
    /**
     * Publish an event for the compliance history.
     */
    private void createAndPublishEvent(ComplianceResponseEvent event,ComplianceStatus previousStatus) {
        Optional<ComplianceOutbox> originatingEventOptional= complianceOutboxRepository.findByEventId(event.getOriginatingEventId());
        if(originatingEventOptional.isPresent()) {
            ComplianceOutbox originatingEvent = originatingEventOptional.get();
            switch (originatingEvent.getType()) {
              //TODO Add events per case
                case ACCOUNT_CREATION:
                case COMPLIANT_ENTITY_INITIALIZATION:
                case UPDATE_OF_VERIFIED_EMISSIONS:
                case SURRENDER:
                case REVERSAL_OF_SURRENDER:
                case EXCLUSION:
                case REVERSAL_OF_EXCLUSION:
                case UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS:
                case UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS:
                case NEW_YEAR:
                case STATIC_COMPLIANCE_REQUEST:
                case GET_CURRENT_DYNAMIC_STATUS:
                    break;
                case RECALCULATE_DYNAMIC_STATUS:
                    writeEventHistoryOnRecalculationOfStatus(event, previousStatus);
                    break;
                default:
                    throw new IllegalStateException(String.format("The Compliance event is invalid: %s", event));
            }   
        }    
    }
    
    private void writeEventHistoryOnRecalculationOfStatus(ComplianceResponseEvent event,ComplianceStatus previousStatus) {
        if(!event.getStatus().equals(previousStatus)) {
            eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                "Old status: " + previousStatus.toString() + ", Updated value: " + event.getStatus().toString(),
                EventType.COMPLIANT_ENTITY_RECALCULATE_DYNAMIC_STATUS_REQUEST,"Dynamic surrender status recalculation");
            
        }
    }
}
