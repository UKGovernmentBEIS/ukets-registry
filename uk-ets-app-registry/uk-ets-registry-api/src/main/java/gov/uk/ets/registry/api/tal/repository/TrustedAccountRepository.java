package gov.uk.ets.registry.api.tal.repository;

import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for trusted accounts.
 */
public interface TrustedAccountRepository extends JpaRepository<TrustedAccount, Long>, TALSearchRepository {
    /**
     * Retrieves the trusted account list for the account with the provided business identifier.
     *
     * @param identifier the account business identifier.
     * @return the trusted account list.
     */
    List<TrustedAccount> findAllByAccountIdentifier(Long identifier);

    /**
     * Retrieves the trusted account list for the provided trusted account full identifier.
     *
     * @param fullIdentifier the trusted account full identifier.
     * @return the trusted account list.
     */
    List<TrustedAccount> findAllByTrustedAccountFullIdentifier(String fullIdentifier);

    /**
     * Retrieves the trusted account list for the account with the provided business identifier and the provided status.
     *
     * @param identifier the account business identifier.
     * @param statuses   the trusted account statuses.
     * @return the trusted account list.
     */
    List<TrustedAccount> findAllByAccountIdentifierAndStatusIn(Long identifier, List<TrustedAccountStatus> statuses);

    /**
     * Retrieves the count of trusted accounts for the account with the provided business identifier and the provided status.
     *
     * @param identifier the account business identifier.
     * @param statuses   the trusted account statuses.
     * @return the trusted accounts count.
     */
    Long countAllByAccountIdentifierAndStatusIn(Long identifier, List<TrustedAccountStatus> statuses);

    /**
     * Retrieves the trusted accounts which are awaiting for activation.
     *
     * @param status      The status.
     * @param currentTime The current time.
     * @return some trusted accounts.
     */
    List<TrustedAccount> findByStatusEqualsAndActivationDateBefore(TrustedAccountStatus status,
                                                                   LocalDateTime currentTime);

    /**
     * Fetch a set of the trusted accounts by id.
     *
     * @param idList the input ids
     * @return a list of  trusted accounts
     */
    List<TrustedAccount> findByIdIn(List<Long> idList);

    @Query("select ta " +
        "from TrustedAccount ta " +
        "inner join Account a " +
        "on a.id = ta.account.id " +
        "where a.identifier = ?1 and ta.trustedAccountFullIdentifier = ?2 and ta.status = ?3")
    TrustedAccount findByAccountAndTrustedAccountFullIdentifierAndStatus(Long identifier,
                                                                         String trustedAccountFullIdentifier,
                                                                         TrustedAccountStatus status);

    @Query("select a.identifier " +
        "from TrustedAccount ta " +
        "inner join Account a " +
        "on a.id = ta.account.id " +
        "where ta.id = ?1 and ta.trustedAccountFullIdentifier = ?2")
    Long findByIdAndAccountFullIdentifier(Long id, String trustedAccountFullIdentifier);
}
