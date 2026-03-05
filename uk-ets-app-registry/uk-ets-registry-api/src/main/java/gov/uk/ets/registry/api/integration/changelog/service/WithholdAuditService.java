package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class WithholdAuditService {

    private final CommonAuditService commonAuditService;

    public void logChanges(Map<Integer, AllocationStatusType> oldValue, List<AllocationStatus> newValue, Account account, SourceSystem updatedBy) {

        newValue.stream()
                .filter(allocationStatus -> oldValue.get(allocationStatus.getAllocationYear().getYear()) != allocationStatus.getStatus())
                .forEach(allocationStatus -> {
                    Integer year = allocationStatus.getAllocationYear().getYear();
                    commonAuditService.handleChanges(DomainObject.WITHHOLD, "withhold",
                            buildValue(year, oldValue.get(year)), buildValue(year, allocationStatus.getStatus()), allocationStatus.getId(),
                            account.getFullIdentifier(), account.getCompliantEntity().getIdentifier(), updatedBy);
                });

    }

    private String buildValue(Integer year, AllocationStatusType statusType) {
        if (statusType == null) {
            return "";
        }
        return String.format("Year: %s, Withhold_Flag: %s", year, statusType);
    }
}
