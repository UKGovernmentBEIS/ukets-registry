package gov.uk.ets.registry.api.integration.message;

import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpeningEventOutcome {

    private AccountOpeningEvent event;
    private String accountIdentifier;
    private List<IntegrationEventErrorDetails> errors;
    private IntegrationEventOutcome outcome;
}
