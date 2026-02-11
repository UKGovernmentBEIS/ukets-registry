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
public class ChangeYearEvent extends DynamicComplianceEvent {

    public ComplianceEventType getType() {
        return ComplianceEventType.NEW_YEAR;
    }

    private Integer newYear;
    
    //Do not check on year since old events have no value 
    @Override
    protected boolean isValid() {
        return true;
    }

    /**
     * When a change year event shall update the reporting period only when the
     * last YearOf verified emissions is not previously defined. e.g given the
     * current year is 2024 and when a change Year event is processed I.e. (we
     * are in 2025) then the reporting period end is extended to 2025
     *
     * @param state
     *            the current compliance state.
     * @return the updated compliance state.
     */
    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {

        // increase the current year
        state.nextYear(newYear);

        // Update the reporting period only the last year is undefined.
        if (state.getLastYearOfVerifiedEmissions() == null) {
            state.updateReportingPeriodEnd(state.getCurrentYear() - 1);
        }
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] The new year is " + newYear + ".";
        } else {
            return "[ ] The year was not increased.";
        }
    }
}
