package gov.uk.ets.compliance.domain.events.base;

import gov.uk.ets.compliance.domain.events.DynamicComplianceUpdateEvent;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Incoming events concerning the dynamic compliance calculation. All dynamic compliance event should extend this class.
 */
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public abstract class DynamicComplianceEvent extends ComplianceEvent implements DynamicComplianceUpdateEvent {
}
