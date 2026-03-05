package gov.uk.ets.registry.api.integration.service.withhold;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.service.dto.UpdateAllocationCommand;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.integration.changelog.service.WithholdAuditService;
import gov.uk.ets.registry.api.integration.consumer.OperationEvent;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.uk.ets.registry.api.integration.config.KafkaConstants.CORRELATION_ID_HEADER;

@Log4j2
@Service
@RequiredArgsConstructor
public class WithholdEventService {

    private final AccountRepository accountRepository;
    private final AccountAllocationService accountAllocationService;
    private final AllocationStatusRepository allocationStatusRepository;
    private final WithholdEventValidator validator;
    private final WithholdAuditService auditService;

    @Transactional
    public List<IntegrationEventErrorDetails> process(AccountWithholdUpdateEvent event, Map<String, Object> headers, SourceSystem sourceSystem) {
        log.info("Received event {} with value {} and headers {}",
                OperationEvent.UPDATE_WITHHOLD, event, headers);

        String correlationId = Optional.ofNullable(headers.get(CORRELATION_ID_HEADER))
                .map(bytes -> new String((byte[]) bytes))
                .orElse("N/A");

        List<IntegrationEventErrorDetails> errors = validator.validate(event);
        if (errors.isEmpty()) {
            process(event, sourceSystem);
        }

        if (errors.isEmpty()) {
            log.info("Event {} with correlationId: {} from {} and value {} was processed successfully.",
                    OperationEvent.UPDATE_WITHHOLD, correlationId, sourceSystem, event);
        } else {
            log.warn("Event {} with correlationId: {} from {} and value {} has to following errors {} and was not processed successfully.",
                    OperationEvent.UPDATE_WITHHOLD, correlationId, sourceSystem, event, errors);
        }

        return errors;
    }

    private void process(AccountWithholdUpdateEvent event, SourceSystem sourceSystem) {

        Long compliantId = event.getRegistryId();
        long reportingYear = event.getReportingYear().getValue();
        AllocationStatusType newAllocationStatusType = event.getWithholdFlag() ? AllocationStatusType.WITHHELD : AllocationStatusType.ALLOWED;

        Account account = accountRepository.findByCompliantEntityIdentifier(compliantId)
                .orElseThrow(() -> new RuntimeException("Could not find account for complaint entity id: " + compliantId));

        Map<Integer, AllocationStatusType> currentStatuses = accountAllocationService.getAccountAllocationStatus(account.getIdentifier());

        Map<Integer, AllocationStatusType> toBeUpdatedStatuses = currentStatuses.entrySet()
                .stream()
                .filter((entry) -> entry.getKey() >= reportingYear)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> newAllocationStatusType));

        boolean noUpdate = toBeUpdatedStatuses.entrySet()
                .stream()
                .allMatch(entry -> currentStatuses.get(entry.getKey()) == newAllocationStatusType);

        if (noUpdate) {
            return;
        }

        accountAllocationService.updateAllocationStatus(UpdateAllocationCommand.builder()
                .accountId(account.getIdentifier())
                .changedStatus(toBeUpdatedStatuses)
                .justification("Update from integration point")
                .build(), false);

        List<AllocationStatus> allocationStatuses = allocationStatusRepository.findByCompliantEntityId(account.getCompliantEntity().getId());

        auditService.logChanges(currentStatuses, allocationStatuses, account, sourceSystem);
    }
}
