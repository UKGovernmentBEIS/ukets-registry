package gov.uk.ets.registry.api.tal.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * Value of Search criteria on filtering trusted accounts.
 */
@Getter
@Builder

public class TrustedAccountFilter {
    /**
     * The account identifier.
     */
    private final Long identifier;

    /**
     * The trusted account identifier criterion.
     */
    private final String accountFullIdentifier;

    /**
     * The trusted account name criterion.
     */

    private final String name;

    /**
     * The trusted account type criterion.
     */

    private final Boolean underSameAccountHolder;
}
