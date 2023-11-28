package gov.uk.ets.registry.api.account.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccountHolder;
import gov.uk.ets.registry.api.account.domain.QAircraftOperator;
import gov.uk.ets.registry.api.account.domain.QCompliantEntity;
import gov.uk.ets.registry.api.account.domain.QInstallation;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.shared.AccountPropertyPath;
import gov.uk.ets.registry.api.account.shared.QAccountProjection;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.compliance.domain.QExcludeEmissionsEntry;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.user.domain.QUser;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Implementation of custom repository of search account.
 */
public class AccountSearchRepositoryImpl implements AccountSearchRepository {

    private static final QAccount account = QAccount.account;
    private static final QAccountHolder holder = QAccountHolder.accountHolder;
    private static final QCompliantEntity compliantEntity = QCompliantEntity.compliantEntity;
    private static final QAccountAccess accountAccess = QAccountAccess.accountAccess;
    private static final QUser user = QUser.user;
    private static final QExcludeEmissionsEntry excludeEmissionsEntry = QExcludeEmissionsEntry.excludeEmissionsEntry;


    @PersistenceContext
    EntityManager entityManager;

    private Map<String, EntityPathBase<?>> sortingMap = Stream.of(new Object[][] {
        {AccountPropertyPath.ACCOUNT_FULL_IDENTIFIER, account},
        {AccountPropertyPath.ACCOUNT_NAME, account},
        {AccountPropertyPath.ACCOUNT_TYPE_LABEL, account},
        {AccountPropertyPath.ACCOUNT_HOLDER_NAME, holder},
        {AccountPropertyPath.ACCOUNT_STATUS, account},
        {AccountPropertyPath.ACCOUNT_COMPLIANCE_STATUS, account},
        {AccountPropertyPath.ACCOUNT_BALANCE, account}
    }).collect(Collectors.toMap(
        data -> (String) data[0],
        data -> (EntityPathBase<?>) data[1]));

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<AccountProjection> search(AccountFilter filter, Pageable pageable) {
        return new Search.Builder<AccountProjection>()
            .pageable(pageable)
            .sortingMap(sortingMap)
            .query(getQuery(filter))
            .build().getResults();
    }

    public JPAQuery<AccountProjection> getQuery(AccountFilter filter) {
        JPAQuery<AccountProjection> query = new JPAQuery<AccountProjection>(entityManager)
            .select(createAccountProjection()).from(account);

        query = filter.getAccountHolderName() != null ?
            query.innerJoin(account.accountHolder, holder)
                 .on(getAccountHolderNameCondition(filter.getAccountHolderName())) :
            query.leftJoin(account.accountHolder, holder);

        if (filter.getRegulatorType() != null ||
            filter.getPermitOrMonitoringPlanIdentifier() != null ||
            filter.getAllocationClassification() != null ||
            filter.getAllocationWithholdStatus() != null ||
            filter.getInstallationOrAircraftOperatorId() != null) {
            query = query.innerJoin(account.compliantEntity, compliantEntity)
                .on(new OptionalBooleanBuilder(account.isNotNull())
                    .notNullAnd(compliantEntity.regulator::eq, filter.getRegulatorType())
                    .notNullAnd(this::getPermitOrMonitoringPlanIdCondition,
                        filter.getPermitOrMonitoringPlanIdentifier())
                    .notNullAnd(this::getAllocationClassificationCondition, filter.getAllocationClassification())
                    .notNullAnd(compliantEntity.allocationWithholdStatus::eq, filter.getAllocationWithholdStatus())
                    .notNullAnd(compliantEntity.identifier.stringValue()::contains,
                        filter.getInstallationOrAircraftOperatorId())
                    .build());
        } else {
            query = query.leftJoin(account.compliantEntity, compliantEntity);
        }

        query = query.innerJoin(account.accountAccesses, accountAccess)
            .innerJoin(accountAccess.user, user)
            .on(new OptionalBooleanBuilder(
                getAuthorizedRepresentativeCondition(filter.getAuthorizedRepresentativeUrid()))
                .notNullAnd(accountAccess.state::notIn, filter.getExcludedAccessStates())
                .notNullAnd(this::getAccessRightCondition, filter.hasLimitedScope())
                .build()
            );

        if (filter.getExcludedForYear() != null) {
            query = query.innerJoin(excludeEmissionsEntry)
                .on(
                    compliantEntity.identifier.eq(excludeEmissionsEntry.compliantEntityId)
                );
        }

        return query.where(new OptionalBooleanBuilder(account.isNotNull())
            .notNullAnd(this::getAccountNameOrIdCondition, filter.getAccountFullIdentifierOrName())
            .notNullAnd(this::getAccountsOfAuthorizedUserCondition, filter.getAccountFullIdentifiers())
            .notNullAnd(account.accountStatus::in, filter.getAccountStatuses())
            .notNullAnd(account.accountStatus::notIn, filter.getExcludedAccountStatuses())
            .notNullAnd(this::getAccountTypeCondition, filter.getAccountTypes())
            .notNullAnd(account.complianceStatus::eq, filter.getComplianceStatus())
            .notNullAnd(this::getExcludedCondition, filter.getExcludedForYear())
            .build());
    }


    private QAccountProjection createAccountProjection() {
        return new QAccountProjection(
            account.id,
            holder.id,
            account.accountName,
            account.accountStatus,
            account.accountType,
            account.approvalOfSecondAuthorisedRepresentativeIsRequired,
            account.balance,
            account.billingAddressSameAsAccountHolderAddress,
            account.checkDigits,
            account.commitmentPeriodCode,
            account.complianceStatus,
            account.compliantEntity.id,
            account.contact.id,
            account.fullIdentifier,
            account.identifier,
            account.kyotoAccountType,
            account.openingDate,
            account.registryAccountType,
            account.registryCode,
            account.status,
            account.transfersToAccountsNotOnTheTrustedListAreAllowed,
            account.unitType,
            holder.birthCountry,
            holder.birthDate,
            holder.contact.id,
            holder.firstName,
            holder.identifier,
            holder.lastName,
            holder.name,
            holder.noRegNumjustification,
            holder.registrationNumber,
            holder.type,
            compliantEntity.regulator
        );
    }

    private BooleanExpression getAccessRightCondition(boolean hasLimitedScope) {
        if (hasLimitedScope) {
            return accountAccess.right.ne(AccountAccessRight.ROLE_BASED);
        }
        return accountAccess.right.eq(AccountAccessRight.ROLE_BASED);
    }

    private BooleanExpression getAccountHolderNameCondition(String term) {
        BooleanExpression nameExpression = account.accountHolder.name.trim().likeIgnoreCase("%" + term + "%");
        BooleanExpression firstNameLastNamePredicate = account.accountHolder.firstName.trim()
            .concat(account.accountHolder.lastName.trim())
            .containsIgnoreCase(term.replaceAll("\\s", ""));

        BooleanExpression lastNameFirstNamePredicate = account.accountHolder.lastName.trim()
            .concat(account.accountHolder.firstName.trim())
            .containsIgnoreCase(term.replaceAll("\\s", ""));

        return nameExpression.or(firstNameLastNamePredicate).or(lastNameFirstNamePredicate);
    }

    private BooleanExpression getAccountTypeCondition(List<AccountType> accountTypes) {
        return accountTypes.stream().map(accountType ->
            account.registryAccountType.eq(accountType.getRegistryType())
                .and(account.kyotoAccountType.eq(accountType.getKyotoType()))
        ).reduce(account.isNull(), (e1, e2) -> e1.or(e2));
    }

    private BooleanExpression getPermitOrMonitoringPlanIdCondition(String identifier) {
        return compliantEntity.as(QInstallation.class).permitIdentifier.containsIgnoreCase(identifier)
            .or(compliantEntity.as(QAircraftOperator.class).monitoringPlanIdentifier.containsIgnoreCase(identifier));
    }

    private BooleanExpression getAccountNameOrIdCondition(String accountIdOrName) {
        String term = "%" + accountIdOrName + "%";
        return account.fullIdentifier.likeIgnoreCase(term).or(account.accountName.likeIgnoreCase(term));
    }

    private BooleanExpression getAuthorizedRepresentativeCondition(String urid) {
        return user.urid.containsIgnoreCase(urid);
    }

    private BooleanExpression getAccountsOfAuthorizedUserCondition(Set<String> accountIds) {
        return account.fullIdentifier.in(accountIds);
    }

    private BooleanExpression getAllocationClassificationCondition(AllocationClassification allocationClassification) {
        if (AllocationClassification.NOT_YET_ALLOCATED.equals(allocationClassification)) {
            return compliantEntity.allocationClassification.eq(allocationClassification);
        }
        return compliantEntity.allocationClassification.in(allocationClassification);
    }


    private BooleanExpression getExcludedCondition(Long year) {
        return excludeEmissionsEntry.excluded.eq(true).and(excludeEmissionsEntry.year.eq(year));
    }
}
