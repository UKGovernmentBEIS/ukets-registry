package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.web.model.InstallationSearchResultDTO;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

/**
 * Data repository for accounts.
 */
public interface AccountRepository
    extends JpaRepository<Account, Long>, AccountSearchRepository, QuerydslPredicateExecutor<Account> {

    /**
     * Retrieves an account based on its business identifier.
     *
     * @param identifier the business identifier
     * @return an account.
     */
    Optional<Account> findByIdentifier(Long identifier);

    @Query(value = "select a from Account a left join fetch a.accountHolder where a.identifier = ?1 ")
    Optional<Account> findByIdentifierWithAccountHolder(Long identifier);

    /**
     * Retrieves an account based on its full identifier.
     *
     * @param fullIdentifier the account full identifier
     * @return an account.
     */
    Optional<Account> findByFullIdentifier(String fullIdentifier);

    /**
     * Retrieves the next business identifier value.
     *
     * @return the next business identifier value.
     */
    @Query(value = "select nextval('account_identifier_seq')", nativeQuery = true)
    Long getNextIdentifier();

    /**
     * Updates the request status of the provided account.
     *
     * @param identifier The unique business identifier.
     * @param status     The new status.
     * @return the number of records updated.
     */
    @Modifying
    @Query("update Account a set a.status = ?2 where a.identifier = ?1")
    int updateRequestStatus(Long identifier, Status status);

    /**
     * Updates the business status of the provided account.
     *
     * @param identifier    The unique business identifier.
     * @param accountStatus The new account status.
     * @return the number of records updated.
     */
    @Modifying
    @Query("update Account a set a.accountStatus = ?2 where a.identifier = ?1")
    int updateAccountStatus(Long identifier, AccountStatus accountStatus);

    /**
     * Returns a count of AircraftOperators found for the specific monitoringPlanIdentifier and the account is not closed
     *
     * @param monitoringPlanId The aircraft monitoring plan Identifier
     * @return A Count of monitoringPlanIdentifiers found
     */
    @Query(value =
        "select count(aircraftOperator) from Account account left join treat(account.compliantEntity as AircraftOperator) aircraftOperator" +
            " where upper(aircraftOperator.monitoringPlanIdentifier) = ?1" +
            " and account.accountStatus <> 'CLOSED'")
    Long getAircraftMonitoringPlanIdsForNonClosedAccounts(String monitoringPlanId);

    /**
     * Returns a count of MaritimeOperators found for the specific maritimeMonitoringPlanIdentifier and the account is not closed
     *
     * @param maritimeMonitoringPlanId The maritime monitoring plan Identifier
     * @return A Count of maritimeMonitoringPlanIdentifiers found
     */
    @Query(value =
        "select count(maritimeOperator) from Account account left join treat(account.compliantEntity as MaritimeOperator) maritimeOperator" +
            " where upper(maritimeOperator.maritimeMonitoringPlanIdentifier) = ?1" +
            " and account.accountStatus <> 'CLOSED'")
    Long getMaritimeMonitoringPlanIdsForNonClosedAccounts(String maritimeMonitoringPlanId);

    /**
     * Returns a count of Maritime Operators found for the specific IMO and the account is not closed
     *
     * @param imo Company IMO number
     * @return A Count of imos found
     */
    @Query(value =
        "SELECT COUNT(*) " +
            "FROM Account account " +
            "LEFT JOIN TREAT(account.compliantEntity AS MaritimeOperator) maritimeOperator " +
            "WHERE maritimeOperator.imo = ?1 " +
            "AND account.accountStatus <> 'CLOSED'")
    Long countMaritimeIMOsForNonClosedAccounts(String imo);

    boolean existsByCompliantEntity_EmitterIdAndAccountStatusNot(String emitterId, AccountStatus status);

    @Query(value =
            "SELECT COUNT(a) > 0 " +
                    "FROM Account a " +
                    "LEFT JOIN TREAT(a.compliantEntity AS CompliantEntity) operator " +
                    "WHERE operator.identifier <> :operatorIdentifier " +
                    "AND operator.emitterId = :emitterId " +
                    "AND a.accountStatus <> :status")
    boolean isExistingEmitterId(@Param("operatorIdentifier") Long operatorIdentifier,
                                @Param("emitterId") String emitterId,
                                @Param("status") AccountStatus status);
    /**
     * Returns a list of Installations found for the specific Identifier and the account is not closed or rejected.
     *
     * @param installationId The installation Identifier
     * @return A list of installations found
     */
    @Query(value =
            "select new gov.uk.ets.registry.api.account.web.model.InstallationSearchResultDTO ( installation.identifier, installation.permitIdentifier, installation.installationName, installation.emitterId ) from Account account inner join " +
                    "treat(account.compliantEntity as Installation ) installation " +
                    "where cast(installation.identifier as string) like ?1% "+
                    "and account.accountStatus not in ( " +
                    "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED, " +
                    "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED, " +
                    "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.CLOSED) " +
    				"and (?2 is null or account.accountHolder.identifier != ?2) ")
    List<InstallationSearchResultDTO> installationsByIdentifierForNonClosedOrRejectedAccounts(String installationId, Long excludeAccountHolderIdentifier);

    /**
     * Returns a count of Installation permitIds found for the specific permit Identifier and the account is not closed or rejected.
     *
     * @param installationPermitId The installation permit Identifier
     * @return A Count of installation permitIds found
     */
    @Query(value =
        "select count(installation) from Account account left join treat(account.compliantEntity as Installation) installation" +
            " where upper(installation.permitIdentifier) = ?1" +
            " and account.accountStatus NOT IN (gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.CLOSED, " +
            " gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) ")
    Long countInstallationsByPermitIdForNonClosedOrRejectedAccounts(String installationPermitId);

    @Query("select a from Account a " +
        "where a.registryAccountType= ?1 " +
        "and a.accountStatus <> gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.CLOSED")
    Optional<Account> findByRegistryAccountTypeForNonClosedAccounts(RegistryAccountType type);

    /**
     * Returns a count of accounts with the same Account Holder identifier.
     *
     * @param identifier the Account Holder identifier
     * @return A count of accounts found
     */
    List<Account> getAccountsByAccountHolder_IdentifierEquals(Long identifier);

    /**
     * Retrieves the accounts.
     *
     * @return A list of BulkArAccountDTO objects.
     */
    @Query(value = "select new gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO( " +
        "a.fullIdentifier, " +
        "a.accountName, " +
        "a.accountStatus, " +
        "a.registryAccountType, " +
        "a.kyotoAccountType) from Account a")
    List<BulkArAccountDTO> retrieveAccounts();

    @Query("select new gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO( " +
            "a.fullIdentifier, " +
            "a.accountName, " +
            "a.accountStatus, " +
            "a.registryAccountType, " +
            "a.kyotoAccountType) from Account a " +
            " where a.kyotoAccountType in (?1) " +
            " and a.registryAccountType = gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType.NONE "
    )
    List<BulkArAccountDTO> findKyotoAccountIdentifiers(List<KyotoAccountType> types);

    @Query("select new gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO( " +
            "a.fullIdentifier, " +
            "a.accountName, " +
            "a.accountStatus, " +
            "a.registryAccountType, " +
            "a.kyotoAccountType) from Account a " +
            " where a.registryAccountType in (?1) "
    )
    List<BulkArAccountDTO> findRegistryAccountIdentifiers(List<RegistryAccountType> types);

    @Query(value = "select a from Account a  join fetch a.compliantEntity c where c.identifier = ?1")
    Optional<Account> findByCompliantEntityIdentifier(Long identifier);

    @Query("""
        select a from Account a
        join fetch a.compliantEntity c
        left join fetch a.metsAccountContacts contacts
        left join fetch contacts.contactTypes
        where c.identifier = :identifier
    """)
    Optional<Account> findByCompliantEntityIdentifierWithContacts(Long identifier);

    @Query(value = "select a from Account a  join fetch a.compliantEntity c where a.identifier = ?1")
    Optional<Account> findByAccountIdentifierWithCompliantEntity(Long identifier);

    @Query("select a                            " +
        "  from Account a                       " +
        "  where a.accountHolder.id = (         " +
        "       select acc.accountHolder.id     " +
        "         from Account acc              " +
        "        where acc.identifier = ?1)     " +
        "   and a.identifier <> ?1              " +
        "   and a.accountStatus not in (        " +
        "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED, " +
        "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.CLOSED, " +
        "      gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) ")
    List<Account> findAccountsOfTheSameAccountHolder(Long identifier);

    List<Account> findAccountByAccountStatus(AccountStatus accountStatus);

    @Query("select a from Account a join fetch a.compliantEntity " +
        "where a.accountStatus not in (" +
        "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED," +
        "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED," +
        "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.CLOSED)"
    )
    List<Account> findActiveAccountsWithCompliantEntities();

    @Query("select a from Account a join fetch a.compliantEntity " +
            "where a.accountStatus not in (" +
            "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.PROPOSED," +
            "gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) " +
            " and a.registryAccountType in (gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT," +
            " gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType.OPERATOR_HOLDING_ACCOUNT) " +
            " and a.kyotoAccountType in (gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType.PARTY_HOLDING_ACCOUNT)"
    )
    List<Account> findOHA_AOHAAccountsWithCompliantEntities();

    /**
     * It joins the Account, AccountAccess and User entities
     * to combine an assocciated fullIdentifier for the account
     * with a given urid
     * @param urid: The urid to be searched
     * @return fullIdentifier
     */
    @Query( "select distinct a.fullIdentifier " +
            "from Account a " +
            "inner join AccountAccess aa " +
            "on a.id = aa.account.id " +
            "inner join User u " +
            "on u.id = aa.user.id " +
            "and aa.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED " +
            "and aa.state in (gov.uk.ets.registry.api.account.domain.types.AccountAccessState.ACTIVE, " +
            "gov.uk.ets.registry.api.account.domain.types.AccountAccessState.SUSPENDED)" +
            "where u.urid = :urid"
    )
    Set<String> findAccountsByUrId(@Param("urid") String urid);

    /**
     * Updates the request status of the provided account.
     *
     * @param identifier The unique business identifier.
     * @param status     The new status.
     * @return the number of records updated.
     */
    @Modifying
    @Query("update Account set excludedFromBilling = ?2, excludedFromBillingRemarks = ?3 where identifier = ?1")
    void updateExcludedFromBilling(Long identifier, boolean excludedFromBilling, String exclusionRemarks);

    boolean existsByPublicIdentifier(String ukPid);

    List<Account> findByPublicIdentifierIsNull();

    @Query("""
    SELECT (COUNT(a) > 0)
    FROM Account a
    LEFT JOIN TREAT(a.compliantEntity AS MaritimeOperator) c
    WHERE c.identifier = :registryId
      AND (
            c.imo = :imo
            OR NOT EXISTS (
                SELECT 1
                FROM MaritimeOperator ce
                WHERE ce.imo = :imo
                  AND ce <> c
            )
          )
    """)
    boolean isImoAvailableForAccount(
            @Param("registryId") Long registryId,
            @Param("imo") String imo
    );

    @Query("""
    SELECT (COUNT(a) > 0)
    FROM Account a
    LEFT JOIN TREAT(a.compliantEntity AS MaritimeOperator) c
    WHERE c.identifier = :registryId
      AND (
            c.maritimeMonitoringPlanIdentifier = :monitoringPlanId
            OR NOT EXISTS (
                SELECT 1
                FROM MaritimeOperator ce
                WHERE ce.maritimeMonitoringPlanIdentifier = :monitoringPlanId
                  AND ce <> c
            )
          )
    """)
    boolean isMaritimeMonitoringPlanAvailableForAccount(
            @Param("registryId") Long registryId,
            @Param("monitoringPlanId") String monitoringPlanId
    );

    @Query("""
    SELECT (COUNT(a) > 0)
    FROM Account a
    LEFT JOIN TREAT(a.compliantEntity AS AircraftOperator) c
    WHERE c.identifier = :registryId
      AND (
            c.monitoringPlanIdentifier = :monitoringPlanId
            OR NOT EXISTS (
                SELECT 1
                FROM AircraftOperator ce
                WHERE ce.monitoringPlanIdentifier = :monitoringPlanId
                  AND ce <> c
            )
          )
    """)
    boolean isMonitoringPlanAvailableForAccount(
            @Param("registryId") Long registryId,
            @Param("monitoringPlanId") String monitoringPlanId
    );

    @Query("""
        SELECT (COUNT(a) > 0)
        FROM Account a
        JOIN Installation c ON c = a.compliantEntity
        WHERE c.identifier = :installationId
          AND (
                c.permitIdentifier = :permitId
                OR NOT EXISTS (
                    SELECT 1
                    FROM Installation ce
                    WHERE ce.permitIdentifier = :permitId
                      AND ce <> c
                )
              )
        """)
    boolean isPermitAvailableForAccount(
            @Param("installationId") Long installationId,
            @Param("permitId") String permitId
    );

    @Query("""
        SELECT ah.type
        FROM Account a
        JOIN a.accountHolder ah
        WHERE a.identifier = :registryId
    """)
    AccountHolderType findAccountHolderTypeByAccountId(@Param("registryId") Long registryId);

    /**
     * Retrieves an account based on its operator identifier(Registry Id) and Account claim code .
     *
     * @param operatorIdentifier the operator identifier of the account
     * @param accountClaimCode the account claim code
     * @return an account.
     */
    @Query(value =
            "SELECT a " +
                    "FROM Account a " +
                    "JOIN FETCH a.compliantEntity c " +
                    "WHERE c.identifier = :operatorIdentifier " +
                    "AND a.accountClaimCode = :accountClaimCode")
    Optional<Account> findByCompliantEntityIdentifierAndAccountClaimCode(@Param("operatorIdentifier") Long operatorIdentifier,
                                @Param("accountClaimCode") String accountClaimCode);

    List<Account> findByAccountClaimCodeIsNull();
}
