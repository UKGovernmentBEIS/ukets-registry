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
public class UpdateLastYearOfVerifiedEmissionsEvent extends DynamicComplianceEvent {

    private Integer lastYearOfVerifiedEmissions;

    public ComplianceEventType getType() {
        return ComplianceEventType.UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS;
    }

    @Override
    protected boolean isValid() {
        return lastYearOfVerifiedEmissions == null || lastYearOfVerifiedEmissions > 0;
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        state.updateLastYearOfVerifierEmissions(lastYearOfVerifiedEmissions);
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] Last year of verified emissions was set to: " + lastYearOfVerifiedEmissions;
        } else {
            return "[ ] Last year of verified emissions was not set to: " + lastYearOfVerifiedEmissions;
        }
    }
}
