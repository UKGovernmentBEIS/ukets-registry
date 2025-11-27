package gov.uk.ets.registry.api.integration.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationEventErrorDetails {

    private IntegrationEventError error;
    private String errorMessage;
}
