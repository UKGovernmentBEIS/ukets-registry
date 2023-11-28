package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.List;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository for account totals.
 */
public interface AccountTotalRepository extends JpaRepository<UnitBlock, Long> {

    /**
     * Retrieves a summary of the account.
     * @param identifier The identifier.
     * @return an account summary.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountSummary(" +
            "      a.identifier, " +
            "      a.registryAccountType, " +
            "      a.kyotoAccountType, " +
            "      a.accountStatus," +
            "      a.registryCode," +
            "      a.fullIdentifier," +
            "      a.commitmentPeriodCode," +
            "      a.balance," +
            "      a.unitType)" +
            " from Account a " +
            "where a.identifier = ?1 " +
            "  and a.accountStatus not in (" +
            "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED, " +
            "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) ")
    AccountSummary getAccountSummary(Long identifier);

    /**
     * Retrieves a summary of the account.
     * @param fullIdentifier The full account identifier.
     * @return an account summary.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountSummary(" +
            "      a.identifier,          " +
            "      a.registryAccountType, " +
            "      a.kyotoAccountType,    " +
            "      a.accountStatus,       " +
            "      a.registryCode,        " +
            "      a.fullIdentifier,      " +
            "      a.commitmentPeriodCode," +
            "      a.balance,             " +
            "      a.unitType)            " +
            " from Account a              " +
            "where a.fullIdentifier = ?1  " +
            "  and a.accountStatus not in (" +
            "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED, " +
            "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) ")
    AccountSummary getAccountSummary(String fullIdentifier);

    /**
     * Retrieves a summary of the account.
     * @param registryAccountType The registry account type.
     * @param kyotoAccountType The kyoto account type.
     * @param accountStatus The account status.
     * @return an account summary.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountSummary(" +
           "      a.identifier,              " +
           "      a.registryAccountType,     " +
           "      a.kyotoAccountType,        " +
           "      a.accountStatus,           " +
           "      a.registryCode,            " +
           "      a.fullIdentifier,          " +
           "      a.commitmentPeriodCode,    " +
           "      a.balance,                 " +
           "      a.unitType)                " +
           " from Account a                  " +
           "where a.registryAccountType = ?1 " +
           "  and a.kyotoAccountType    = ?2 " +
           "  and a.accountStatus in      ?3 " +
           "  and a.commitmentPeriodCode = ?4 " +
           "  and a.accountStatus not in (    " +
           "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED, " +
           "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) ")
    List<AccountSummary> getAccountSummary(RegistryAccountType registryAccountType, KyotoAccountType kyotoAccountType,
                                           List<AccountStatus> accountStatus, Integer commitmentPeriod);

    /**
     * Retrieves a summary of the account.
     * @param registryAccountType The registry account type.
     * @param kyotoAccountType The kyoto account type.
     * @param accountStatus The account status.
     * @return an account summary.
     */
    @Query("select new gov.uk.ets.registry.api.transaction.domain.data.AccountSummary(" +
        "      a.identifier,              " +
        "      a.registryAccountType,     " +
        "      a.kyotoAccountType,        " +
        "      a.accountStatus,           " +
        "      a.registryCode,            " +
        "      a.fullIdentifier,          " +
        "      a.commitmentPeriodCode,    " +
        "      a.balance,                 " +
        "      a.unitType)                " +
        " from Account a                  " +
        "where a.registryAccountType = ?1 " +
        "  and a.kyotoAccountType    = ?2 " +
        "  and a.accountStatus       = ?3 ")
    List<AccountSummary> getAccountSummary(RegistryAccountType registryAccountType, KyotoAccountType kyotoAccountType,
                                           AccountStatus accountStatus);

    /**
     * Performs a lock on the provided account.
     * @param identifier The account identifier.
     * @return a number.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a.identifier from Account a where a.identifier = ?1")
    Long lockOnAccount(Long identifier);

    /**
     * Retrieves the account holder identifier of the provided account.
     * @param accountIdentifier The account identifier.
     * @return the account holder identifier.
     */
    @Query(value = "select h.identifier from Account a join a.accountHolder h where a.identifier = ?1")
    Long getAccountHolderIdentifier(Long accountIdentifier);

    /**
     * Retrieves whether the approval of a second Authorised Representative is required.
     * @param accountIdentifier The account identifier.
     * @return false/true
     */
    @Query(value = "select approvalOfSecondAuthorisedRepresentativeIsRequired from Account where identifier = ?1")
    boolean approvalOfSecondAuthorisedRepresentativeIsRequired(Long accountIdentifier);

    /**
     * Retrieves whether transfers for accounts not on the trusted account list are allowed.
     *
     * @param identifier The account identifier.
     * @return false/true
     */
    @Query(value = "select a.transfersToAccountsNotOnTheTrustedListAreAllowed from Account a where a.identifier = ?1")
    boolean transfersToAccountsNotOnTheTrustedListAreAllowed(Long identifier);

    /**
     * Retrieves whether a single person approval is required for specific transactions.
     *
     * @param accountIdentifier The account identifier.
     * @return false/true
     */
    @Query(value = "select singlePersonApprovalRequired from Account where identifier = ?1")
    boolean singlePersonApprovalRequired(Long accountIdentifier);
}
