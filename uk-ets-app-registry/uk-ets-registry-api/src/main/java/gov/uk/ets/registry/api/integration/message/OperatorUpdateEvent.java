package gov.uk.ets.registry.api.integration.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperatorUpdateEvent {

    private Long operatorId;
    private String emitterId;
    private String regulator;
}
