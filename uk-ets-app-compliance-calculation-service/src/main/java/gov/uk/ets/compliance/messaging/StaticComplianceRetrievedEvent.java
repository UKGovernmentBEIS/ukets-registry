package gov.uk.ets.compliance.messaging;

import gov.uk.ets.compliance.domain.events.base.StaticComplianceRequestEvent;
import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventBase;
import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Event emitted as a response to the {@link StaticComplianceRequestEvent}
 * containing information about the compliance status at the requested date.
 */
@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StaticComplianceRetrievedEvent extends ComplianceOutgoingEventBase {

    private LocalDateTime dateRequested;

    @Override
    public ComplianceOutgoingEventType getType() {
        return ComplianceOutgoingEventType.STATIC_RETRIEVAL;
    }

    @Override
    protected boolean isValid() {
        return dateRequested != null;
    }
}
