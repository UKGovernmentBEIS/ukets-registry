package gov.uk.ets.registry.api.integration.message;

import gov.uk.ets.registry.api.integration.error.IntegrationEventError;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEmissionsUpdateEventOutcome {

    private AccountEmissionsUpdateEvent event;
    private List<IntegrationEventError> errors;
    private IntegrationEventOutcome outcome;
}
