package gov.uk.ets.registry.api.compliance.messaging.events.outgoing;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ToString
public class StaticComplianceRequestEvent extends ComplianceOutgoingEventBase {

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.STATIC_COMPLIANCE_REQUEST;
    }

    @Override
    protected boolean doValidate() {
        return dateRequested != null;
    }
}
