package gov.uk.ets.registry.api.integration.service.account;

import java.util.List;
import lombok.Getter;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

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
