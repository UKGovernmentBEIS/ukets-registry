package gov.uk.ets.registry.api.account.messaging;

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
public class CompliantEntityInitializationEvent extends ComplianceOutgoingEventBase {

    public ComplianceEventType getType() {
        return ComplianceEventType.COMPLIANT_ENTITY_INITIALIZATION;
    }

    private Integer firstYearOfVerifiedEmissions;
    private Integer lastYearOfVerifiedEmissions;
    private Integer currentYear;

    @Override
    protected boolean doValidate() {
        return firstYearOfVerifiedEmissions != null && currentYear != null;
    }
}
