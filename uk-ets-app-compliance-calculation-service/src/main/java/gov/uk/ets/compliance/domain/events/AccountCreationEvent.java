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
@Deprecated
/**
 * @deprecated use {@link CompliantEntityInitializationEvent} instead of this one
 * @author P35036
 *
 */
public class AccountCreationEvent extends DynamicComplianceEvent implements InitCompliantEntity {

    private int firstYearOfVerifiedEmissions;

    // object here cause lastYearOfVerifiedEmissions can be null
    private Integer lastYearOfVerifiedEmissions;

    private int currentYear;

    public AccountCreationEvent(Long compliantEntityId, int firstYearOfVerifiedEmissions,
                                Integer lastYearOfVerifiedEmissions,
                                int currentYear) {
        this.compliantEntityId = compliantEntityId;
        this.firstYearOfVerifiedEmissions = firstYearOfVerifiedEmissions;
        this.lastYearOfVerifiedEmissions = lastYearOfVerifiedEmissions;
        this.currentYear = currentYear;
    }

    public ComplianceEventType getType() {
        return ComplianceEventType.ACCOUNT_CREATION;
    }

    @Override
    protected boolean isValid() {
        boolean isFYVELessOrEqualThanLYVE =
            (lastYearOfVerifiedEmissions == null) || lastYearOfVerifiedEmissions >= firstYearOfVerifiedEmissions;
        return firstYearOfVerifiedEmissions > 0 && currentYear > 0 && isFYVELessOrEqualThanLYVE;
    }

    /**
     * The account creation event is initiating the Compliance State,
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
