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
public class UpdateFirstYearOfVerifiedEmissionsEvent extends ComplianceOutgoingEventBase {

    private Integer firstYearOfVerifiedEmissions;

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.UPDATE_OF_FIRST_YEAR_OF_VERIFIED_EMISSIONS;
    }

    @Override
    protected boolean doValidate() {
        return firstYearOfVerifiedEmissions != null;
    }
}
