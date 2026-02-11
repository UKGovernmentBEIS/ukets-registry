package gov.uk.ets.registry.api.compliance.messaging.events.outgoing;

import java.util.Optional;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChangeYearEvent extends ComplianceOutgoingEventBase {

    @Override
    public ComplianceEventType getType() {
        return ComplianceEventType.NEW_YEAR;
    }

    private Integer newYear;
    
    @Override
    protected boolean doValidate() {
        return Optional.of(newYear).isPresent();
    }
}
