package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The account access transfer object.
 */
@Getter
@Setter
@AllArgsConstructor
public class AccountAccessDTO implements Serializable {
    /**
     * The account business identifier.
     */
    String accountFullIdentifier;

    /**
     * The account access right.
     */
    AccountAccessRight accessRight;
}
