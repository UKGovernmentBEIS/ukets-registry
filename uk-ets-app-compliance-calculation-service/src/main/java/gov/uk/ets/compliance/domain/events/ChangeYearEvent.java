package gov.uk.ets.compliance.domain.events;

import java.util.Optional;

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

    @Override
    protected boolean isValid() {
        return true;
    }

    /**
     * When a Change Year event should check before changing the current Year if
     * the previous year is excluded. If yes then we should exclude the next
     * year as well. Additionally a change year event shall update the reporting
     * period only when the last YearOf verified emissions is not previously
     * defined. e.g given the current year is 2024 and when a change Year event
     * is processed I.e. we are in 2025) then If 2023 was already in the
     * excluded list then we should add 2024 in the excluded years as well if
     * the lastYearOf verified Emissions in not defined then the reporting
     * period end is extended to 2025
     *
     * @param state
     *            the current compliance state.
     * @return the updated compliance state.
     */
    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {

        // increase the current year
        state.nextYear();
        
        if (!shouldSkipRollOverOfExclusionYear(state)) {
            // Roll over happens here
            if (state.isExcludedForYear(state.getCurrentYear() - 1)) {
                state.exclude(state.getCurrentYear());
            }
        }

        // Update the reporting period only the last year is undefined.
        if (state.getLastYearOfVerifiedEmissions() == null) {
            state.updateReportingPeriodEnd(state.getCurrentYear() - 1);
        }
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] The year was increased.";
        } else {
            return "[ ] The year was not increased.";
        }
    }

    /**
     * This method is similar to
     * {@link gov.uk.ets.registry.api.compliance.messaging.ChangeYearEventScheduler#shouldSkipRollOverOfExclusionYear(CompliantEntity,long)}
     *
     * @return true if the roll over <strong>should not</strong> be performed
     *         false otherwise
     */
    private boolean shouldSkipRollOverOfExclusionYear(ComplianceState state) {
        var yearBeforeLast = state.getCurrentYear() - 1;
        // We must be at least 2 years ahead of start before a roll over takes
        // place.
        if (Optional.ofNullable(state.getFirstYearOfVerifiedEmissions())
                .orElseThrow() > yearBeforeLast) {
            return true;
        }

        // We must be at most 1 year ahead of last for a roll over to take
        // place.
        Optional<Integer> endYearOptional = Optional
                .ofNullable(state.getLastYearOfVerifiedEmissions());
        if (endYearOptional.isPresent()
                && endYearOptional.get() <= yearBeforeLast) {
            return true;
        }

        return false;
    }
}
