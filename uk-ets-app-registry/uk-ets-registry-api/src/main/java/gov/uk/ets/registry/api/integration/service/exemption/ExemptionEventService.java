package gov.uk.ets.registry.api.integration.service.exemption;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.integration.changelog.service.ExemptionAuditService;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static gov.uk.ets.registry.api.integration.config.KafkaConstants.CORRELATION_ID_HEADER;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExemptionEventService {

    private final AccountRepository accountRepository;
    private final ExcludeEmissionsRepository excludeEmissionsRepository;
    private final ComplianceService complianceService;
    private final ExemptionEventValidator validator;
    private final ExemptionAuditService auditService;

    @Transactional
    public List<IntegrationEventErrorDetails> process(AccountExemptionUpdateEvent event, Map<String, Object> headers, SourceSystem sourceSystem) {
        log.info("Received event {} with value {} and headers {}",
                OperationEvent.UPDATE_EXEMPTION, event, headers);

        String correlationId = Optional.ofNullable(headers.get(CORRELATION_ID_HEADER))
                .map(bytes -> new String((byte[]) bytes))
                .orElse("N/A");

        List<IntegrationEventErrorDetails> errors = validator.validate(event);
        if (errors.isEmpty()) {
            process(event, sourceSystem);
        }

        if (errors.isEmpty()) {
            log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                    OperationEvent.UPDATE_EXEMPTION, correlationId, sourceSystem, event);
        } else {
            log.warn("Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                    OperationEvent.UPDATE_EXEMPTION, correlationId, sourceSystem, event, errors);
        }

        return errors;
    }

    private void process(AccountExemptionUpdateEvent event, SourceSystem sourceSystem) {

        Long compliantId = event.getRegistryId();
        long year = event.getReportingYear().getValue();
        Boolean isExcluded = event.getExemptionFlag();

        ExcludeEmissionsEntry existingEntry = excludeEmissionsRepository.findByCompliantEntityIdAndYear(compliantId, year);

        Optional<Boolean> existingValue = Optional.ofNullable(existingEntry).map(ExcludeEmissionsEntry::isExcluded);
        if (!Objects.equals(existingValue.orElse(false), isExcluded)) {
            Account account = accountRepository.findByCompliantEntityIdentifier(compliantId)
                    .orElseThrow(() -> new RuntimeException("Could not find account for complaint entity id: " + compliantId));
            complianceService.updateExclusionStatus(account.getIdentifier(), year, isExcluded);
            ExcludeEmissionsEntry updatedEntry = excludeEmissionsRepository.findByCompliantEntityIdAndYear(compliantId, year);
            auditService.logChanges(existingValue.orElse(null), updatedEntry, sourceSystem);
        }
    }

}