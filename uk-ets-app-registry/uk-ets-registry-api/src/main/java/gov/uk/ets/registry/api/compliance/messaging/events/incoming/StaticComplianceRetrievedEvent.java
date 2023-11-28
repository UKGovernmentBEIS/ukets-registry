package gov.uk.ets.registry.api.compliance.messaging.events.incoming;

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
public class StaticComplianceRetrievedEvent extends ComplianceResponseEvent {

}
