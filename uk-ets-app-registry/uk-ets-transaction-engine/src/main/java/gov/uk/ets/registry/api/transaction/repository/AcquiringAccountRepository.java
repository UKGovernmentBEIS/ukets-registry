package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for retrieving acquiring accounts.
 */
public interface AcquiringAccountRepository extends JpaRepository<UnitBlock, Long> {

    /**
     * Retrieves eligible acquiring accounts under the same account holder with the provided account.
     *
     * @param accountIdentifier The account identifier.
     * @return some trusted accounts.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo(" +
        "       a.identifier,                                 " +
        "       a.fullIdentifier,                             " +
        "       a.accountName,                                " +
        "       ah.name,                                      " +
        "       a.registryCode,                               " +
        "       true)                                         " +
        "  from Account a, AccountHolder ah                   " +
        " where a.accountHolder.id = (                        " +
        "       select acc.accountHolder.id                   " +
        "         from Account acc                            " +
        "        where acc.identifier = ?1)                   " +
        "   and a.accountStatus not in ('SUSPENDED', 'CLOSED', 'PROPOSED', 'REJECTED')" +
        "   and a.identifier <> ?1                            " +
        "   and ah.id = a.accountHolder.id                    ")
    List<AcquiringAccountInfo> retrieveEligibleAcquiringAccountsUnderTheSameAccountHolder(Long accountIdentifier);

    /**
     * Retrieves the ACTIVE trusted accounts of the provided account as acquiring accounts.
     *
     * @param accountIdentifier The account identifier.
     * @return some acquiring accounts.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo(" +
        "       ta.trustedAccountFullIdentifier,                " +
        "       ta.description,                                 " +
        "       ah.name,                                        " +
        "       true )                                          " +
        "  from Account a, TrustedAccount ta, AccountHolder ah  " +
        " where ta.account.identifier = ?1                              " +
        "   and ta.status = 'ACTIVE'                            " +
        "   and a.id = ta.account.id                            " +
        "   and ah.id = a.accountHolder.id                      ")
    List<AcquiringAccountInfo> retrieveOtherTrustedAccountsAsAcquiringAccounts(Long accountIdentifier);

    /**
     * Retrieves an acquiring account based on the user defined full identifier.
     *
     * @param fullIdentifier The account full identifier.
     * @return an account.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountInfo(" +
        "       a.identifier,                                 " +
        "       a.fullIdentifier,                             " +
        "       a.accountName,                                " +
        "       ah.name,                                      " +
        "       a.registryCode)                               " +
        "  from Account a, AccountHolder ah                   " +
        " where a.fullIdentifier = ?1                        " +
        "   and ah.id = a.accountHolder.id                    ")
    AccountInfo retrieveUserDefinedAccount(String fullIdentifier);

    /**
     * Retrieves an account with its account type (for the needs of transaction reversals)
     * based on the user defined full identifier.
     * @param fullIdentifier The account full identifier.
     * @return the AccountInfo.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountInfo(" +
           "       a.identifier,                                 " +
           "       a.fullIdentifier,                             " +
           "       a.accountName,                                " +
           "       ah.name,                                      " +
           "       a.registryCode,                               " +
           "       a.accountType)                                " +
           "  from Account a, AccountHolder ah                   " +
           " where a.fullIdentifier = ?1                        " +
           "   and ah.id = a.accountHolder.id                    ")
    AccountInfo retrieveAccountForReversedTransactions(String fullIdentifier);

    /**
     * Retrieves predefined candidate ETS acquiring accounts, based on the accountTypes provided.
     *
     * @param accountIdentifier The account identifier.
     * @param registryAccountTypes The queried account types.
     * @return some trusted accounts.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo(" +
        "       a.identifier,                                 " +
        "       a.fullIdentifier,                             " +
        "       a.accountName,                                " +
        "       ah.name,                                      " +
        "       a.registryCode,                               " +
        "       false )                                       " +
        "  from Account a, Account b, AccountHolder ah        " +
        " where a.accountStatus not in ('SUSPENDED', 'CLOSED', 'PROPOSED', 'REJECTED')" +
        "   and a.identifier <> ?1                            " +
        "   and a.registryAccountType in ?2                           " +
        "   and b.identifier = ?1                            " +
        "   and b.registryAccountType in ?2                           " +
        "   and ah.id = a.accountHolder.id                    ")
    List<AcquiringAccountInfo> retrievePredefinedCandidateEtsAcquiringAccounts(Long accountIdentifier,
                                                                               List<RegistryAccountType> registryAccountTypes);


}
