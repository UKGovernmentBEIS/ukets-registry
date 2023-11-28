package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.tal.domain.TrustedAccountFilter;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountListType;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.tal.web.model.search.TALProjection;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service for trusted accounts.
 */
public interface TrustedAccountListService {

    /**
     * Retrieves the trusted account list for the account with the provided business identifier.
     *
     * @param identifier the account business identifier.
     * @return the trusted account list.
     */
    Set<TrustedAccountDTO> getTrustedAccounts(Long identifier);

    /**
     * Retrieves the trusted account list for the account with the provided business identifier.
     *
     * @param identifier the account business identifier.
     * @param type       the trusted account list type.
     * @return the trusted account list.
     */
    Set<TrustedAccountDTO> getTrustedAccounts(Long identifier, TrustedAccountListType type);

    /**
     * Searches for trusted accounts that match the given criteria.
     *
     * @param filter The search criteria.
     * @param pageable The pagination info.
     * @return the trusted account list.
     */
    Page<TALProjection> search(TrustedAccountFilter filter, Pageable pageable);

    /**
     * Adds a trusted account to the trusted account list of the specified host account.
     *
     * @param trustedAccount the trusted account to be added.
     * @param accountId      the business identifier of the host account.
     * @return the generated task business identifier.
     */
    Long addTrustedAccount(TrustedAccountDTO trustedAccount, Long accountId);

    /**
     * Updates a trusted account of the specified host account.
     *
     * @param trustedAccountDto the trusted accounts to be updated.
     * @param accountId         the business identifier of the host account.
     * @return the updated {@link TrustedAccountDTO trusted account details}.
     */
    TrustedAccountDTO updateTrustedAccount(TrustedAccountDTO trustedAccountDto, Long accountId);

    /**
     * Removes a list of trusted accounts from the trusted account list of the specified host account.
     *
     * @param trustedAccounts the list of trusted accounts to be removed.
     * @param accountId       the business identifier of the host account.
     * @return the generated task business identifier.
     */
    Long removeTrustedAccounts(List<TrustedAccountDTO> trustedAccounts, Long accountId);

    void cancelTrustedAccount(Long accountIdentifier, String trustedAccountFullIdentifier);
}
