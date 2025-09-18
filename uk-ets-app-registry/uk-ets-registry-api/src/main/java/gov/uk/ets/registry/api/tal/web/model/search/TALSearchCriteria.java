package gov.uk.ets.registry.api.tal.web.model.search;


import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The Trusted Accounts search criteria as they received from web client.
 */

@Getter
@Setter
public class TALSearchCriteria {
    @NotNull
    private Long accountId;

    private String accountNumber;

    @Size(min = 3)
    private String accountNameOrDescription;

    private Boolean trustedAccountType;
}
