package gov.uk.ets.registry.api.transaction.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountAccess;
import gov.uk.ets.registry.api.account.domain.QAccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.common.search.SearchUtils;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.QTaskTransaction;
import gov.uk.ets.registry.api.transaction.common.FullAccountIdentifierParser;
import gov.uk.ets.registry.api.transaction.domain.QAccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.QAccountProjection;
import gov.uk.ets.registry.api.transaction.domain.QSearchableTransaction;
import gov.uk.ets.registry.api.transaction.domain.QTransaction;
import gov.uk.ets.registry.api.transaction.domain.QTransactionAccountBalance;
import gov.uk.ets.registry.api.transaction.domain.QTransactionConnection;
import gov.uk.ets.registry.api.transaction.domain.QTransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionConnectionType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.shared.TransactionPropertyPath;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchAliases;
import gov.uk.ets.registry.api.user.domain.QUser;

public class TransactionProjectionRepositoryImpl implements TransactionProjectionRepository {
    /**
     * The persistence context.
     */
    @PersistenceContext
    EntityManager entityManager;

    private static final QUser relatedToAccountAccess = new QUser("relatedToAccountAccess");
    private static final QSearchableTransaction transaction = QSearchableTransaction.searchableTransaction;
    private static final QAccount ukRegistryTransferringAccount = new QAccount("transferring");
    private static final QAccount ukRegistryAcquiringAccount = new QAccount("acquiring");
    private static final QAccountHolder ukRegistryTransferringAccountHolder =
        new QAccountHolder("transferringAccountHolder");
    private static final QAccountHolder ukRegistryAcquiringAccountHolder = new QAccountHolder("acquiringAccountHolder");
    private static final QTask task = new QTask("taskOfTransaction");
    private static final QTaskTransaction taskTransaction = new QTaskTransaction("taskOfTransactionRelation");
    private static final QAccountAccess transferringUkRegistryAccountAccess =
        new QAccountAccess("transferringAccountAccess");
    private static final QAccountAccess acquiringUkRegistryAccountAccess = new QAccountAccess("acquiringAccountAccess");
    private static final QTransactionAccountBalance transactionAccountBalance = 
        QTransactionAccountBalance.transactionAccountBalance;
    private static final QTransactionConnection reversesTransactionConnectionsOfOriginal =
  		  new QTransactionConnection("reversesTransactionConnectionsOfOriginal");
    private static final QTransaction reversedBy = new QTransaction("reversedBy");
    private static final QTransactionConnection reversesTransactionConnectionOfReversal =
  		  new QTransactionConnection("reversesTransactionConnectionOfReversal");
    private static final QTransaction reverses = new QTransaction("reverses");
    private static final QTransactionConnection reversesTransactionConnections =
  		  new QTransactionConnection("reversesTransactionConnections");
    private static final QTransaction reversalTransaction = new QTransaction("reversalTransaction");
    private static final QAccountProjection transferringAccountProjection = new QAccountProjection(
        transaction.transferringAccount.accountFullIdentifier,
        ukRegistryTransferringAccount.accountName,
        transaction.transferringAccount.accountType,
        ukRegistryTransferringAccount.registryAccountType,
        ukRegistryTransferringAccountHolder.name,
        ukRegistryTransferringAccount.identifier,
        ukRegistryTransferringAccount.accountType,
        ukRegistryTransferringAccount.accountStatus);

    private static final QAccountProjection acquiringAccountProjection = new QAccountProjection(
        transaction.acquiringAccount.accountFullIdentifier,
        ukRegistryAcquiringAccount.accountName,
        transaction.acquiringAccount.accountType,
        ukRegistryAcquiringAccount.registryAccountType,
        ukRegistryAcquiringAccountHolder.name,
        ukRegistryAcquiringAccount.identifier,
        ukRegistryAcquiringAccount.accountType,
        ukRegistryAcquiringAccount.accountStatus
    );

    private static Map<String, EntityPathBase<?>> sortingMap = Stream.of(new Object[][] {
        {TransactionPropertyPath.TRANSACTION_IDENTIFIER, transaction},
        {TransactionPropertyPath.TRANSACTION_QUANTITY, transaction},
        {TransactionPropertyPath.TRANSACTION_LAST_UPDATED, transaction},
        {TransactionPropertyPath.TRANSACTION_STATUS, transaction},
        {TransactionPropertyPath.TRANSACTION_TRANSFERRING_ACCOUNT, ukRegistryTransferringAccount},
        {TransactionPropertyPath.TRANSACTION_ACQUIRING_ACCOUNT, ukRegistryAcquiringAccount}
    }).collect(Collectors.toMap(
        data -> (String) data[0],
        data -> (EntityPathBase<?>) data[1]));

    private static Map<String, Class<?>> aliasMap = Map.of(
        TransactionSearchAliases.RUNNING_BALANCE_QUANTITY.getAlias(),Long.class,
        TransactionSearchAliases.RUNNING_BALANCE_UNIT_TYPE.getAlias(),UnitType.class
        );
    
    @Override
    public Page<TransactionProjection> search(TransactionFilter filter, Pageable pageable) {
        return new Search.Builder<TransactionProjection>().query(getQuery(filter))
            .pageable(pageable)
            .sortingMap(sortingMap)
            .aliasesMap(aliasMap)
            .build()
            .getResults();
    }

    public JPAQuery<TransactionProjection> getQuery(TransactionFilter filter, Pageable pageable) {

        return new Search.Builder<TransactionProjection>().query(getQuery(filter))
            .pageable(pageable)
            .sortingMap(sortingMap)
            .aliasesMap(aliasMap)
            .build()
            .getQuery();
    }

    private JPAQuery<TransactionProjection> getQuery(TransactionFilter filter) {
  
        QTransactionProjection transactionProjection = null;
        //When we display the transactions of a specific account
        Optional<Long> transferringAccountNumberOpt = 
            FullAccountIdentifierParser.getInstance(filter.getTransferringAccountNumber()).getIdentifier();
        Optional<Long> acquiringAccountNumberOpt = 
            FullAccountIdentifierParser.getInstance(filter.getAcquiringAccountNumber()).getIdentifier();
        if (filter.isShowRunningBalances() && 
            transferringAccountNumberOpt.isPresent() &&
            acquiringAccountNumberOpt.isPresent() &&
            Objects.equals(filter.getTransferringAccountNumber(), filter.getAcquiringAccountNumber())) {
            
            Expression<Long> accountBalanceCases = new CaseBuilder()
                .when(transactionAccountBalance.isNull()
                    .and(transaction.status.notIn(TransactionStatus.getFinalStatuses()))
                    .and(ukRegistryTransferringAccount.fullIdentifier
                    .eq(filter.getTransferringAccountNumber())))
                    .then(ukRegistryTransferringAccount.balance)
                .when(transactionAccountBalance.isNull()
                    .and(transaction.status.notIn(TransactionStatus.getFinalStatuses()))
                    .and(ukRegistryAcquiringAccount.fullIdentifier
                    .eq(filter.getAcquiringAccountNumber())))
                    .then(ukRegistryAcquiringAccount.balance)
                .when(transactionAccountBalance.transferringAccountIdentifier
                    .eq(transferringAccountNumberOpt.get()))
                .then(transactionAccountBalance.transferringAccountBalance)
                .when(transactionAccountBalance.acquiringAccountIdentifier
                    .eq(acquiringAccountNumberOpt.get()))
                .then(transactionAccountBalance.acquiringAccountBalance)
                .otherwise(0L)
                .as(TransactionSearchAliases.RUNNING_BALANCE_QUANTITY.getAlias());
            
            Expression<UnitType> accountBalanceUnitTypeCases = new CaseBuilder()
                .when(transactionAccountBalance.isNull().and(ukRegistryTransferringAccount.fullIdentifier
                    .eq(filter.getTransferringAccountNumber())))
                    .then(ukRegistryTransferringAccount.unitType)
                .when(transactionAccountBalance.isNull().and(ukRegistryAcquiringAccount.fullIdentifier
                    .eq(filter.getAcquiringAccountNumber())))
                    .then(ukRegistryAcquiringAccount.unitType)                
                .when(transactionAccountBalance.transferringAccountIdentifier
                    .eq(transferringAccountNumberOpt.get()))
                .then(transactionAccountBalance.transferringAccountBalanceUnitType)
                .when(transactionAccountBalance.acquiringAccountIdentifier
                    .eq(acquiringAccountNumberOpt.get()))
                .then(transactionAccountBalance.acquiringAccountBalanceUnitType)
                .otherwise(Expressions.nullExpression())
                .as(TransactionSearchAliases.RUNNING_BALANCE_UNIT_TYPE.getAlias());
            
            transactionProjection = new QTransactionProjection(
                transaction.identifier,
                transaction.type,
                transaction.status,
                transaction.lastUpdated,
                transaction.quantity,
                transaction.unitType,
                transferringAccountProjection,
                acquiringAccountProjection,
                transaction.started,
                reverses.identifier,  
                reversedBy.identifier,
                accountBalanceCases,
                accountBalanceUnitTypeCases
            ); 
        } else {
            transactionProjection = new QTransactionProjection(
                transaction.identifier,
                transaction.type,
                transaction.status,
                transaction.lastUpdated,
                transaction.quantity,
                transaction.unitType,
                transferringAccountProjection,
                acquiringAccountProjection,
                transaction.started,
                reverses.identifier, 
                reversedBy.identifier
            );
        }
        

        
        JPAQuery<TransactionProjection> query = new JPAQuery<TransactionProjection>(entityManager)
            .select(transactionProjection).from(transaction)
            .leftJoin(transaction.transferringUkRegistryAccount, ukRegistryTransferringAccount)
            .leftJoin(transaction.acquiringUkRegistryAccount, ukRegistryAcquiringAccount)
            .leftJoin(transaction.transferringUkRegistryAccount.accountHolder, ukRegistryTransferringAccountHolder)
            .leftJoin(transaction.acquiringUkRegistryAccount.accountHolder, ukRegistryAcquiringAccountHolder)
            .leftJoin(transaction.accountsBalances, transactionAccountBalance);
        
        
		query = query.leftJoin(transaction.transactionConnectionsOfTheObject, reversesTransactionConnectionsOfOriginal)
				.on(reversesTransactionConnectionsOfOriginal.type.eq(TransactionConnectionType.REVERSES)
						.and(reversesTransactionConnectionsOfOriginal.date.eq(latestReversedDateOfOriginalTransaction(transaction.id))))
				.leftJoin(reversesTransactionConnectionsOfOriginal.subjectTransaction, reversedBy)
				.on(reversesTransactionConnectionsOfOriginal.subjectTransaction.id.eq(reversedBy.id)
						.and(reversedBy.status.eq(TransactionStatus.COMPLETED)));

		query = query.leftJoin(transaction.transactionConnectionsOfTheSubject, reversesTransactionConnectionOfReversal)
				.on(reversesTransactionConnectionOfReversal.subjectTransaction.id.eq(transaction.id)
						.and(reversesTransactionConnectionOfReversal.type.eq(TransactionConnectionType.REVERSES)))
				.leftJoin(reversesTransactionConnectionOfReversal.objectTransaction, reverses)
				.on(reversesTransactionConnectionOfReversal.objectTransaction.id.eq(reverses.id));
        		     
        
        if (filter.taskParticipatesInFilter()) {
            query = query.join(transaction.taskTransaction, taskTransaction)
                .join(taskTransaction.task,task)
                .on(new OptionalBooleanBuilder(taskTransaction.transactionIdentifier.eq(transaction.identifier))
                    .notNullAnd(task.initiatedBy.urid::containsIgnoreCase, filter.getInitiatorUserId())
                    .notNullAnd(task.completedBy.urid::containsIgnoreCase, filter.getApproverUserId())
                    .notNullAnd(this::getTransactionProposalDateFrom, filter.getTransactionalProposalDateFrom())
                    .notNullAnd(this::getTransactionProposalDateTo, filter.getTransactionLastUpdateDateTo())
                    .build());
        }
        
        BooleanExpression condition = new OptionalBooleanBuilder(transaction.isNotNull())
            .notNullAnd(transaction.identifier::containsIgnoreCase, filter.getTransactionId())
            .notNullAnd(transaction.type::eq, filter.getTransactionType())
            .notNullAnd(transaction.status::eq, filter.getTransactionStatus())
            .notNullAnd(this::getTransactionLastUpdatedFrom, filter.getTransactionLastUpdateDateFrom())
            .notNullAnd(this::getTransactionLastUpdatedUntil, filter.getTransactionLastUpdateDateTo())
            .notNullAnd(this::getAccountsNumbersCondition, getAccountsNumbersFilter(filter))
            .notNullAnd(this::getTransferringAccountTypeCondition, filter.getTransferringAccountTypes())
            .notNullAnd(this::getAcquiringAccountTypeCondition, filter.getAcquiringAccountTypes())
            .notNullAnd(transaction.unitType::eq, filter.getUnitType())
            .build();

        if (filter.isEnrolledNonAdmin()) {
            BooleanExpression transferringAccountNonAdminSpec =
                JPAExpressions.selectFrom(transferringUkRegistryAccountAccess).where(
                        transferringUkRegistryAccountAccess.account.eq(ukRegistryTransferringAccount)
                            .and(transferringUkRegistryAccountAccess.user.urid.eq(filter.getAuthorizedRepresentativeUrid()))
                            .and(transferringUkRegistryAccountAccess.state.eq(AccountAccessState.ACTIVE))
                            .and(transferringUkRegistryAccountAccess.right.ne(AccountAccessRight.ROLE_BASED))
                            .and(transferringUkRegistryAccountAccess.account.accountStatus.notIn(AccountStatus.SUSPENDED,
                                AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.TRANSFER_PENDING,
                                AccountStatus.CLOSURE_PENDING,
                                AccountStatus.CLOSED)))
                    .exists();

            BooleanExpression acquiringAccountNonAdminSpec =
                JPAExpressions.selectFrom(acquiringUkRegistryAccountAccess).where(
                    acquiringUkRegistryAccountAccess.account.eq(ukRegistryAcquiringAccount)
                        .and(acquiringUkRegistryAccountAccess.user.urid.eq(filter.getAuthorizedRepresentativeUrid()))
                        .and(acquiringUkRegistryAccountAccess.state.eq(AccountAccessState.ACTIVE))
                        .and(acquiringUkRegistryAccountAccess.right.ne(AccountAccessRight.ROLE_BASED))
                        .and(acquiringUkRegistryAccountAccess.account.accountStatus.notIn(AccountStatus.SUSPENDED,
                            AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.TRANSFER_PENDING,
                            AccountStatus.CLOSURE_PENDING,
                            AccountStatus.CLOSED))
                        .and(transaction.status.eq(TransactionStatus.COMPLETED))).exists();

            condition = condition.and(transferringAccountNonAdminSpec.or(acquiringAccountNonAdminSpec));
            // we have to differentiate for admin users to check fi the have Account Access (for accounts related to the transaction)
        } else if (filter.getEndUserSearch() != null && filter.getEndUserSearch().getAdminSearch()) {
            condition = condition.and(filterTransactionByRoleBasedAccess(filter));
        }

        // Added for UKETS-5590
        if (SearchFiltersUtils.ALL_KP_GOVERNMENT_ACCOUNTS.equals(filter.getAcquiringAccountTypeOption())) {
            condition =
                condition.and(transaction.acquiringUkRegistryAccount.registryAccountType.eq(RegistryAccountType.NONE));
        }
        if (SearchFiltersUtils.ALL_KP_GOVERNMENT_ACCOUNTS.equals(filter.getTransferringAccountTypeOption())) {
            condition = condition.and(
                transaction.transferringUkRegistryAccount.registryAccountType.eq(RegistryAccountType.NONE));
        }
        
		if (filter.getReversed() != null) {
			condition = condition.and(filterTransactionByReversed(filter.getReversed()));
		}

        query = query.where(condition);

        return query;
    }

    private BooleanExpression getTransactionProposalDateFrom(Date transactionProposalDateFrom) {
        return SearchUtils.getFromDatePredicate(transactionProposalDateFrom, task.initiatedDate);
    }

    private BooleanExpression getTransactionProposalDateTo(Date transactionProposalDateTo) {
        return SearchUtils.getUntilDatePredicate(transactionProposalDateTo, task.initiatedDate);
    }

    private BooleanExpression getTransactionLastUpdatedFrom(Date lastUpdatedFrom) {
        return SearchUtils.getFromDatePredicate(lastUpdatedFrom, transaction.lastUpdated);
    }

    private BooleanExpression getTransactionLastUpdatedUntil(Date lastUpdatedTo) {
        return SearchUtils.getUntilDatePredicate(lastUpdatedTo, transaction.lastUpdated);
    }

    private BooleanExpression getTransferringAccountTypeCondition(List<AccountType> accountTypes) {
        return getAccountTypesCondition(accountTypes, ukRegistryTransferringAccount, transaction.transferringAccount);
    }

    private BooleanExpression getAcquiringAccountTypeCondition(List<AccountType> accountTypes) {
        return getAccountTypesCondition(accountTypes, ukRegistryAcquiringAccount, transaction.acquiringAccount);
    }

    private BooleanExpression getAccountTypesCondition(List<AccountType> accountTypes, QAccount accountPath,
                                                       QAccountBasicInfo accountBasicInfoPath) {
        Optional<BooleanExpression> expression = accountTypes.stream()
            .map(accountType -> getAccountTypeBooleanExpression(accountType, accountPath, accountBasicInfoPath))
            .reduce((e1, e2) -> e1.or(e2));

        if (expression.isPresent()) {
            return expression.get();
        }
        return null;
    }

    private BooleanExpression getAccountTypeBooleanExpression(AccountType accountType, QAccount accountPath,
                                                              QAccountBasicInfo accountBasicInfoPath) {
        if (accountType.getKyotoType() != null && accountType.getRegistryType() == RegistryAccountType.NONE) {
            return accountBasicInfoPath.accountType.eq(accountType.getKyotoType());
        }
        return accountPath.registryAccountType.eq(accountType.getRegistryType())
            .and(accountPath.kyotoAccountType.eq(accountType.getKyotoType()));
    }

    private BooleanExpression getAccountsNumbersCondition(AccountsNumbersFilter accountsNumbersFilter) {
        if (accountsNumbersFilter.acquiringAccountNumber != null
            && accountsNumbersFilter.transferringAccountNumber != null
            && accountsNumbersFilter.acquiringAccountNumber.equals(accountsNumbersFilter.transferringAccountNumber)) {
            return
                transaction.transferringAccount.accountFullIdentifier
                    .containsIgnoreCase(accountsNumbersFilter.transferringAccountNumber)
                    .or(transaction.acquiringAccount.accountFullIdentifier
                        .containsIgnoreCase(accountsNumbersFilter.acquiringAccountNumber));
        }
        return new OptionalBooleanBuilder(transaction.isNotNull())
            .notNullAnd(transaction.transferringAccount.accountFullIdentifier::containsIgnoreCase,
                accountsNumbersFilter.transferringAccountNumber)
            .notNullAnd(transaction.acquiringAccount.accountFullIdentifier::containsIgnoreCase,
                accountsNumbersFilter.acquiringAccountNumber)
            .build();
    }

    private AccountsNumbersFilter getAccountsNumbersFilter(TransactionFilter filter) {
        if (filter.getAcquiringAccountNumber() == null && filter.getTransferringAccountNumber() == null) {
            return null;
        }
        return new AccountsNumbersFilter(
            filter.getTransferringAccountNumber(), filter.getAcquiringAccountNumber());
    }

    private static class AccountsNumbersFilter {
        private String transferringAccountNumber;
        private String acquiringAccountNumber;

        public AccountsNumbersFilter(String transferringAccountNumber,
                                     String acquiringAccountNumber) {
            this.transferringAccountNumber = transferringAccountNumber;
            this.acquiringAccountNumber = acquiringAccountNumber;
        }
    }

    private BooleanExpression filterTransactionByRoleBasedAccess(TransactionFilter filter) {
        return transaction.id.in(transactionRelatedWithTransferringAccountWhereUserHasRoleBasedAccess(filter))
            .or(transaction.id.in(transactionRelatedWithAcquiringAccountWhereUserHasRoleBasedAccess(filter)));
    }

    private JPQLQuery<Long> transactionRelatedWithTransferringAccountWhereUserHasRoleBasedAccess(
        TransactionFilter filter) {
        return JPAExpressions
            .select(transaction.id)
            .from(transaction)
            .innerJoin(transaction.transferringUkRegistryAccount, ukRegistryTransferringAccount)
            .innerJoin(ukRegistryTransferringAccount.accountAccesses, transferringUkRegistryAccountAccess)
            .innerJoin(transferringUkRegistryAccountAccess.user, relatedToAccountAccess)
            .where(
                transferringUkRegistryAccountAccess.state.eq(AccountAccessState.ACTIVE)
                    .and(transferringUkRegistryAccountAccess.right.eq(AccountAccessRight.ROLE_BASED))
                    .and(relatedToAccountAccess.iamIdentifier.eq(filter.getEndUserSearch().getIamIdentifier()))
            );
    }

    private JPQLQuery<Long> transactionRelatedWithAcquiringAccountWhereUserHasRoleBasedAccess(
        TransactionFilter filter) {
        return JPAExpressions
            .select(transaction.id)
            .from(transaction)
            .innerJoin(transaction.acquiringUkRegistryAccount, ukRegistryAcquiringAccount)
            .innerJoin(ukRegistryAcquiringAccount.accountAccesses, acquiringUkRegistryAccountAccess)
            .innerJoin(acquiringUkRegistryAccountAccess.user, relatedToAccountAccess)
            .where(
                acquiringUkRegistryAccountAccess.state.eq(AccountAccessState.ACTIVE)
                    .and(acquiringUkRegistryAccountAccess.right.eq(AccountAccessRight.ROLE_BASED))
                    .and(relatedToAccountAccess.iamIdentifier.eq(filter.getEndUserSearch().getIamIdentifier()))
            );
    }
    
	private JPQLQuery<Date> latestReversedDateOfOriginalTransaction(NumberPath<Long> transactionId) {
		return JPAExpressions
				.select(reversesTransactionConnections.date.max())
				.from(reversesTransactionConnections)
				.where(reversesTransactionConnections.type.eq(TransactionConnectionType.REVERSES)
						.and(reversesTransactionConnections.objectTransaction.id.eq(transactionId)));
	}
    
	private BooleanExpression filterTransactionByReversed(Boolean reversed) {
		if (Boolean.TRUE.equals(reversed)) {
			return transaction.id.in(transactionAssociatedWithCompletedReversal());
		} else if (Boolean.FALSE.equals(reversed)) {
			return transaction.type.notIn(TransactionType.getReversalTransactionTypes())
					.and(transaction.id.notIn(transactionAssociatedWithCompletedReversal()));
		}
		return null; // otherwise do not modify the query
	}

	private JPQLQuery<Long> transactionAssociatedWithCompletedReversal() {
		return JPAExpressions
				.select(transaction.id)
				.from(transaction)
				.innerJoin(transaction.transactionConnectionsOfTheObject, reversesTransactionConnections)
				.innerJoin(reversalTransaction)
					.on(reversesTransactionConnections.subjectTransaction.id.eq(reversalTransaction.id))
				.where(reversesTransactionConnections.type.eq(TransactionConnectionType.REVERSES)
						.and(reversalTransaction.status.eq(TransactionStatus.COMPLETED)));
	}

}
