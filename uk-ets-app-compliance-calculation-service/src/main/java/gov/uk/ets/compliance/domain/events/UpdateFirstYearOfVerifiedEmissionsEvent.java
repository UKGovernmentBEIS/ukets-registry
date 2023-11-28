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
public class UpdateFirstYearOfVerifiedEmissionsEvent extends DynamicComplianceEvent {

    private int firstYearOfVerifiedEmissions;

    public ComplianceEventType getType() {
        return ComplianceEventType.UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS;
    }

    @Override
    protected boolean isValid() {
        return firstYearOfVerifiedEmissions > 0;
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        state.updateFirstYearOfVerifierEmissions(firstYearOfVerifiedEmissions);
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] First year of verified emissions was set to " + firstYearOfVerifiedEmissions;
        } else {
            return "[ ] First year of verified emissions was not set to " + firstYearOfVerifiedEmissions;
        }
    }
}
