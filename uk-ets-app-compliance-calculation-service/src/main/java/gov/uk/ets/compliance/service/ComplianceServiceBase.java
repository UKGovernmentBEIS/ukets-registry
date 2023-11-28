package gov.uk.ets.compliance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.compliance.domain.DynamicCompliance;
import gov.uk.ets.compliance.domain.DynamicComplianceEntity;
import gov.uk.ets.compliance.repository.DynamicComplianceRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * Contains common functionality between dynamic and static compliance services.
 */
@RequiredArgsConstructor
public class ComplianceServiceBase {

    final DynamicComplianceRepository dynamicComplianceRepository;
    private final ObjectMapper objectMapper;

    DynamicComplianceEntity findDynamicComplianceEntityByIdOrElseThrow(Long compliantEntityId) {
        return dynamicComplianceRepository.findById(compliantEntityId)
                .orElseThrow(() -> new DynamicComplianceException(String
                    .format("Dynamic Compliance not found for compliant entity id: %s", compliantEntityId)));
    }

    void checkForDuplicateEvent(UUID eventId, DynamicCompliance dynamicCompliance) {
        dynamicCompliance.getEventHistory().getArchive().stream()
            .filter(e -> e.getEventId().equals(eventId))
            .findFirst()
            .ifPresent(e -> {throw new DynamicComplianceException("Event has already been processed");});
    }

    DynamicCompliance deserializeToDynamicCompliance(String dynamicCompliance) {
        try {
            return objectMapper.readValue(dynamicCompliance, DynamicCompliance.class);
        } catch (IOException e) {
            throw new DynamicComplianceException("Null or not Serializable " +
                    DynamicCompliance.class.getSimpleName() +
                    "object", e);
        }
    }

    String serializeDynamicCompliance(DynamicCompliance dynamicCompliance) {
        try {
            return objectMapper.writeValueAsString(dynamicCompliance);
        } catch (JsonProcessingException e) {
            throw new DynamicComplianceException(
                    String.format("Cannot serialise dynamic Compliance for compliant entity:%s",
                            dynamicCompliance.getCompliantEntityId()),
                    e.getCause());
        }
    }
}
