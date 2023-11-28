package gov.uk.ets.compliance.domain.events.base;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gov.uk.ets.compliance.domain.events.AbstractEventThatCanBeMarked;
import gov.uk.ets.compliance.domain.events.AccountCreationEvent;
import gov.uk.ets.compliance.domain.events.ChangeYearEvent;
import gov.uk.ets.compliance.domain.events.ComplianceEventType;
import gov.uk.ets.compliance.domain.events.CompliantEntityInitializationEvent;
import gov.uk.ets.compliance.domain.events.ExclusionEvent;
import gov.uk.ets.compliance.domain.events.ExclusionReversalEvent;
import gov.uk.ets.compliance.domain.events.GetCurrentDynamicStatusEvent;
import gov.uk.ets.compliance.domain.events.RecalculateDynamicStatusEvent;
import gov.uk.ets.compliance.domain.events.SurrenderEvent;
import gov.uk.ets.compliance.domain.events.SurrenderReversalEvent;
import gov.uk.ets.compliance.domain.events.UpdateFirstYearOfVerifiedEmissionsEvent;
import gov.uk.ets.compliance.domain.events.UpdateLastYearOfVerifiedEmissionsEvent;
import gov.uk.ets.compliance.domain.events.UpdateOfVerifiedEmissionsEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Represents both dynamic and static compliance events. Concrete events should not extend this class.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = ComplianceEventType.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AccountCreationEvent.class, name = "ACCOUNT_CREATION"),
        @JsonSubTypes.Type(value = CompliantEntityInitializationEvent.class, name = "COMPLIANT_ENTITY_INITIALIZATION"),
        @JsonSubTypes.Type(value = UpdateOfVerifiedEmissionsEvent.class, name = "UPDATE_OF_VERIFIED_EMISSIONS"),
        @JsonSubTypes.Type(value = SurrenderEvent.class, name = "SURRENDER"),
        @JsonSubTypes.Type(value = SurrenderReversalEvent.class, name = "REVERSAL_OF_SURRENDER"),
        @JsonSubTypes.Type(value = ExclusionEvent.class, name = "EXCLUSION"),
        @JsonSubTypes.Type(value = ExclusionReversalEvent.class, name = "REVERSAL_OF_EXCLUSION"),
        @JsonSubTypes.Type(value = UpdateFirstYearOfVerifiedEmissionsEvent.class,
                name = "UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS"),
        @JsonSubTypes.Type(value = UpdateLastYearOfVerifiedEmissionsEvent.class,
                name = "UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS"),
        @JsonSubTypes.Type(value = ChangeYearEvent.class, name = "NEW_YEAR"),
        @JsonSubTypes.Type(value = GetCurrentDynamicStatusEvent.class, name = "GET_CURRENT_DYNAMIC_STATUS"),
        @JsonSubTypes.Type(value = RecalculateDynamicStatusEvent.class, name = "RECALCULATE_DYNAMIC_STATUS"),
        @JsonSubTypes.Type(value = StaticComplianceRequestEvent.class, name = "STATIC_COMPLIANCE_REQUEST"),
})
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public abstract class ComplianceEvent extends AbstractEventThatCanBeMarked {


    /**
     * <p>We require the concrete events to specify the type. </p>
     * <p>This is convenient to be defined as an abstract method instead of a field because
     * this way the Builder does not generate a method for the type (we don't want to allow
     * the clients to accidentally change the type defined in the concrete event).
     */
    protected abstract ComplianceEventType getType();

    protected UUID eventId;
    protected String actorId;
    protected Long compliantEntityId;
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

    public boolean isValidCompliantEvent() {
        return eventId != null && compliantEntityId != null && dateTriggered != null && isValid();
    }

    protected abstract boolean isValid();
}
