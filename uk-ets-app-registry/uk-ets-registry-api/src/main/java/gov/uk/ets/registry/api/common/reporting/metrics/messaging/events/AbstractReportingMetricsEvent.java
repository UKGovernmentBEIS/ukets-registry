package gov.uk.ets.registry.api.common.reporting.metrics.messaging.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import gov.uk.ets.registry.api.common.reporting.metrics.types.ReportingMetricsEventType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EmissionsUpdatedEvent.class, name = "UPDATE_OF_VERIFIED_EMISSIONS"),
    @JsonSubTypes.Type(value = TransactionFinalisationEvent.class, name = "TRANSACTION_FINALISATION"),
    @JsonSubTypes.Type(value = DynamicComplianceStatusCalculatedEvent.class, name = "RECALCULATE_DYNAMIC_STATUS"),
    @JsonSubTypes.Type(value = InstallationTransferEvent.class, name = "INSTALLATION_TRANSFER")
})
@SuperBuilder
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractReportingMetricsEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * We require the concrete events to specify the type.
     * This is convenient to be defined as an abstract method instead of a field because
     * this way the Builder does not generate a method for the type (we don't want to allow
     * the clients to accidentally change the type defined in the concrete event).
     */
    public abstract ReportingMetricsEventType getType();

    @Builder.Default
    private UUID eventId = UUID.randomUUID();

    public abstract String getIdentifier();
    
    /**
     * Timestamp representing when the event was triggered.
     */
    @Builder.Default
    protected LocalDateTime dateTriggered = LocalDateTime.now();
}
