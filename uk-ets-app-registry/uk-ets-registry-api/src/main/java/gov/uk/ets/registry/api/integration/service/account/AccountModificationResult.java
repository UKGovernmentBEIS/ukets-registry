package gov.uk.ets.registry.api.integration.service.account;

import java.util.List;
import lombok.Getter;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@Getter
public class AccountModificationResult {

    public AccountModificationResult(String accountFullIdentifier,
                                     List<IntegrationEventErrorDetails> errors
                                     ) {
        this.accountFullIdentifier = accountFullIdentifier;
        this.errors = errors;
        this.modified = false;
    }

    public AccountModificationResult(String accountFullIdentifier, Boolean modified) {
        this.accountFullIdentifier = accountFullIdentifier;
        this.modified = modified;
    }

    public AccountModificationResult(List<IntegrationEventErrorDetails> errors) {
        this.modified = false;
        this.errors = errors;
    }

    private Boolean modified;
    private String accountFullIdentifier;
    private List<IntegrationEventErrorDetails> errors = List.of();
}
