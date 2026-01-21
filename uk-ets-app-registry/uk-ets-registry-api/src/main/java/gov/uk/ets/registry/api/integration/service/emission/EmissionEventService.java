package gov.uk.ets.registry.api.integration.service.emission;

import static gov.uk.ets.registry.api.integration.config.KafkaConstants.CORRELATION_ID_HEADER;

import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.messaging.UpdateOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.integration.changelog.service.EmissionAuditService;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.sis.internal.util.StandardDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;

@Log4j2
@Service
@RequiredArgsConstructor
public class EmissionEventService {

    private final EmissionsEntryRepository emissionsEntryRepository;
    private final ComplianceEventService complianceEventService;
    private final AccountRepository accountRepository;
    private final EventService eventService;
    private final EmissionEventValidator validator;
    private final EmissionAuditService emissionAuditService;

    @Transactional
    public List<IntegrationEventError> process(AccountEmissionsUpdateEvent event, Map<String, Object> headers, SourceSystem sourceTopic) {

        log.info("Received event {} with value {} and headers {}",
            OperationEvent.UPDATE_EMISSIONS_VALUE, event, headers);

        String correlationId = Optional.ofNullable(headers.get(CORRELATION_ID_HEADER))
            .map(bytes -> new String((byte[]) bytes))
            .orElse("N/A");

        List<IntegrationEventError> errors = validator.validate(event);
        if (errors.isEmpty()) {
            process(event, sourceTopic);
        }

        if (errors.isEmpty()) {
            log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                OperationEvent.UPDATE_EMISSIONS_VALUE, correlationId, sourceTopic, event);
        } else {
            log.warn("Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                OperationEvent.UPDATE_EMISSIONS_VALUE, correlationId, sourceTopic, event, errors);
        }

        return errors;
    }

    private void process(AccountEmissionsUpdateEvent event, SourceSystem sourceSystem) {

        Long compliantId = event.getRegistryId();
        long year = event.getReportingYear().getValue();
        Long reportableEmissions = event.getReportableEmissions();

        EmissionsEntry newEntry = createEmissionsEntryWithoutFile(compliantId, year, reportableEmissions);

        Optional<EmissionsEntry> existingEntry = emissionsEntryRepository
            .findAllByCompliantEntityIdAndYear(
                compliantId, year)
            .stream()
            .max(Comparator.comparing(EmissionsEntry::getUploadDate));

        boolean alreadyUpToDate = existingEntry.map(EmissionsEntry::getEmissions)
            .filter(currentEmissions -> Objects.equals(reportableEmissions, currentEmissions))
            .isPresent();

        if (alreadyUpToDate) {
            return;
        }

        emissionsEntryRepository.save(newEntry);
        emissionAuditService.logChanges(existingEntry.orElse(null), newEntry, sourceSystem);
        publishUpdateOfVerifiedEmissionsEvent(newEntry);

        Object oldValue = existingEntry.map(EmissionsEntry::getEmissions).map(Object::toString).orElse("No emissions");
        String updatedValue = Objects.nonNull(reportableEmissions) ? reportableEmissions.toString() :  "No emissions";
        String description = "Reporting year: " + year + ", Old value: " + oldValue + ", Updated value: " + updatedValue;
        String what = "Emissions update event";

        eventService.createAndPublishEvent(compliantId.toString(), null, description, EventType.APPROVE_COMPLIANT_ENTITY_EMISSIONS, what);
        accountRepository.findByCompliantEntityIdentifier(compliantId).ifPresent(account ->
            eventService.createAndPublishEvent(account.getIdentifier().toString(), null, description, EventType.APPROVE_ACCOUNT_EMISSIONS, what));

    }

    private EmissionsEntry createEmissionsEntryWithoutFile(Long compliantEntityId, Long year, Long emissions) {
        EmissionsEntry emissionsEntry = new EmissionsEntry();
        emissionsEntry.setCompliantEntityId(compliantEntityId);
        emissionsEntry.setYear(year);
        emissionsEntry.setEmissions(emissions);
        emissionsEntry.setFilename("N/A");
        emissionsEntry.setUploadDate(LocalDateTime.now());
        return emissionsEntry;
    }

    private void publishUpdateOfVerifiedEmissionsEvent(EmissionsEntry entry) {

        LocalDateTime now = LocalDateTime.now(ZoneId.of(StandardDateFormat.UTC));
        UpdateOfVerifiedEmissionsEvent event = UpdateOfVerifiedEmissionsEvent.builder()
            .compliantEntityId(entry.getCompliantEntityId())
            .actorId("system")
            .dateTriggered(now)
            .dateRequested(now)
            .year(entry.getYear().intValue())
            .verifiedEmissions(entry.getEmissions())
            .build();

        complianceEventService.processEvent(event);

    }
}
