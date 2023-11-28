package gov.uk.ets.registry.api.ar.infrastructure;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ARAccountAccessRepositoryImpl implements ARAccountAccessRepository {
    private static final QAccount account = QAccount.account;
    private static final QAccountAccess accountAccess = QAccountAccess.accountAccess;

    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AccountAccess> fetchARs(Long accountIdentifier, AccountAccessState accountAccessState) {
        return new JPAQuery<AccountAccess>(entityManager).from(accountAccess)
            .join(accountAccess.account, account)
            .on(new OptionalBooleanBuilder(account.identifier.eq(accountIdentifier))
                .notNullAnd(accountAccess.state::eq, accountAccessState)
                .build())
            .where(
                accountAccess.state.ne(AccountAccessState.REMOVED)
                    .and(accountAccess.right.ne(AccountAccessRight.ROLE_BASED))
            )
            .innerJoin(accountAccess.user)
            .fetchJoin()
            .fetch();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AccountAccess> fetchArsForAccount(long accountIdentifier, String urid) {

        return entityManager.createQuery("""
             select acs
             from AccountAccess acs
                  join acs.user u1
                  join acs.account acc0
                  join acc0.accountHolder hol
             where (
                 (:urid is null
                    and hol.id = (
                            select acc1.accountHolder.id
                            from Account acc1
                            where acc1.identifier = :accountIdentifier
                        )
                 )
                  or
                 (:urid is not null
                    and hol.id in (
                        select acc1.accountHolder.id from Account acc1 where acc1.id in (
                            select acc2.id from AccountAccess acs2
                                join acs2.account acc2
                                join acs2.user u2
                             where u2.urid = :urid
                        )
                    )
                 )
             )
             and acs.user.id not in (
                select acs3.user.id from AccountAccess acs3 where acs3.account.identifier = :accountIdentifier
            )
            and hol.type <> gov.uk.ets.registry.api.account.domain.types.AccountHolderType.GOVERNMENT
            and acs.state = gov.uk.ets.registry.api.account.domain.types.AccountAccessState.ACTIVE
            and acs.right <> gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.ROLE_BASED
            and u1.state <> gov.uk.ets.registry.api.user.domain.UserStatus.SUSPENDED
            and u1.state <> gov.uk.ets.registry.api.user.domain.UserStatus.DEACTIVATED
            and u1.state <> gov.uk.ets.registry.api.user.domain.UserStatus.DEACTIVATION_PENDING """,
                AccountAccess.class)
            .setParameter("urid", urid)
            .setParameter("accountIdentifier", accountIdentifier)
            .getResultList();
    }

}
