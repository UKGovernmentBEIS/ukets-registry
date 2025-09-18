package gov.uk.ets.registry.api.compliance.messaging.events.outgoing;

import gov.uk.ets.registry.api.compliance.messaging.events.ComplianceEventType;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExclusionReversalEvent extends ComplianceOutgoingEventBase {

    private static final long serialVersionUID = -5264470816010097007L;

    public ComplianceEventType getType() {
        return ComplianceEventType.REVERSAL_OF_EXCLUSION;
    }

    private Integer year;

    private String reason;

    @Override
    protected boolean doValidate() {
        return Optional.of(year).isPresent();
    }
}
