package gov.uk.ets.compliance.domain.events.base;

import gov.uk.ets.compliance.domain.events.ComplianceEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Incoming event containing a request for retrieval of the compliance status at the requested date.
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class StaticComplianceRequestEvent extends ComplianceEvent {

    @Override
    protected ComplianceEventType getType() {
        return ComplianceEventType.STATIC_COMPLIANCE_REQUEST;
    }

    @Override
    protected boolean isValid() {
        return this.dateRequested != null;
    }
}
