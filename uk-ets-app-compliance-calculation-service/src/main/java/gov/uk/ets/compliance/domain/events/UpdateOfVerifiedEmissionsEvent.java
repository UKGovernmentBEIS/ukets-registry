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
public class UpdateOfVerifiedEmissionsEvent extends DynamicComplianceEvent {

    private int year;
    private Long verifiedEmissions;

    public ComplianceEventType getType() {
        return ComplianceEventType.UPDATE_OF_VERIFIED_EMISSIONS;
    }

    @Override
    protected boolean isValid() {
        return year > 0 && (verifiedEmissions == null || verifiedEmissions >= 0);
    }

    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        state.updateVerifiedEmissions(year, verifiedEmissions);
        return state;
    }

    @Override
    public String toString() {
        if (isMarked()) {
            return "[X] Verified emissions (" + verifiedEmissions + ") were uploaded for year " + year + ".";
        } else {
            return "[ ] Verified emissions (" + verifiedEmissions + ") were not uploaded for year " + year + ".";
        }
    }
}
