package gov.uk.ets.compliance.outbox;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.uk.ets.compliance.domain.ComplianceStatus;
import gov.uk.ets.compliance.messaging.ComplianceCalculatedEvent;
import gov.uk.ets.compliance.messaging.ComplianceCalculationErrorEvent;
import gov.uk.ets.compliance.messaging.StaticComplianceRetrievedEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * we have to specify that the EXISTING_PROPERTY here otherwise the serializer will add the type property too,
 * so it will be duplicated in the json.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = ComplianceOutgoingEventType.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ComplianceCalculatedEvent.class, name = "CALCULATION"),
        @JsonSubTypes.Type(value = StaticComplianceRetrievedEvent.class, name = "STATIC_RETRIEVAL"),
        @JsonSubTypes.Type(value = ComplianceCalculationErrorEvent.class, name = "CALCULATION_ERROR")
})
@SuperBuilder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@ToString
public abstract class ComplianceOutgoingEventBase implements Serializable {

    /**
     * <p>We require the concrete events to specify the type. </p>
     *
     * <p>This is convenient to be defined as an abstract method instead of a field because
     * this way the Builder does not generate a method for the type (we don't want to allow
     * the clients to accidentally change the type defined in the concrete event).
     */
    public abstract ComplianceOutgoingEventType getType();

    private UUID originatingEventId;
    private Long compliantEntityId;
    private LocalDateTime dateTriggered;
    private ComplianceStatus status;

    /**
     * Validates common event properties then delegates to concrete event validation logic.
     */
    public final boolean isValidOutgoingEvent() {
        if (originatingEventId == null || compliantEntityId == null || dateTriggered == null || status == null ||
            getType() == null) {
            return false;
        }
        return isValid();
    }

    /**
     * Forces the concrete events to implement some form of validation.
     */
    protected abstract boolean isValid();

}
