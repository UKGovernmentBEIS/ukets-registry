package gov.uk.ets.registry.api.compliance.messaging.events.incoming;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = ComplianceEventType.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComplianceCalculatedEvent.class, name = "CALCULATION"),
    @JsonSubTypes.Type(value = StaticComplianceRetrievedEvent.class, name = "STATIC_RETRIEVAL"),
    @JsonSubTypes.Type(value = ComplianceCalculationErrorEvent.class, name = "CALCULATION_ERROR")
})
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public class ComplianceResponseEvent {
    private UUID originatingEventId;
    private Long compliantEntityId;
    private LocalDateTime dateTriggered;
    private ComplianceStatus status;

}
