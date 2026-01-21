package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ExemptionAuditService {

    private final CommonAuditService commonAuditService;

    public void logChanges(Boolean oldValue, ExcludeEmissionsEntry newEntry, SourceSystem updatedBy) {


        String fullIdentifier = commonAuditService.getAccountByCompliantEntity(newEntry.getCompliantEntityId())
                .getFullIdentifier();

        commonAuditService.handleChanges(DomainObject.EXEMPTION, "exemption",
                buildValue(newEntry.getYear(), oldValue), buildValue(newEntry.getYear(), newEntry.isExcluded()), newEntry.getId(),
                fullIdentifier, newEntry.getCompliantEntityId(), updatedBy);
    }

    private String buildValue(Long year, Boolean exemptionFlag) {
        if (exemptionFlag == null) {
            return "";
        }
        return String.format("Year: %s, Excluded_Flag: %s", year, exemptionFlag);
    }
}
