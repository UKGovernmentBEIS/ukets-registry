package gov.uk.ets.registry.api.tal.repository;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.tal.domain.QTrustedAccount;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountFilter;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.web.model.search.QTALProjection;
import gov.uk.ets.registry.api.tal.web.model.search.TALProjection;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class TALSearchRepositoryImpl implements TALSearchRepository {

    private static final QTrustedAccount trustedAccount = QTrustedAccount.trustedAccount;
    private static final QAccount account = QAccount.account;

    @PersistenceContext
    EntityManager entityManager;

    /**
     * We create an empty sortingMap to ignore the dynamic ordering implemented here
     *
     * @see Search#applyOrder
     * because query ordering for both manually and automatically added trusted
     * accounts cannot be supported by the dynamic ordering
     */
    private Map<String, EntityPathBase<?>> sortingMap = Stream.of(new Object[][] {
    }).collect(Collectors.toMap(
        data -> (String) data[0],
        data -> (EntityPathBase<?>) data[1]));


    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TALProjection> search(TrustedAccountFilter filter, Pageable pageable) {

        OrderSpecifier orderSpecifier = getOrderBy(pageable.getSort(), filter.getUnderSameAccountHolder());

        if (orderSpecifier != null) {
            return new Search.Builder<TALProjection>()
                .pageable(pageable)
                .sortingMap(sortingMap)
                .query(getQuery(filter).orderBy(orderSpecifier))
                .build().getResults();
        } else {
            return new Search.Builder<TALProjection>()
                .pageable(pageable)
                .sortingMap(sortingMap)
                .query(getQuery(filter))
                .build().getResults();
        }
    }

    public JPAQuery<TALProjection> getQuery(TrustedAccountFilter filter) {
        if (Boolean.TRUE.equals(filter.getUnderSameAccountHolder())) {

            JPAQuery<TALProjection> query = new JPAQuery<TALProjection>(entityManager)
                .select(new QTALProjection(
                    account.id,
                    account.fullIdentifier,
                    Expressions.asBoolean(true),
                    account.accountName)
                ).from(account);

            return query.where(new OptionalBooleanBuilder(account.accountHolder.id.eq(JPAExpressions
                .select(account.accountHolder.id)
                .from(account)
                .where(account.identifier.eq(filter.getIdentifier())))
                .and(account.identifier.ne(filter.getIdentifier())).and(account.accountStatus.notIn(
                    AccountStatus.CLOSED)))
                .notNullAnd(account.fullIdentifier::containsIgnoreCase, filter.getAccountFullIdentifier())
                .notNullAnd(account.accountName::containsIgnoreCase, filter.getName())
                .build());

        } else if (Boolean.FALSE.equals(filter.getUnderSameAccountHolder())) {
            JPAQuery<TALProjection> query = new JPAQuery<TALProjection>(entityManager)
                .select(
                    new QTALProjection(
                        trustedAccount.account.id,
                        trustedAccount.trustedAccountFullIdentifier,
                        Expressions.asBoolean(false),
                        trustedAccount.description,
                        trustedAccount.account.accountName,
                        trustedAccount.status,
                        trustedAccount.activationDate
                    )
                ).from(trustedAccount);


            return query.where(new OptionalBooleanBuilder(
                trustedAccount.account.id.eq(JPAExpressions
                    .select(account.id)
                    .from(account)
                    .where(account.identifier.eq(filter.getIdentifier()))).and(trustedAccount.status.in(
                    TrustedAccountStatus.ACTIVE, TrustedAccountStatus.PENDING_ACTIVATION,
                    TrustedAccountStatus.PENDING_ADDITION_APPROVAL, TrustedAccountStatus.PENDING_REMOVAL_APPROVAL)))
                .notNullAnd(trustedAccount.trustedAccountFullIdentifier::containsIgnoreCase,
                    filter.getAccountFullIdentifier())
                .notNullAnd(account.accountName.concat(trustedAccount.description.coalesce(""))::containsIgnoreCase,
                    filter.getName())
                .build());
        } else {

            JPAQuery<TALProjection> subquery = new JPAQuery<TALProjection>(entityManager)
                .select(new QTALProjection(
                    trustedAccount.account.id.coalesce(account.id),
                    trustedAccount.trustedAccountFullIdentifier.coalesce(account.fullIdentifier),
                    new CaseBuilder().when(trustedAccount.trustedAccountFullIdentifier.isNotEmpty()).then(false)
                        .otherwise(true),
                    trustedAccount.description,
                    account.accountName,
                    trustedAccount.status,
                    trustedAccount.activationDate))
                .from(account).leftJoin(trustedAccount)
                .on(account.id.eq(trustedAccount.account.id).and(trustedAccount.status
                    .in(TrustedAccountStatus.ACTIVE, TrustedAccountStatus.PENDING_ACTIVATION,
                        TrustedAccountStatus.PENDING_ADDITION_APPROVAL, TrustedAccountStatus.PENDING_REMOVAL_APPROVAL))
                    .and(account.identifier.eq(filter.getIdentifier())))
                .where(account.accountHolder.id.eq(JPAExpressions
                    .select(account.accountHolder.id)
                    .from(account)
                    .where(account.identifier.eq(filter.getIdentifier()))));

            return subquery.where(new OptionalBooleanBuilder(trustedAccount.trustedAccountFullIdentifier.isNotNull()
                .or(trustedAccount.trustedAccountFullIdentifier.isNull()
                    .and(account.identifier.ne(filter.getIdentifier())).and(account.accountStatus.notIn(
                        AccountStatus.CLOSED))))
                .notNullAnd(trustedAccount.trustedAccountFullIdentifier.coalesce(account.fullIdentifier)
                    ::containsIgnoreCase, filter.getAccountFullIdentifier())
                .notNullAnd(account.accountName.concat(trustedAccount.description.coalesce(""))::containsIgnoreCase,
                    filter.getName()).build());

        }
    }

    private OrderSpecifier<String> getOrderBy(Sort sort, Boolean underSameAccountHolder) {
        Optional<Sort.Order> orderOptional = sort.get().findFirst();

        if (orderOptional.isPresent()) {
            Sort.Order order = orderOptional.get();
            Order direction = order.getDirection().isAscending() ? ASC : DESC;

            if (order.getProperty().equals("accountFullIdentifier")) {
                if (Boolean.TRUE.equals(underSameAccountHolder)) {
                    return new OrderSpecifier<>(direction, account.fullIdentifier);
                } else if (Boolean.FALSE.equals(underSameAccountHolder)) {
                    return new OrderSpecifier<>(direction, trustedAccount.trustedAccountFullIdentifier);
                } else {
                    return new OrderSpecifier<>(direction,
                        trustedAccount.trustedAccountFullIdentifier.coalesce(account.fullIdentifier));
                }
            } else if (order.getProperty().equals("status")) {
                if (!Boolean.TRUE.equals(underSameAccountHolder)) {
                    return new OrderSpecifier<>(direction, trustedAccount.status.stringValue());
                }
            } else if (order.getProperty().equals("underSameAccountHolder") && underSameAccountHolder == null) {
                return new OrderSpecifier<>(direction,
                    new CaseBuilder().when(trustedAccount.trustedAccountFullIdentifier.isNotEmpty()).then(false)
                        .otherwise(true).stringValue());
            }
        }
        return null;
    }
}
