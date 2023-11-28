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
public class ExclusionEvent extends DynamicComplianceEvent {

    private int year;

    public ComplianceEventType getType() {
        return ComplianceEventType.EXCLUSION;
    }

    @Override
    protected boolean isValid() {
        return year > 0;
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        state.exclude(year);
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] Year " + year + " was excluded.";
        } else {
            return "[ ] Year " + year + " was not excluded.";
        }
    }
}
