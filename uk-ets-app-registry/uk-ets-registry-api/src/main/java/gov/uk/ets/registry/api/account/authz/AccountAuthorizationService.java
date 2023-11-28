package gov.uk.ets.registry.api.account.authz;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.web.model.AccountAccessDTO;
import java.util.List;

/**
 * Service for Account Authorization.
 */
public interface AccountAuthorizationService {
    /**
     * Checks if the current user has access rights to the account.
     *
     * @param accountIdentifier The account short identifier.
     */
    AccountAccessRight checkAccountAccess(Long accountIdentifier) throws AccountActionException;

    /**
     * Checks if the current user has the queried access rights to the account.
     *
     * @param accountIdentifier  The account short identifier.
     * @param queriedAccessRight The queried access rights.
     */
    boolean checkAccountAccess(Long accountIdentifier, AccountAccessRight queriedAccessRight);

    /**
     * Checks if the user with userId has the queried access rights to the account.
     *
     * @param accountIdentifier  The account short identifier.
     * @param urid               The user business identifier
     * @param queriedAccessRight The queried access rights.
     */
    boolean checkAccountAccess(Long accountIdentifier, String urid, AccountAccessRight queriedAccessRight);

    /**
     * Retrieves the account identifiers on which the current user has access rights.
     *
     * @return The accounts the user has access rights to.
     */
    List<AccountAccessDTO> getAccountAccessesForCurrentUser();

    /**
     * Retrieves the accounts for which the given user is AR (active or suspended).
     */
    List<AccountAccessDTO> getActiveOrSuspendedAccountAccessesForUrid(String urid);
}
