package gov.uk.ets.registry.api.integration.changelog.service;

import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.integration.changelog.DomainObject;
import gov.uk.ets.registry.api.integration.consumer.SourceSystem;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmissionAuditService {

    private final CommonAuditService commonAuditService;

    public void logChanges(EmissionsEntry oldEntry, EmissionsEntry newEntry, SourceSystem updatedBy) {


        String fullIdentifier = commonAuditService.getAccountByCompliantEntity(newEntry.getCompliantEntityId())
                .getFullIdentifier();

        commonAuditService.handleChanges(DomainObject.EMISSIONS, "emissions",
                buildValue(oldEntry), buildValue(newEntry), newEntry.getId(),
                fullIdentifier, newEntry.getCompliantEntityId(), updatedBy);
    }

    private String buildValue(EmissionsEntry entry) {
        if (entry == null) {
            return "";
        }
        String emissions = Optional.ofNullable(entry.getEmissions()).map(Object::toString).orElse("No Emissions");
        return String.format("Year: %s, Emissions: %s", entry.getYear(), emissions);
    }
}
