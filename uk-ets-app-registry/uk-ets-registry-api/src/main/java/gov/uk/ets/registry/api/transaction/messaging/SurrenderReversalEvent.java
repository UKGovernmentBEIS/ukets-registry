package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class SurrenderReversalEvent extends ComplianceOutgoingEventBase {

    private long amount;

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.REVERSAL_OF_SURRENDER;
    }

    @Override
    protected boolean doValidate() {
        return amount > 0;
    }
}
