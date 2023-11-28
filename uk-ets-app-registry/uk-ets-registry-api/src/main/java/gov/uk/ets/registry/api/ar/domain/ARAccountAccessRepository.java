package gov.uk.ets.registry.api.ar.domain;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import java.util.List;

/**
 * Repository for {@link AccountAccess} objects related to authorized representatives
 */
public interface ARAccountAccessRepository {

    /**
     * Fetches the authorized representative related {@link AccountAccess} entities of the account with identifier the
     * passed accountIdentifier parameter. It returns the {@link AccountAccess} entities with {@link AccountAccessState}
     * state equal with the passed accountAccessState parameter. If the accountAccessState is null then it does not
     * filter by state and returns all the {@link AccountAccessState} entities related with the account.
     *
     * @param accountIdentifier  The mandatory account identifier filter.
     * @param accountAccessState The optional {@link AccountAccessState} filter
     * @return The {@link AccountAccess} entities
     */
    List<AccountAccess> fetchARs(Long accountIdentifier, AccountAccessState accountAccessState);

    /**
     * It fetches the authorized representatives related {@link AccountAccess} entities for all accounts that the user
     * with urid is an AR, except these that are related to the account with identifier equal with the passed
     * accountIdentifier parameter.
     *
     * If no urid is passed, then the {@link AccountAccess} entities returned are the ones associated
     * only with the direct account holder.
     *
     * @param accountIdentifier The unique business identifier of the account which related {@link AccountAccess} are
     *                          excluded from the search.
     * @param urid              The unique business identifier of the current AR user
     * @return The {@link AccountAccess} entities
     */
    List<AccountAccess> fetchArsForAccount(long accountIdentifier, String urid);

}
