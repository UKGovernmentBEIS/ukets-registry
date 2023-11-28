package gov.uk.ets.registry.api.task.repository;

import static java.util.stream.Collectors.toList;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
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
import gov.uk.ets.registry.api.common.search.SearchUtils;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.QTaskTransaction;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.task.searchmetadata.domain.QTaskSearchMetadata;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.task.shared.QTaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskPropertyPath;
import gov.uk.ets.registry.api.task.shared.TaskSearchAliases;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.transaction.domain.QSearchableTransaction;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionAcquiringAccountMode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.user.domain.QIamUserRole;
import gov.uk.ets.registry.api.user.domain.QUser;
import gov.uk.ets.registry.api.user.domain.QUserRoleMapping;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class TaskRepositoryImpl implements TaskProjectionRepository {

    private static final QTask task = QTask.task;
    private static final QSearchableTransaction searchableTransaction = QSearchableTransaction.searchableTransaction;
    private static final QAccount account = QAccount.account;
    private static final QAccount transactionAcquiringAccount = QAccount.account;
    private static final QAccountHolder holder = QAccountHolder.accountHolder;
    private static final QUser claimedBy = QUser.user;
    private static final QUser disclosedName = QUser.user;
    private static final QUser createdBy = new QUser("initiatedBy");
    private static final QAccountAccess accountAccess = QAccountAccess.accountAccess;
    private static final QUser relatedToUser = new QUser("relatedToUser");
    private static final QUser relatedToAccountAccess = new QUser("relatedToAccountAccess");
    private static final QTaskSearchMetadata taskSearchMetadataForAh = new QTaskSearchMetadata("accountHolderMetadata");
    private static final QTaskSearchMetadata taskSearchMetadataForType = new QTaskSearchMetadata("accountTypeMetadata");
    private static final QTaskSearchMetadata taskSearchMetadataForAllocationCategory = new QTaskSearchMetadata("allocationCategoryMetadata");
    private static final QTaskSearchMetadata taskSearchMetadataForAllocationYear = new QTaskSearchMetadata("allocationYearMetadata");
    private static final QTaskSearchMetadata subTaskSearchMetadata = new QTaskSearchMetadata("subTaskSearchMetadata");
    private static final QTaskTransaction taskTransaction = new QTaskTransaction("transactionIdentifer");
    public static final String ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES = "ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES";

    /**
     * The persistence context.
     */
    @PersistenceContext
    EntityManager entityManager;

    private Map<String, EntityPathBase<?>> sortingMap = Stream.of(new Object[][] {
        {TaskPropertyPath.TASK_REQUEST_ID, task},
        {TaskPropertyPath.TASK_TYPE, task},
        {TaskPropertyPath.TASK_INITIATOR_FIRST_NAME, task.initiatedBy},
        {TaskPropertyPath.TASK_INITIATOR_LAST_NAME, task.initiatedBy},
        {TaskPropertyPath.TASK_CLAIMANT_FIRST_NAME, task.claimedBy},
        {TaskPropertyPath.TASK_CLAIMANT_LAST_NAME, task.claimedBy},
        {TaskPropertyPath.ACCOUNT_IDENTIFIER, account},
        {TaskPropertyPath.ACCOUNT_KYOTO_TYPE, account},
        {TaskPropertyPath.ACCOUNT_REGISTRY_TYPE, account},
        {TaskPropertyPath.ACCOUNT_TYPE_LABEL, account},
        {TaskPropertyPath.TASK_SEARCH_METADATA_TYPE_VALUE, taskSearchMetadataForType},
        {TaskPropertyPath.ACCOUNT_HOLDER_NAME, holder},
        {TaskPropertyPath.TASK_SEARCH_METADATA_AH_VALUE, taskSearchMetadataForAh},
        {TaskPropertyPath.TASK_SEARCH_METADATA_ALLOCATION_CATEGORY_VALUE, taskSearchMetadataForAllocationCategory},
        {TaskPropertyPath.TASK_SEARCH_METADATA_ALLOCATION_YEAR_VALUE, taskSearchMetadataForAllocationYear},
        {TaskPropertyPath.TASK_CREATED_DATE, task},
        {TaskPropertyPath.TASK_STATUS, task},
    }).collect(Collectors.toMap(
        data -> (String) data[0],
        data -> (EntityPathBase<?>) data[1]));

    private static Map<String, Class<?>> aliasMap = Map.of(
        TaskSearchAliases.TRANSACTION_IDENTIFIERS.getAlias(),String.class,
        TaskSearchAliases.RECIPIENT_ACCOUNT_NUMBERS.getAlias(),String.class
        );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TaskProjection> userSearch(TaskSearchCriteria criteria, Pageable pageable) {
        return new Search.Builder<TaskProjection>()
            .query(getUserQuery(criteria))
            .countQuery(getUserCountQuery(criteria))
            .pageable(pageable)
            .sortingMap(sortingMap)
            .aliasesMap(aliasMap)
            .build()
            .getResults();
    }

    public JPAQuery<TaskProjection> getUserQuery(TaskSearchCriteria criteria) {

        JPAQuery<TaskProjection> query = getTaskProjectionJPAQuery(false);

        //Handle Claimant and Initiator cases
        query.where(getUserWhereStatement(criteria));
        return groupBy(query);
    }     
    
    private Predicate []  getUserWhereStatement(TaskSearchCriteria criteria) {
        Predicate [] predicates =  {getCommonCriteria(criteria)
            .notNullAnd(task.requestId::eq, criteria.getRequestId())
            .notNullAnd(this::getUserQueryClaimantBooleanExpression, criteria.getClaimantName())
            .notNullAnd(this::getUserQueryInitiatorBooleanExpression, criteria.getInitiatorName())
            .build()
            .and(task.id.notIn(tasksForAllocationRequest())),
        //A couple of subqueries that fetch either tasks that the user is AR to
        // or/and tasks that are directly bound to the user e.g. Submit Document tasks
        task.id.in(
                JPAExpressions
                    .select(task.id)
                    .from(task)
                    .innerJoin(task.account, account)
                    .innerJoin(account.accountAccesses, accountAccess)
                    .on(accountAccess.state.eq(AccountAccessState.ACTIVE)
                        .and(accountAccess.right.ne(AccountAccessRight.ROLE_BASED)))
                    .innerJoin(accountAccess.user, relatedToAccountAccess)
                    .on(new BooleanBuilder(
                        relatedToAccountAccess.iamIdentifier.eq(criteria.getEndUserSearch().getIamIdentifier())))
                    .where(account.accountStatus.notIn(AccountStatus.SUSPENDED, AccountStatus.SUSPENDED_PARTIALLY)
                        .and(accountAccess.user.state.eq(UserStatus.ENROLLED))
                    ))
            .or(task.id.in(
                JPAExpressions
                    .select(task.id)
                    .from(task)
                    .innerJoin(task.user, relatedToUser)
                    .on(new BooleanBuilder(
                        relatedToUser.iamIdentifier.eq(criteria.getEndUserSearch().getIamIdentifier())))
                    .where(task.account.isNull()
                        .or(task.type.eq(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD)
                            .and(task.account.accountStatus
                                .notIn(AccountStatus.SUSPENDED, AccountStatus.SUSPENDED_PARTIALLY)))))
            ),
        task.type.notIn(RequestType.getTasksNotDisplayedToAR()),
        searchableTransaction.type.isNull()
            .or(searchableTransaction.type
                .notIn(TransactionType.tasksAccessibleOnlyToAdmin()))
            };
        return predicates;
    }   
    
    public LongSupplier getUserCountQuery(TaskSearchCriteria criteria) {
        return new LongSupplier() {
            
            @Override
            public long getAsLong() {
                
                JPAQuery<Long> countQuery = getTotalResutsJPAQuery(false) 
                    .where(getUserWhereStatement(criteria));
                
                return countQuery
                    .fetchOne();
            }
        };
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TaskProjection> adminSearch(TaskSearchCriteria criteria, Pageable pageable) {
        return new Search.Builder<TaskProjection>()
            .query(getAdminQuery(criteria))
            .countQuery(getAdminCountQuery(criteria))
            .pageable(pageable)
            .sortingMap(sortingMap)
            .aliasesMap(aliasMap)
            .build()
            .getResults();
    }

    public JPAQuery<TaskProjection> getAdminQuery(TaskSearchCriteria criteria) {
        JPAQuery<TaskProjection> tasksQuery = getTaskProjectionJPAQuery(true)
            .where(getAdminWhereStatement(criteria));
        return groupBy(tasksQuery);
    }  
    
    public LongSupplier getAdminCountQuery(TaskSearchCriteria criteria) {
        return new LongSupplier() {
            
            @Override
            public long getAsLong() {
                
                JPAQuery<Long> countQuery = getTotalResutsJPAQuery(true) 
                    .where(getAdminWhereStatement(criteria));
                
                return countQuery
                    .fetchOne();
            }
        };
    }
    
    private <T> JPAQuery<T> groupBy(JPAQuery<T> query) {
        return query.groupBy(task.requestId
            ,task.type,task.initiatedBy.knownAs,task.initiatedBy.firstName,task.initiatedBy.lastName,
            task.initiatedBy.id,task.initiatedBy.urid,task.initiatedBy.disclosedName,
            task.claimedBy.knownAs,task.claimedBy.firstName,task.claimedBy.lastName,task.claimedBy.disclosedName,
            account.identifier,account.fullIdentifier,account.kyotoAccountType,account.registryAccountType,
            taskSearchMetadataForType.metadataName,taskSearchMetadataForType.metadataValue,account.accountStatus,
            taskSearchMetadataForAh.metadataName,taskSearchMetadataForAh.metadataValue,
            taskSearchMetadataForAllocationCategory.metadataName,taskSearchMetadataForAllocationCategory.metadataValue,
            taskSearchMetadataForAllocationYear.metadataName,taskSearchMetadataForAllocationYear.metadataValue,account.accountHolder.name,
            task.user.disclosedName,task.user.urid,task.initiatedDate,task.status,transactionAcquiringAccount.accountName,
            task.difference,task.claimedBy.urid,task.completedDate, account.accountHolder.identifier,account.accountType
            );
    }
    
    private Predicate getAdminWhereStatement(TaskSearchCriteria criteria) {
        return  getCommonCriteria(criteria)
            .notNullAnd(this::getAdminQueryRequestIdBooleanExpression, criteria.getRequestId())
            .notNullAnd(this::getAdminQueryClaimantBooleanExpression, criteria.getClaimantName())
            .notNullAnd(this::getAdminQueryInitiatorBooleanExpression, criteria.getInitiatorName())
            .notNullAnd(this::filterTaskTypes, criteria.getExcludeUserTasks())
            .notNullAnd(this::filterInitiatedBy, criteria.getInitiatedBy())
            .notNullAnd(this::filterClaimedBy, criteria.getClaimedBy())
            .build()
            .and(filterTaskByRoleBasedAccess(criteria));
    }    
    
    private QTaskProjection getQTaskProjection(boolean isAdminQuery) {
        return  new QTaskProjection(
            task.requestId,
            task.type,
            isAdminQuery ?
                new CaseBuilder()
                    .when(task.initiatedBy.knownAs.isNotEmpty())
                    .then(task.initiatedBy.knownAs)
                    .otherwise(task.initiatedBy.firstName)
                : task.initiatedBy.disclosedName,
            isAdminQuery ?
                new CaseBuilder()
                    .when(task.initiatedBy.knownAs.isNotEmpty())
                    .then(Expressions.stringTemplate("null"))
                    .otherwise(task.initiatedBy.lastName)
                : ConstantImpl.create(""),
            task.initiatedBy.id,
            task.initiatedBy.urid,
            isAdminQuery ?
                new CaseBuilder()
                    .when(task.claimedBy.knownAs.isNotEmpty())
                    .then(task.claimedBy.knownAs)
                    .otherwise(task.claimedBy.firstName)
                : task.claimedBy.disclosedName,
            isAdminQuery ?
                new CaseBuilder()
                    .when(task.claimedBy.knownAs.isNotEmpty())
                    .then(Expressions.stringTemplate("null"))
                    .otherwise(task.claimedBy.lastName)
                : ConstantImpl.create(""),
            account.identifier,
            account.fullIdentifier,
            account.kyotoAccountType,
            account.registryAccountType,
            //for the Account open tasks, the account holder/type are found in the taskSearchMetadata table
            //the next two casebuilders will not be needed if/when we move everything to that table
            new CaseBuilder()
                .when(taskSearchMetadataForType.metadataName.eq(MetadataName.ACCOUNT_TYPE))
                .then(taskSearchMetadataForType.metadataValue)
                .otherwise(ConstantImpl.create("")),
            account.accountStatus,
            new CaseBuilder()
                .when(taskSearchMetadataForAh.metadataName.eq(MetadataName.AH_NAME))
                .then(taskSearchMetadataForAh.metadataValue)
                .otherwise(account.accountHolder.name),
            new CaseBuilder()
                .when(task.type.in(RequestType.getTasksWithDisclosedName()))
                .then(task.user.disclosedName)
                .otherwise(Expressions.stringTemplate("null")),
            new CaseBuilder()
                .when(task.user.disclosedName.eq("Registry Administrator"))
                .then(Expressions.stringTemplate("null"))
                .otherwise(task.user.urid),
            Expressions.stringTemplate("string_agg({0},{1})", taskTransaction.transactionIdentifier,",").as(TaskSearchAliases.TRANSACTION_IDENTIFIERS.getAlias()),
            task.initiatedDate,
            task.status,
            isAdminQuery ? Expressions.stringTemplate("string_agg({0},{1})", taskTransaction.recipientAccountNumber,",").as(TaskSearchAliases.RECIPIENT_ACCOUNT_NUMBERS.getAlias()) : 
                Expressions.stringTemplate("string_agg({0},{1})",
            new CaseBuilder()
                .when(taskTransaction.isNotNull()
                    .and(taskTransaction.transaction.type.in(TransactionType.showAccountNameInsteadOfNumber())))
                .then(
                    JPAExpressions.select(transactionAcquiringAccount.accountName)
                        .from(transactionAcquiringAccount)
                        .where(transactionAcquiringAccount.identifier.eq(
                            taskTransaction.transaction.acquiringAccount.accountIdentifier))
                )
                .otherwise(taskTransaction.recipientAccountNumber),",").as(TaskSearchAliases.RECIPIENT_ACCOUNT_NUMBERS.getAlias()),
            task.difference,
            task.claimedBy.urid,
            task.completedDate,
            account.accountHolder.identifier,
            account.accountType,
            isAdminQuery ?
                    new CaseBuilder()
                            .when(taskSearchMetadataForAllocationCategory.metadataName.eq(MetadataName.ALLOCATION_CATEGORY))
                            .then(taskSearchMetadataForAllocationCategory.metadataValue)
                            .otherwise(ConstantImpl.create(""))
                    : ConstantImpl.create(""),
            isAdminQuery ?
                    new CaseBuilder()
                            .when(taskSearchMetadataForAllocationYear.metadataName.eq(MetadataName.ALLOCATION_YEAR))
                            .then(taskSearchMetadataForAllocationYear.metadataValue)
                            .otherwise(ConstantImpl.create(""))
                    : ConstantImpl.create(""));
    }
    
    private JPAQuery<Long> getTotalResutsJPAQuery(boolean isAdminQuery) {
        JPAQuery<Long> totalResultsJPAQuery = new JPAQuery<>(entityManager);

        totalResultsJPAQuery.select(task.requestId.countDistinct());

        return getCommonJoinStatements(totalResultsJPAQuery, isAdminQuery);
    }
    
    private JPAQuery<TaskProjection> getTaskProjectionJPAQuery(boolean isAdminQuery) {
        JPAQuery<TaskProjection> taskProjectionJPAQuery = new JPAQuery<>(entityManager);
        taskProjectionJPAQuery.select(getQTaskProjection(isAdminQuery));
            
        return getCommonJoinStatements(taskProjectionJPAQuery, isAdminQuery);
    }

    private <E> JPAQuery<E> getCommonJoinStatements(JPAQuery<E> taskProjectionJPAQuery,boolean isAdminQuery) {
         taskProjectionJPAQuery.from(task)
            .innerJoin(task.initiatedBy, createdBy)
            .leftJoin(task.account, account)
            .leftJoin(task.user, disclosedName)
            .leftJoin(account.accountHolder, holder)
            .leftJoin(task.claimedBy, claimedBy)
            .leftJoin(task.taskSearchMetadata, taskSearchMetadataForType)
            .on(taskSearchMetadataForType.metadataName.eq(MetadataName.ACCOUNT_TYPE))
            .leftJoin(task.taskSearchMetadata, taskSearchMetadataForAh)
            .on(taskSearchMetadataForAh.metadataName.eq(MetadataName.AH_NAME))
            .leftJoin(task.taskSearchMetadata, taskSearchMetadataForAllocationCategory)
            .on(taskSearchMetadataForAllocationCategory.metadataName.eq(MetadataName.ALLOCATION_CATEGORY))
            .leftJoin(task.taskSearchMetadata, taskSearchMetadataForAllocationYear)
            .on(taskSearchMetadataForAllocationYear.metadataName.eq(MetadataName.ALLOCATION_YEAR))
            .leftJoin(task.transactionIdentifiers,taskTransaction);
        if (!isAdminQuery) {
            taskProjectionJPAQuery.leftJoin(taskTransaction.transaction, searchableTransaction);
        }
        return taskProjectionJPAQuery;
    }
    
    private OptionalBooleanBuilder getCommonCriteria(TaskSearchCriteria criteria) {
        return new OptionalBooleanBuilder(task.isNotNull())
            .notNullAnd(this::getAccountHolderBooleanExpression, criteria.getAccountHolder())
            .notNullAnd(this::getAllocationCategoryBooleanExpression, criteria.getAllocationCategory())
            .notNullAnd(this::getAllocationYearBooleanExpression, criteria.getAllocationYear())
            .notNullAnd(this::getNameOrUserIdBooleanExpression, criteria.getNameOrUserId())
            .notNullAnd(this::getAccountNumberBooleanExpression, criteria.getAccountNumber())
            .notNullAnd(this::getkyotoAccountTypeBooleanExpression, criteria.getAccountType())
            .notNullAnd(this::getRegistryAccountTypeBooleanExpression, criteria.getAccountType())
            .notNullAnd(task.status::eq, criteria.getTaskOutcome())
            .notNullAnd(this::getOutcomeBooleanExpression, criteria.getTaskStatus())
            .notNullAnd(task.type::eq, criteria.getTaskType())
            .notNullAnd(this::getTransactionIdBooleanExpression, criteria.getTransactionId())
            .notNullAnd(this::getUridBooleanExpression, criteria.getUrid())
            .notNullAnd(this::getCreatedOnFromBooleanExpression, criteria.getCreatedOnFrom())
            .notNullAnd(this::getCreatedOnToBooleanExpression, criteria.getCreatedOnTo())
            .notNullAnd(this::getClaimedOnFromBooleanExpression, criteria.getClaimedOnFrom())
            .notNullAnd(this::getClaimedOnToBooleanExpression, criteria.getClaimedOnTo())
            .notNullAnd(this::getCompletedOnFromBooleanExpression, criteria.getCompletedOnFrom())
            .notNullAnd(this::getCompletedOnToBooleanExpression, criteria.getCompletedOnTo());
    }


    private BooleanExpression getCreatedOnFromBooleanExpression(Date createdOnFrom) {
        return getFromDatePredicate(createdOnFrom, task.initiatedDate);
    }

    private BooleanExpression getCreatedOnToBooleanExpression(Date createdOnTo) {
        return getUntilDatePredicate(createdOnTo, task.initiatedDate);
    }

    private BooleanExpression getClaimedOnFromBooleanExpression(Date claimedOnFrom) {
        return getFromDatePredicate(claimedOnFrom, task.claimedDate);
    }

    private BooleanExpression getClaimedOnToBooleanExpression(Date claimedOnTo) {
        return getUntilDatePredicate(claimedOnTo, task.claimedDate);
    }

    private BooleanExpression getCompletedOnFromBooleanExpression(Date completedOnFrom) {
        return getFromDatePredicate(completedOnFrom, task.completedDate);
    }

    private BooleanExpression getCompletedOnToBooleanExpression(Date completedOnTo) {
        return getUntilDatePredicate(completedOnTo, task.completedDate);
    }

    private BooleanExpression getFromDatePredicate(Date input, DateTimePath<Date> path) {
        LocalDate localDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return path.eq(date).or(path.after(date));
    }

    private BooleanExpression getUntilDatePredicate(Date input, DateTimePath<Date> path) {
        LocalDate localDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Date date = Date
            .from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return path.before(date);
    }

    private BooleanExpression getAccountNumberBooleanExpression(String accountNumber) {
        return account.fullIdentifier.containsIgnoreCase(accountNumber);
    }

    // for Account open tasks, account type and holder are stored in the taskSearchMetadata table
    private BooleanExpression getkyotoAccountTypeBooleanExpression(String kyotoAccountType) {
        KyotoAccountType code = KyotoAccountType.parse(kyotoAccountType);
        return code != null ? account.kyotoAccountType.eq(code)
            .or(taskSearchMetadataForType.metadataName.eq(MetadataName.ACCOUNT_TYPE)
                .and(taskSearchMetadataForType.metadataValue.eq(kyotoAccountType))) :
            account.kyotoAccountType.eq(KyotoAccountType.PARTY_HOLDING_ACCOUNT).or(account.isNull());
    }

    private BooleanExpression getRegistryAccountTypeBooleanExpression(String registryAccountType) {
        RegistryAccountType code = RegistryAccountType.parse(registryAccountType);
        return code != null ? account.registryAccountType.eq(code)
            .or(taskSearchMetadataForType.metadataName.eq(MetadataName.ACCOUNT_TYPE)
                .and(taskSearchMetadataForType.metadataValue.eq(registryAccountType))) :
            account.registryAccountType.eq(RegistryAccountType.NONE).or(account.isNull());
    }

    private BooleanExpression getAdminQueryClaimantBooleanExpression(String claimant) {
        return claimedBy.knownAs.isNotEmpty().and(claimedBy.knownAs.containsIgnoreCase(claimant))
            .or(claimedBy.knownAs.isNull().or(claimedBy.knownAs.isEmpty()).and(
                SearchUtils.getFirstNameLastNamePredicate(claimant, claimedBy.firstName, claimedBy.lastName)));
    }

    private BooleanExpression getAdminQueryInitiatorBooleanExpression(String creator) {
        return createdBy.knownAs.isNotEmpty().and(createdBy.knownAs.containsIgnoreCase(creator))
            .or(createdBy.knownAs.isNull().or(createdBy.knownAs.isEmpty()).and(
                SearchUtils.getFirstNameLastNamePredicate(creator, createdBy.firstName, createdBy.lastName)));
    }

    private BooleanExpression getAdminQueryRequestIdBooleanExpression(Long requestId) {
        return task.requestId.eq(requestId)
            .or(JPAExpressions.select(task.parentTask.requestId).from(task.parentTask).eq(requestId));
    }

    private BooleanExpression getUserQueryClaimantBooleanExpression(String claimant) {
        return claimedBy.disclosedName.containsIgnoreCase(claimant);
    }

    private BooleanExpression getUserQueryInitiatorBooleanExpression(String creator) {
        return createdBy.disclosedName.containsIgnoreCase(creator);
    }

    private BooleanExpression getAccountHolderBooleanExpression(String accountHolder) {
        return taskSearchMetadataForAh.metadataName.eq(MetadataName.AH_NAME)
            .and(taskSearchMetadataForAh.metadataValue.containsIgnoreCase(accountHolder))
            .or(account.accountHolder.name.containsIgnoreCase(accountHolder));
    }

    private BooleanExpression getAllocationCategoryBooleanExpression(String allocationCategory) {
        return taskSearchMetadataForAllocationCategory.metadataName.eq(MetadataName.ALLOCATION_CATEGORY)
            .and(taskSearchMetadataForAllocationCategory.metadataValue.eq(allocationCategory));
    }

    private BooleanExpression getAllocationYearBooleanExpression(String allocationYear) {
        return taskSearchMetadataForAllocationYear.metadataName.eq(MetadataName.ALLOCATION_YEAR)
            .and(taskSearchMetadataForAllocationYear.metadataValue.eq(allocationYear));
    }

    private BooleanExpression getTransactionIdBooleanExpression(String transactionId) {
        return task.id.eq(tasksForTransaction(transactionId));
    }

    private BooleanExpression getNameOrUserIdBooleanExpression(String nameOrUserId) {
        return JPAExpressions
                .select(subTaskSearchMetadata)
                .from(subTaskSearchMetadata)
                .where(subTaskSearchMetadata.task.eq(task)
                        .and(
                                subTaskSearchMetadata.metadataName.eq(MetadataName.USER_ID_NAME_KNOWN_AS)
                                        .and(subTaskSearchMetadata.metadataValue.contains(nameOrUserId))
                        )
                )
                .exists();
    }

    private BooleanExpression getUridBooleanExpression(String urid) {
        return task.user.urid.containsIgnoreCase(urid);
    }

    private BooleanExpression getOutcomeBooleanExpression(TaskStatus status) {
        BooleanExpression result = null;

        if (TaskStatus.OPEN.equals(status)) {
            result = task.status.ne(RequestStateEnum.APPROVED)
                .and(task.status.ne(RequestStateEnum.REJECTED));

        } else if (TaskStatus.UNCLAIMED.equals(status)) {
            result = task.claimedBy.isNull();

        } else if (TaskStatus.CLAIMED.equals(status)) {
            result = task.claimedBy.isNotNull().and(task.status.ne(RequestStateEnum.APPROVED))
                .and(task.status.ne(RequestStateEnum.REJECTED));

        } else if (TaskStatus.COMPLETED.equals(status)) {
            result = task.status.eq(RequestStateEnum.APPROVED)
                .or(task.status.eq(RequestStateEnum.REJECTED));
        }

        return result;

    }

    /**
     * Filters out user tasks (as defined in {@link RequestType}).
     */
    private BooleanExpression filterTaskTypes(Boolean excludeUserTasks) {
        if (Boolean.TRUE.equals(excludeUserTasks)) {
            return task.type.notIn(RequestType.getUserTasks());
        }
        return null; // otherwise do not modify the query
    }


    /**
     * Filter task initiator according to its role.
     */
    private BooleanExpression filterInitiatedBy(String initiatedBy) {
        List<String> roleNames = mapToRoleNames(initiatedBy);
        return task.initiatedBy.id.in(getRoleNameSubQuery(roleNames));
    }

    /**
     * Filter task claimant according to its role.
     * In case of 'ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES' we also want to show the unclaimed tasks.
     */
    private BooleanExpression filterClaimedBy(String claimedBy) {
        List<String> roleNames = mapToRoleNames(claimedBy);
        BooleanExpression claimedByRoles = task.claimedBy.id.in(getRoleNameSubQuery(roleNames));

        if (claimedBy.equals(ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES)) {
            return claimedByRoles.or(task.claimedBy.isNull());
        }
        return claimedByRoles;
    }

    private List<String> mapToRoleNames(String roleType) {
        if (roleType.equals(ALL_EXCEPT_AUTHORIZED_REPRESENTATIVES)) {
            return UserRole.getAllExceptAr().stream()
                .map(UserRole::getKeycloakLiteral)
                .collect(toList());
        } else {
            return List.of(UserRole.valueOf(roleType).getKeycloakLiteral());
        }
    }

    /**
     * This function creates a sub-query to be used to filter by user id.
     */
    private JPQLQuery<Long> getRoleNameSubQuery(List<String> roleNames) {
        QUser u = new QUser("u");
        QIamUserRole iur = new QIamUserRole("iur");
        QUserRoleMapping urm = new QUserRoleMapping("urm");

        BooleanExpression roleNameCheck = iur.roleName.in(roleNames.toArray(new String[0]));

        return JPAExpressions.select(u.id)
            .from(u)
            .innerJoin(u.userRoles, urm)
            .innerJoin(urm.role, iur)
            .where(roleNameCheck);
    }

    /**
     * We want the admins to see both the tasks that are related to account on which the have role based access
     * AND
     * the tasks that are not related to any accounts.
     * AND
     * the tasks concerning transaction reversals (exception)
     * AND
     * the tasks concerning allocation (another exception..)
     */
    private BooleanExpression filterTaskByRoleBasedAccess(
        TaskSearchCriteria criteria) {
        return task.id.in(tasksRelatedWithAccountWhereUserHasRoleBasedAccess(criteria))
            .or(task.id.in(tasksUnrelatedToAccounts()))
            .or(task.id.in(tasksForTransactionReversals()))
            .or(task.id.in(tasksForAllocationRequest()));
    }

    private JPQLQuery<Long> tasksRelatedWithAccountWhereUserHasRoleBasedAccess(
        TaskSearchCriteria criteria) {
        return JPAExpressions
            .select(task.id)
            .from(task)
            .innerJoin(task.account, account)
            .innerJoin(account.accountAccesses, accountAccess)
            .innerJoin(accountAccess.user, relatedToAccountAccess)
            .where(
                accountAccess.state.eq(AccountAccessState.ACTIVE)
                    .and(accountAccess.right.eq(AccountAccessRight.ROLE_BASED))
                    .and(relatedToAccountAccess.iamIdentifier.eq(criteria.getEndUserSearch().getIamIdentifier()))
            );
    }

    private JPQLQuery<Long> tasksUnrelatedToAccounts() {
        return JPAExpressions
            .select(task.id)
            .from(task)
            .where(task.account.isNull());
    }

    private JPQLQuery<Long> tasksForTransactionReversals() {
        return JPAExpressions
            .select(task.id)
            .from(task)
            .innerJoin(taskTransaction.transaction, searchableTransaction)
            .where(searchableTransaction.type.in(reversalTransactionTypes()));
    }

    private JPQLQuery<Long> tasksForAllocationRequest() {
        return JPAExpressions
            .select(task.id)
            .from(task)
            .where(task.type.eq(RequestType.ALLOCATION_REQUEST));
    }
    
    private JPQLQuery<Long> tasksForTransaction(String transactionId) {
        return JPAExpressions
            .select(taskTransaction.task.id)
            .from(taskTransaction)
            .where(taskTransaction.transactionIdentifier.containsIgnoreCase(transactionId));
    }

    private List<TransactionType> reversalTransactionTypes() {
        return Arrays.stream(TransactionType.values())
            .filter(t -> t.getAcquiringAccountMode() != null)
            .filter(t -> t.getAcquiringAccountMode().equals(TransactionAcquiringAccountMode.REVERSED))
            .collect(toList());
    }
}
