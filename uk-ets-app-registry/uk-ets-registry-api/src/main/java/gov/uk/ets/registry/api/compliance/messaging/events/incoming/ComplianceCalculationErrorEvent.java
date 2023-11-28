package gov.uk.ets.registry.api.compliance.messaging.events.incoming;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ComplianceCalculationErrorEvent extends ComplianceResponseEvent {

    private String message;
}
