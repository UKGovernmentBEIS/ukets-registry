package gov.uk.ets.registry.api.account.messaging.compliance;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import gov.uk.ets.registry.api.compliance.messaging.events.outgoing.ComplianceOutgoingEventBase;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class UpdateLastYearOfVerifiedEmissionsEvent extends ComplianceOutgoingEventBase {

    // null value means indefinite last year of verified emissions
    private Integer lastYearOfVerifiedEmissions;

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.UPDATE_OF_LAST_YEAR_OF_VERIFIED_EMISSIONS;
    }

    @Override
    protected boolean doValidate() {
        return lastYearOfVerifiedEmissions == null || lastYearOfVerifiedEmissions > 0;
    }
}
