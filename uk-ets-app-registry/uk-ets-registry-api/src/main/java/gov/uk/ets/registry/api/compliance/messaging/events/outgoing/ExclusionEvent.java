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
public class ExclusionEvent extends ComplianceOutgoingEventBase {

    private static final long serialVersionUID = 2506019404659469292L;

    public ComplianceEventType getType() {
        return ComplianceEventType.EXCLUSION;
    }

    private Integer year;

    @Override
    protected boolean doValidate() {
        return Optional.of(year).isPresent();
    }
}
