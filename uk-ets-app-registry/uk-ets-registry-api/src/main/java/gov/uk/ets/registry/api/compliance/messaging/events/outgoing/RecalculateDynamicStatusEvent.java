package gov.uk.ets.registry.api.compliance.messaging.events.outgoing;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RecalculateDynamicStatusEvent extends ComplianceOutgoingEventBase {

    private static final long serialVersionUID = -8324106104802552826L;

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.RECALCULATE_DYNAMIC_STATUS;
    }

    @Override
    protected boolean doValidate() {
        return true;
    }
    
}
