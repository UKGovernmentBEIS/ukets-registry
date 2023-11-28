package gov.uk.ets.registry.api.notification.userinitiated.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AircraftOperatorParameters {

    /**
     * needed to correlate with compliant entity
     */
    private Long accountId;
    private String monitoringPlan;
    private Long id;
}
