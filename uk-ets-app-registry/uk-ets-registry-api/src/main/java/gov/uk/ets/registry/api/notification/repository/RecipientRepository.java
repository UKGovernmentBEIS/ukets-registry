package gov.uk.ets.registry.api.notification.repository;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.user.domain.QIamUserRole;
import gov.uk.ets.registry.api.user.domain.QUser;
import gov.uk.ets.registry.api.user.domain.QUserRoleMapping;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.List;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Repository responsible for bringing in all the users related to an account and having a specific status.
 */
@Repository
@RequiredArgsConstructor
public class RecipientRepository {
    private static final QAccount account = QAccount.account;
    private static final QUser user = QUser.user;
    private static final QAccountAccess accountAccess = QAccountAccess.accountAccess;
    private static final QUserRoleMapping roleMapping = QUserRoleMapping.userRoleMapping;
    private static final QIamUserRole iamUserRole = QIamUserRole.iamUserRole;

    private final EntityManager entityManager;

    /**
     * Return all the non un-enrolled authorised representatives of the account.
     *
     * @param accountId             the account id to use for the query.
     * @param includeAuthorityUsers
     * @return a list of @{@link User} objects.
     */
    public List<User> getNotificationRecipientsOfAccount(Long accountId, boolean includeAuthorityUsers) {
        JPAQuery<User> query = new JPAQuery<User>(entityManager)
            .select(user)
            .distinct()
            .from(accountAccess)
            .innerJoin(accountAccess.account, account)
            .innerJoin(accountAccess.user, user);
        // if we want to exclude authority users, we need to fetch user roles here:
        if (!includeAuthorityUsers) {
            query.innerJoin(user.userRoles, roleMapping).fetchJoin()
                .innerJoin(roleMapping.role, iamUserRole).fetchJoin();
        }
        return query
            .where(
                user.state.notIn(UserStatus.UNENROLLED, UserStatus.REGISTERED,
                        UserStatus.VALIDATED, UserStatus.DEACTIVATION_PENDING, UserStatus.DEACTIVATED)
                    .and(accountAccess.state.eq(AccountAccessState.ACTIVE))
                    .and(accountAccess.right.ne(AccountAccessRight.ROLE_BASED))
                    .and(account.identifier.eq(accountId))
            )
            .fetch();
    }
}
