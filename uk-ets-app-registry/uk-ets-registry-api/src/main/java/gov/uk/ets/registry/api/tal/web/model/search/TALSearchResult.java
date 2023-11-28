package gov.uk.ets.registry.api.tal.web.model.search;

import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * The Data transfer object for trusted accounts search result rows
 */

@Getter
@Setter
public class TALSearchResult implements SearchResult {
    public static TALSearchResult of(TALProjection trustedAccount) {
        TALSearchResult results = new TALSearchResult();
        results.setId(trustedAccount.getId());
        results.setAccountFullIdentifier(trustedAccount.getAccountFullIdentifier());
        results.setUnderSameAccountHolder(trustedAccount.getUnderSameAccountHolder());
        results.setDescription(trustedAccount.getDescription());
        results.setName(trustedAccount.getName());
        results.setStatus(trustedAccount.getStatus());
        results.setActivationDate(trustedAccount.getActivationDate());
        return results;
    }
    /**
     * The ID of the trusted account.
     */
    private Long id;
    /**
     * The full identifier of the trusted account.
     */
    private String accountFullIdentifier;

    /**
     * Signifies if the trusted account is under the same account holder as the host account.
     */
    private Boolean underSameAccountHolder;

    /**
     * The trusted account description.
     */
    private String description;

    /**
     * The trusted account name.
     */
    private String name;

    /**
     * The trusted account status.
     */
    private TrustedAccountStatus status;

    /**
     * The planned activation date of the trusted account.
     */
    private ZonedDateTime activationDate;

}
