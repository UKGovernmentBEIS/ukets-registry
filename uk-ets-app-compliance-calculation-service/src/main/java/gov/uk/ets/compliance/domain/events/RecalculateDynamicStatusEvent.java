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
public class RecalculateDynamicStatusEvent extends DynamicComplianceEvent {


    @Override
    protected ComplianceEventType getType() {
        return ComplianceEventType.RECALCULATE_DYNAMIC_STATUS;
    }

    @Override
    protected boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] Recalculate dynamic status.";
        } else {
            return "[ ] Recalculate dynamic status.";
        }
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        //Nothing to do
        return state;
    }
    
}
