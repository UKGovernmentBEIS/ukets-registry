package gov.uk.ets.registry.api.integration.service.account;

import gov.uk.ets.registry.api.integration.error.IntegrationEventErrorDetails;
import java.util.List;
import lombok.Getter;

@Getter
public class AccountOpeningResult {

    public AccountOpeningResult(String accountFullIdentifier) {
        this.accountFullIdentifier = accountFullIdentifier;
    }

    public AccountOpeningResult(List<IntegrationEventErrorDetails> errors) {
        this.errors = errors;
    }

    private String accountFullIdentifier;
    private List<IntegrationEventErrorDetails> errors = List.of();
}
