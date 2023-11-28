package gov.uk.ets.registry.api.account.repository;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountAccessDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Repository for account accesses.
 */
public interface AccountAccessRepository
    extends JpaRepository<AccountAccess, Long>, QuerydslPredicateExecutor<AccountAccess> {

    /**
     * Finds the accesses on the provided account.
     *
     * @param identifier The account identifier.
     * @return the accesses of the account
     */
    @SuppressWarnings("java:S100")
    @Query(value =
        "select a from AccountAccess a " +
            "where a.account.identifier= ?1 " +
            "and a.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED")
    List<AccountAccess> finARsByAccount_Identifier(Long identifier);

    /**
     * Return ALL AccountAccesses.
     */
    AccountAccess findByAccount_IdentifierAndUser_Urid(Long identifier, String urid);

    /**
     * Return only AR related AccountAccesses.
     */
    @Query(value =
        "select a from AccountAccess a " +
            "left join fetch a.user u " +
            "left join fetch a.account ac " +
            "left join fetch ac.accountHolder ah " +
            "where a.account.identifier = ?1 " +
            "and u.urid= ?2 " +
            "and a.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED")
    AccountAccess findArsByAccount_IdentifierAndUser_Urid(Long identifier, String urid);

    List<AccountAccess> findByUser_Urid(String urid);

    /**
     * Same as above, but fetches accounts.
     */
    @EntityGraph(attributePaths = {"account"})
    List<AccountAccess> getByUser_Urid(String urid);

    @Query(value =
        "select a from AccountAccess a " +
            "left join fetch a.user u " +
            "left join fetch a.account ac " +
            "where u.urid = ?1 " +
            "and a.state in ?2")
    List<AccountAccess> findArsByUridAndStateInWithAccount(String urid, List<AccountAccessState> stateList);

    /**
     * Finds all the account accesses of the specific user
     *
     * @param urid The user identifier
     * @return All the access of the user
     */
    @Query(value =
        "select a from AccountAccess a " +
            "left join fetch a.user u " +
            "left join fetch a.account ac " +
            "left join fetch ac.accountHolder ah " +
            "where u.urid= ?1 " +
            "and a.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED")
    List<AccountAccess> findARsInAccountByUser(String urid);

    /**
     * Finds the account accesses of the specific user
     *
     * @param urid            The user identifier
     * @param accountStatuses The account statuses that should not be displayed
     * @return All the access of the user
     */
    @Query(value =
        "select a from AccountAccess a " +
            "left join fetch a.user u " +
            "left join fetch a.account ac " +
            "left join fetch ac.accountHolder ah " +
            "where u.urid= ?1 " +
            "and ac.accountStatus not in ?2 " +
            "and a.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED")
    List<AccountAccess> findARsInAccountByUser(String urid, List<AccountStatus> accountStatuses);

    /**
     * Finds all the accesses by user iam identifier.
     *
     * @param iamIdentifier
     * @return
     */
    @Query(value = "select a from AccountAccess a left join fetch a.user u where u.iamIdentifier= ?1")
    List<AccountAccess> findByUserIamIdentifier(String iamIdentifier);

    /**
     * Finds all the accesses for users of the accounts related with user iam identifier excluding the specified user.
     *
     * @param iamIdentifier
     * @return
     */
    @Query(value = "select b from AccountAccess a "
        + "inner join AccountAccess b on "
        + "a.account.id=b.account.id "
        + "inner join a.user u inner join fetch b.user r "
        + "where u.iamIdentifier= ?1 and "
        + "u.id <> r.id " +
        "and a.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED " +
        "and b.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED")
    List<AccountAccess> findRelatedARsByUserIamIdentifier(String iamIdentifier);

    /**
     * Retrieves all account accesses.
     *
     * @return A list of BulkArAccountAccessDTO objects.
     */
    @Query(value = "select new gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountAccessDTO(" +
        "aa.user.urid, " +
        "aa.account.fullIdentifier, " +
        "aa.state) from AccountAccess aa " +
        "where aa.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED")
    List<BulkArAccountAccessDTO> retrieveAccountAccesses();
    
    /**
     * Deletes the accesses for the specified user and access right.
     * @param user the user whose accesses should be deleted
     * @param right the AccountAccessRight to delete
     * @return the number of rows deleted
     */
    Long deleteByUserAndRight(User user,AccountAccessRight right);
}
