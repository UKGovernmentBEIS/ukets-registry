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
public class GetCurrentDynamicStatusEvent extends ComplianceOutgoingEventBase {

    private static final long serialVersionUID = -7778821556189549269L;

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.GET_CURRENT_DYNAMIC_STATUS;
    }

    @Override
    protected boolean doValidate() {
        //TODO Maybe add something meaningful
        return true;
    }

}
