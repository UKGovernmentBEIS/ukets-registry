package gov.uk.ets.compliance.messaging;

import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventBase;
import gov.uk.ets.compliance.outbox.ComplianceOutgoingEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComplianceCalculatedEvent extends ComplianceOutgoingEventBase {

    @Override
    public ComplianceOutgoingEventType getType() {
        return ComplianceOutgoingEventType.CALCULATION;
    }

    @Override
    protected boolean isValid() {
        return true;
    }
}
