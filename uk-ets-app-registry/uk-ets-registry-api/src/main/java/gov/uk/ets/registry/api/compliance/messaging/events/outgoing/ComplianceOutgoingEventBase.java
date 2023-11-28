package gov.uk.ets.registry.api.compliance.messaging.events.outgoing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import gov.uk.ets.registry.api.account.messaging.AccountCreationEvent;
import gov.uk.ets.registry.api.account.messaging.CompliantEntityInitializationEvent;
import gov.uk.ets.registry.api.account.messaging.compliance.UpdateFirstYearOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.account.messaging.compliance.UpdateLastYearOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import gov.uk.ets.registry.api.file.upload.emissionstable.messaging.UpdateOfVerifiedEmissionsEvent;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderEvent;
import gov.uk.ets.registry.api.transaction.messaging.SurrenderReversalEvent;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * we have to specify that the EXISTING_PROPERTY here otherwise the serializer will add the type property too,
 * so it will be duplicated in the json.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    defaultImpl = ComplianceEventType.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AccountCreationEvent.class, name = "ACCOUNT_CREATION"),
    @JsonSubTypes.Type(value = CompliantEntityInitializationEvent.class, name = "COMPLIANT_ENTITY_INITIALIZATION"),
    @JsonSubTypes.Type(value = UpdateOfVerifiedEmissionsEvent.class, name = "UPDATE_OF_VERIFIED_EMISSIONS"),
    @JsonSubTypes.Type(value = ChangeYearEvent.class, name = "NEW_YEAR"),
    @JsonSubTypes.Type(value = SurrenderEvent.class, name = "SURRENDER"),
    @JsonSubTypes.Type(value = SurrenderReversalEvent.class, name = "REVERSAL_OF_SURRENDER"),
    @JsonSubTypes.Type(value = ExclusionEvent.class, name = "EXCLUSION"),
    @JsonSubTypes.Type(value = ExclusionReversalEvent.class, name = "REVERSAL_OF_EXCLUSION"),
    @JsonSubTypes.Type(value = UpdateFirstYearOfVerifiedEmissionsEvent.class, name = "UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS"),
    @JsonSubTypes.Type(value = UpdateLastYearOfVerifiedEmissionsEvent.class, name = "UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS"),
    @JsonSubTypes.Type(value = StaticComplianceRequestEvent.class, name = "STATIC_COMPLIANCE_REQUEST"),
    @JsonSubTypes.Type(value = GetCurrentDynamicStatusEvent.class, name = "GET_CURRENT_DYNAMIC_STATUS"),
    @JsonSubTypes.Type(value = RecalculateDynamicStatusEvent.class, name = "RECALCULATE_DYNAMIC_STATUS")
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
    public abstract ComplianceEventType getType();

    @Builder.Default
    private final UUID eventId = UUID.randomUUID();
    private Long compliantEntityId;
    private String actorId;
    /**
     * Timestamp representing when the event was triggered;
     */
    protected LocalDateTime dateTriggered;
    /**
     * Timestamp representing when the original action/task was processed.
     * <p>CAUTION: This is for example the timestamp of a transaction task approval
     * <strong>NOT</strong> of the transaction completion.</p>
     */
    protected LocalDateTime dateRequested;

    /**
     * Validates common event properties then delegates to concrete event validation logic.
     */
    @JsonIgnore
    public final boolean isValid() {
        if (compliantEntityId == null || actorId == null || dateTriggered == null || getType() == null ||
            getDateRequested() == null) {
            return false;
        }
        return doValidate();
    }

    /**
     * Forces the concrete events to implement some form of validation.
     */
    protected abstract boolean doValidate();
}
