package gov.uk.ets.registry.api.compliance.messaging;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.messaging.compliance.UpdateFirstYearOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.account.messaging.compliance.UpdateLastYearOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.compliance.domain.StaticComplianceStatus;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ExclusionEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ExclusionReversalEvent;
import gov.uk.ets.registry.api.compliance.messaging.outbox.ComplianceOutboxService;
import gov.uk.ets.registry.api.compliance.repository.StaticComplianceStatusRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderEvent;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderReversalEvent;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ComplianceEventService {

    public static final String OPERATOR_DETAILS_UPDATE_TEXT = "Operator Details Update";

    private final ComplianceOutboxService service;
    private final EventService eventService;
    private final StaticComplianceStatusRepository staticComplianceStatusRepository;


    /**
     * Processing the events consists of persisting it to the outbox table.
     */
    public void processEvent(ComplianceOutgoingEventBase event) {
        if (event.isValid()) {
            service.create(event);
            createAndPublishEvent(event);
        } else {
            throw new IllegalStateException(String.format("The Compliance event is invalid: %s", event));
        }
    }

    /**
     * <p>Checks if the LYVE has passed for the specified compliance entity.</p>
     * <p>If it has, checks if the previous static compliance status as stored in the database is of type A or EXCLUDED.</p>
     * In that case, the static compliance status should not be updated anymore.
     */
    public boolean skipStaticComplianceStatusRequestForEntity(CompliantEntity compliantEntity,
                                                              int currentYear) {
        // UKETS-5960: end year can be null, in that case we don't want to skip.
        // the need for null check here is the unboxing that occurs when calling getEndYear method afterwards...
        if (compliantEntity.getEndYear() == null) {
            return false;
        }
        int lastYearOfVerifiedEmissions = compliantEntity.getEndYear();

        // to explain the condition bellow here is an example:
        // lastYearOfVerifiedEmissions: 2023 - snapshot date 30/4/2024 -> this means that currentYear is 2024,
        // but in 2024 we will be taking a snapshot for the year 2023 (reporting year is 2023).
        // this is why we consider that lastYearOfVerifiedEmissionsHasPassed one year after the lastYearOfVerifiedEmissions (lastYearOfVerifiedEmissions + 1).
        boolean lastYearOfVerifiedEmissionsHasPassed = currentYear > lastYearOfVerifiedEmissions + 1;

        if (lastYearOfVerifiedEmissionsHasPassed) {
            List<StaticComplianceStatus> previousStaticComplianceStatuses =
                staticComplianceStatusRepository.findByCompliantEntityIdentifierAndYearGreaterThanEqual(
                    compliantEntity.getIdentifier(),
                    (long) lastYearOfVerifiedEmissions);

            return previousStaticComplianceStatuses
                .stream()
                .max(Comparator.comparing(StaticComplianceStatus::getYear))
                .map(StaticComplianceStatus::getComplianceStatus)
                .map(complianceStatus -> complianceStatus == ComplianceStatus.A ||
                    complianceStatus == ComplianceStatus.EXCLUDED)
                .orElse(false);
        }
        
        //See JIRA UKETS-5989
        int firstYearOfVerifiedEmissions = compliantEntity.getStartYear();
        
        return currentYear < firstYearOfVerifiedEmissions + 1;
    }

    /**
     * Publish a second event for the compliance history.
     */
    private void createAndPublishEvent(ComplianceOutgoingEventBase event) {

        switch (event.getType()) {
            case ACCOUNT_CREATION: //Nothing to do
            case COMPLIANT_ENTITY_INITIALIZATION: //Nothing to do
            case UPDATE_OF_VERIFIED_EMISSIONS: //Check if we need an event in case the task is rejected.
                break;
            case SURRENDER:
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    "Allowances surrendered for the account: " + ((SurrenderEvent) event).getAmount(),
                    EventType.COMPLIANT_ENTITY_SURRENDER_COMPLETED, "Transaction completed.");
                break;
            case REVERSAL_OF_SURRENDER:
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    "Reversal of surrender of allowances for the account: " +
                        ((SurrenderReversalEvent) event).getAmount(),
                    EventType.COMPLIANT_ENTITY_SURRENDER_COMPLETED, "Transaction completed.");
                break;
            case EXCLUSION:
                ExclusionEvent exclusionEvent = (ExclusionEvent) event;
                String existingEmissions = Optional.ofNullable(exclusionEvent.getEmissions())
                    .map(emissions -> String.format("Reported Emissions: %s", emissions))
                    .orElse(null);
                String exclusionDescription = Stream.of(String.format("Excluded: %s", exclusionEvent.getYear()),
                    existingEmissions,
                    String.format("Reason: %s", exclusionEvent.getReason()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(System.lineSeparator()));
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    exclusionDescription,
                    EventType.COMPLIANT_ENTITY_EXCLUSION_STATUS_UPDATED, "Exclusion status updated");
                break;
            case REVERSAL_OF_EXCLUSION:
                ExclusionReversalEvent exclusionReversalEvent = (ExclusionReversalEvent) event;
                String reversalOfExclusionDescription =
                    String.format("Exclusion reversed: %s", exclusionReversalEvent.getYear()) +
                    System.lineSeparator() +
                    String.format("Reason: %s", exclusionReversalEvent.getReason());
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    reversalOfExclusionDescription,
                    EventType.COMPLIANT_ENTITY_EXCLUSION_STATUS_UPDATED, "Exclusion status updated");
                break;
            case UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS:
                String firstYearDescription = String.format("First Year of Verified Emissions: %s",
                    ((UpdateFirstYearOfVerifiedEmissionsEvent) event).getFirstYearOfVerifiedEmissions());
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    firstYearDescription,
                    EventType.COMPLIANT_ENTITY_EMISSIONS_FIRST_YEAR_UPDATED, OPERATOR_DETAILS_UPDATE_TEXT);
                break;
            case UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS:
                String lastYearDescription = String.format("Last Year of Verified Emissions: %s",
                    ((UpdateLastYearOfVerifiedEmissionsEvent) event).getLastYearOfVerifiedEmissions());
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    lastYearDescription,
                    EventType.COMPLIANT_ENTITY_EMISSIONS_LAST_YEAR_UPDATED, OPERATOR_DETAILS_UPDATE_TEXT);
                break;
            case NEW_YEAR:
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    "New Year",
                    EventType.COMPLIANT_ENTITY_NEW_YEAR, OPERATOR_DETAILS_UPDATE_TEXT);
                break;
            case STATIC_COMPLIANCE_REQUEST:
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    "Static compliance request",
                    EventType.COMPLIANT_ENTITY_STATIC_COMPLIANCE_REQUEST, OPERATOR_DETAILS_UPDATE_TEXT);
                break;
            case GET_CURRENT_DYNAMIC_STATUS:
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    "Get current dynamic compliance status",
                    EventType.COMPLIANT_ENTITY_GET_CURRENT_DYNAMIC_STATUS_REQUEST, "Get current compliance status");
                break;
            case RECALCULATE_DYNAMIC_STATUS:
                eventService.createAndPublishEvent(event.getCompliantEntityId().toString(), null,
                    null,
                    EventType.COMPLIANT_ENTITY_RECALCULATE_DYNAMIC_STATUS_REQUEST, "Dynamic surrender status recalculation request submitted");
                break;
            default:
                throw new IllegalStateException(String.format("The Compliance event is invalid: %s", event));
        }
    }
}
