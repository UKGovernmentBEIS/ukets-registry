package gov.uk.ets.compliance.domain.events;

import gov.uk.ets.compliance.domain.ComplianceState;
import gov.uk.ets.compliance.domain.events.base.DynamicComplianceEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class CompliantEntityInitializationEvent extends DynamicComplianceEvent implements InitCompliantEntity {

    private int firstYearOfVerifiedEmissions;

    // object here cause lastYearOfVerifiedEmissions can be null
    private Integer lastYearOfVerifiedEmissions;

    private int currentYear;

    @Override
    protected boolean isValid() {
        boolean isFYVELessOrEqualThanLYVE =
            (lastYearOfVerifiedEmissions == null) || lastYearOfVerifiedEmissions >= firstYearOfVerifiedEmissions;
        return firstYearOfVerifiedEmissions > 0 && currentYear > 0 && isFYVELessOrEqualThanLYVE;
    }

    public ComplianceEventType getType() {
        return ComplianceEventType.COMPLIANT_ENTITY_INITIALIZATION;
    }

    /**
     * The Compliant Entity Initialization event is initiating the Compliance State,
     * so it does not update the state.
     * TODO: this needs to be removed
     *
     * @param state
     * @return
     */
    @Override
    public ComplianceState updateComplianceState(ComplianceState state) {
        return state;
    }
}
