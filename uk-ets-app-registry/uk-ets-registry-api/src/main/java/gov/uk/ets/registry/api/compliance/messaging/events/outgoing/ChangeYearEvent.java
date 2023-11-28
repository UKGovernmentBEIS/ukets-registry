package gov.uk.ets.registry.api.compliance.messaging.events.outgoing;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChangeYearEvent extends ComplianceOutgoingEventBase {
    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.NEW_YEAR;
    }

    @Override
    protected boolean doValidate() {
        return true;
    }
}
