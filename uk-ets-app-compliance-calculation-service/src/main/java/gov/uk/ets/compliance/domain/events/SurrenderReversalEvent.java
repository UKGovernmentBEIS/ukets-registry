package gov.uk.ets.compliance.domain.events;

import gov.uk.ets.compliance.domain.ComplianceState;
import gov.uk.ets.compliance.domain.events.base.DynamicComplianceEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class SurrenderReversalEvent extends DynamicComplianceEvent {

    private long amount;

    public ComplianceEventType getType() {
        return ComplianceEventType.REVERSAL_OF_SURRENDER;
    }

    @Override
    protected boolean isValid() {
        return amount >= 0;
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        state.surrenderReversal(amount);
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] Amount (" + amount + ") was reversed.";
        } else {
            return "[ ] Amount (" + amount + ") was not reversed.";
        }
    }
}
