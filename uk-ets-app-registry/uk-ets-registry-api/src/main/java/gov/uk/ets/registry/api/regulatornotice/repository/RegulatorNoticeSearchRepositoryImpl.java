package gov.uk.ets.registry.api.regulatornotice.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.QAccount;
import gov.uk.ets.registry.api.account.domain.QAccountHolder;
import gov.uk.ets.registry.api.account.domain.QAircraftOperator;
import gov.uk.ets.registry.api.account.domain.QCompliantEntity;
import gov.uk.ets.registry.api.account.domain.QInstallation;
import gov.uk.ets.registry.api.account.domain.QMaritimeOperator;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.common.search.SearchUtils;
import gov.uk.ets.registry.api.regulatornotice.domain.QRegulatorNotice;
import gov.uk.ets.registry.api.regulatornotice.shared.QRegulatorNoticeProjection;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticeProjection;
import gov.uk.ets.registry.api.regulatornotice.shared.RegulatorNoticePropertyPath;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeSearchCriteria;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.user.domain.QUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RegulatorNoticeSearchRepositoryImpl implements RegulatorNoticeSearchRepository {
    private static final QRegulatorNotice regulatorNotice = QRegulatorNotice.regulatorNotice;
    private static final QAccount account = QAccount.account;
    private static final QAccountHolder holder = QAccountHolder.accountHolder;
    private static final QCompliantEntity compliantEntity = QCompliantEntity.compliantEntity;
    private static final QInstallation installation = QInstallation.installation;
    private static final QAircraftOperator aircraft = QAircraftOperator.aircraftOperator;
    private static final QMaritimeOperator maritime = QMaritimeOperator.maritimeOperator;
    private static final QUser claimedBy = new QUser("claimedBy");
    private static final QUser completedBy = new QUser("completedBy");


    @PersistenceContext
    EntityManager entityManager;

    private final Map<String, EntityPathBase<?>> sortingMap = Stream.of(new Object[][]{
            {RegulatorNoticePropertyPath.TASK_REQUEST_ID, regulatorNotice},
            {RegulatorNoticePropertyPath.ACCOUNT_HOLDER_NAME, holder},
            {RegulatorNoticePropertyPath.TASK_PROCESS_TYPE, regulatorNotice},
            {RegulatorNoticePropertyPath.TASK_INITIATED_DATE, regulatorNotice},
            {RegulatorNoticePropertyPath.PERMIT_OR_MONITORING_PLAN_IDENTIFIER, compliantEntity},
            {RegulatorNoticePropertyPath.TASK_CLAIMANT_FIRST_NAME, regulatorNotice.claimedBy},
            {RegulatorNoticePropertyPath.TASK_CLAIMANT_LAST_NAME, regulatorNotice.claimedBy},
            {RegulatorNoticePropertyPath.TASK_STATUS, regulatorNotice}
    }).collect(Collectors.toMap(
            data -> (String) data[0],
            data -> (EntityPathBase<?>) data[1]));

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<RegulatorNoticeProjection> search(RegulatorNoticeSearchCriteria criteria, Pageable pageable) {
        return new Search.Builder<RegulatorNoticeProjection>()
                .pageable(pageable)
                .sortingMap(sortingMap)
                .query(getQuery(criteria))
                .build().getResults();
    }

    public JPAQuery<RegulatorNoticeProjection> getQuery(RegulatorNoticeSearchCriteria criteria) {
        JPAQuery<RegulatorNoticeProjection> regulatorNoticeProjectionQuery = new JPAQuery<AccountProjection>(entityManager)
                .select(createRegulatorNoticeProjection()).from(regulatorNotice)
                .leftJoin(regulatorNotice.account, account)
                .leftJoin(account.accountHolder, holder)
                .leftJoin(regulatorNotice.claimedBy, claimedBy)
                .leftJoin(regulatorNotice.completedBy, completedBy)
                .leftJoin(account.compliantEntity, compliantEntity)
                .leftJoin(installation).on(installation.id.eq(compliantEntity.id))
                .leftJoin(aircraft).on(aircraft.id.eq(compliantEntity.id))
                .leftJoin(maritime).on(maritime.id.eq(compliantEntity.id));

        return regulatorNoticeProjectionQuery.where(
                new OptionalBooleanBuilder(regulatorNotice.isNotNull())
                        .notNullAnd(account.fullIdentifier::containsIgnoreCase, criteria.getAccountNumber())
                        .notNullAnd(holder.identifier::eq,
                                criteria.getAccountHolderId())
                        .notNullAnd(regulatorNotice.processType::containsIgnoreCase, criteria.getProcessType())
                        .notNullAnd(this::taskStatusCondition, criteria.getTaskStatus())
                        .notNullAnd(compliantEntity.identifier::eq, criteria.getOperatorId())
                        .notNullAnd(this::permitOrMonitoringPlanIdentifierCondition,
                                criteria.getPermitOrMonitoringPlanIdentifier())
                        .notNullAnd(this::claimantNameCondition, criteria.getClaimantName())
                        .build()
        );

    }

    private BooleanExpression permitOrMonitoringPlanIdentifierCondition(String value) {
        if (value == null) {
            return null;
        }

        return new CaseBuilder()
                .when(installation.id.isNotNull())
                .then(installation.permitIdentifier)
                .when(aircraft.id.isNotNull())
                .then(aircraft.monitoringPlanIdentifier)
                .when(maritime.id.isNotNull())
                .then(maritime.maritimeMonitoringPlanIdentifier)
                .otherwise(Expressions.constant(""))
                .containsIgnoreCase(value);
    }

    private BooleanExpression claimantNameCondition(String claimantName) {
        if (claimantName == null) {
            return null;
        }

        return claimedBy.knownAs.isNotEmpty().and(claimedBy.knownAs.containsIgnoreCase(claimantName))
                .or(claimedBy.knownAs.isNull().or(claimedBy.knownAs.isEmpty()).and(
                        SearchUtils.getFirstNameLastNamePredicate(claimantName, claimedBy.firstName, claimedBy.lastName)));
    }

    private BooleanExpression taskStatusCondition(TaskStatus taskStatus) {
        if (taskStatus == null) {
            return null;
        }

        return switch (taskStatus) {
            case UNCLAIMED, OPEN -> regulatorNotice.claimedBy.isNull()
                    .and(regulatorNotice.status.notIn(
                            RequestStateEnum.APPROVED,
                            RequestStateEnum.REJECTED));
            case CLAIMED -> regulatorNotice.claimedBy.isNotNull()
                    .and(regulatorNotice.status.notIn(
                            RequestStateEnum.APPROVED,
                            RequestStateEnum.REJECTED));
            case COMPLETED -> regulatorNotice.status.in(
                    RequestStateEnum.APPROVED,
                    RequestStateEnum.REJECTED);
            default -> null;
        };
    }


    private QRegulatorNoticeProjection createRegulatorNoticeProjection() {

        Expression<String> permitOrMonitoringPlanIdentifier =
                new CaseBuilder()
                        .when(installation.id.isNotNull())
                        .then(installation.permitIdentifier)
                        .when(aircraft.id.isNotNull())
                        .then(aircraft.monitoringPlanIdentifier)
                        .when(maritime.id.isNotNull())
                        .then(maritime.maritimeMonitoringPlanIdentifier)
                        .otherwise(Expressions.constant(""));

        return new QRegulatorNoticeProjection(
                regulatorNotice.requestId,
                holder.firstName,
                holder.lastName,
                new CaseBuilder()
                        .when(regulatorNotice.claimedBy.knownAs.isNotEmpty())
                        .then(regulatorNotice.claimedBy.knownAs)
                        .otherwise(regulatorNotice.claimedBy.firstName),
                new CaseBuilder()
                        .when(regulatorNotice.claimedBy.knownAs.isNotEmpty())
                        .then(Expressions.stringTemplate("null"))
                        .otherwise(regulatorNotice.claimedBy.lastName),
                regulatorNotice.claimedBy.urid,
                regulatorNotice.claimedDate,
                new CaseBuilder()
                        .when(regulatorNotice.completedBy.knownAs.isNotEmpty())
                        .then(regulatorNotice.completedBy.knownAs)
                        .otherwise(regulatorNotice.completedBy.firstName),
                new CaseBuilder()
                        .when(regulatorNotice.completedBy.knownAs.isNotEmpty())
                        .then(Expressions.stringTemplate("null"))
                        .otherwise(regulatorNotice.completedBy.lastName),
                regulatorNotice.completedDate,
                holder.name,
                permitOrMonitoringPlanIdentifier,
                regulatorNotice.processType,
                regulatorNotice.initiatedDate,
                regulatorNotice.status
        );
    }

}
