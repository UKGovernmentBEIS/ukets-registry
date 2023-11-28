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
public class ExclusionReversalEvent extends DynamicComplianceEvent {

    private int year;

    public ComplianceEventType getType() {
        return ComplianceEventType.REVERSAL_OF_EXCLUSION;
    }

    @Override
    protected boolean isValid() {
        return year > 0;
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        state.excludeReverse(year);
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] Year " + year + " was excluded(reversal).";
        } else {
            return "[ ] Year " + year + " was not excluded(reversal).";
        }
    }
}
