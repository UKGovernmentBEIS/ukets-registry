package gov.uk.ets.registry.api.itl.notice.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.QueryDSLSearch;
import gov.uk.ets.registry.api.common.search.SearchUtils;
import gov.uk.ets.registry.api.itl.notice.domain.QITLNotification;
import gov.uk.ets.registry.api.itl.notice.domain.QITLNotificationHistory;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticePropertyPath;
import gov.uk.ets.registry.api.itl.notice.web.model.QITLNoticeResult;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeResult;
import gov.uk.ets.registry.api.itl.notice.web.model.ITLNoticeSearchCriteria;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class ITLNoticeSearchRepositoryImpl implements ITLNoticeSearchRepository {

    private static final QITLNotification noticeLog = new QITLNotification("nlg");
    private static final QITLNotificationHistory noticeLogHistory = new QITLNotificationHistory("itlNoticeResult");
    private static final QITLNotificationHistory nlhr1 = new QITLNotificationHistory("nlhr1");
    private static final QITLNotificationHistory nlh = new QITLNotificationHistory("nlh");

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<ITLNoticeResult>
    search(ITLNoticeSearchCriteria criteria, Pageable pageable) {
        Expression<Date> listSubQuery =
                ExpressionUtils.as(JPAExpressions.select(nlhr1.createdDate.min())
                .from(nlhr1)
                .where(nlhr1.notification.id.eq(noticeLog.id)),"receivedOn");

        QITLNoticeResult itlNoticeResult = new QITLNoticeResult(
                noticeLog.notificationIdentifier,
                noticeLogHistory.type,
                listSubQuery,
                noticeLogHistory.createdDate,
                noticeLogHistory.status);

        Map<String, Expression<?>> sortingMap = Stream
                .of(new Object[][] { { ITLNoticePropertyPath.NOTICE_IDENTIFIER, itlNoticeResult },
                        { ITLNoticePropertyPath.NOTICE_TYPE, itlNoticeResult },
                        { ITLNoticePropertyPath.NOTICE_DATE_RECEIVED_ON, itlNoticeResult },
                        { ITLNoticePropertyPath.NOTICE_DATE_UPDATED_ON, itlNoticeResult },
                        { ITLNoticePropertyPath.NOTICE_STATUS, itlNoticeResult } })
                .collect(Collectors.toMap(data -> (String) data[0], data -> (Expression<?>) data[1]));

        JPAQuery<ITLNoticeResult> querywithGroup = new JPAQuery<>(entityManager)
                .select(itlNoticeResult).distinct()
                .from(noticeLogHistory).innerJoin(noticeLog).on(noticeLogHistory.notification.id.eq(noticeLog.id))
                .where(noticeLogHistory.createdDate.in(
                        JPAExpressions.select(nlh.createdDate.max())
                                .from(nlh)
                                .where(noticeLog.id.eq(nlh.notification.id))
                ))
                .where(new OptionalBooleanBuilder(noticeLogHistory.isNotNull()
                .and(noticeLogHistory.notification.isNotNull()))
                .notNullAnd(this::getNotificationIdentifier, criteria.getNotificationIdentifier())
                .notNullAnd(this::getNoticeType, criteria.getNoticeType())
                .notNullAnd(this::getNoticeStatus, criteria.getNoticeStatus())
                .notNullAnd(this::getMessageDateFrom, criteria.getMessageDateFrom())
                .notNullAnd(this::getMessageDateTo, criteria.getMessageDateTo()).build());

        return new QueryDSLSearch.Builder<ITLNoticeResult>().pageable(pageable).sortingMap(sortingMap)
                .query(querywithGroup).build().getResults();
    }

    private BooleanExpression getNotificationIdentifier(long notificationIdentifier) {
        return noticeLog.notificationIdentifier.eq(notificationIdentifier);
    }

    private BooleanExpression getMessageDateFrom(Date messageDateFrom) {
        return SearchUtils.getFromDatePredicate(messageDateFrom, noticeLogHistory.createdDate);
    }

    private BooleanExpression getMessageDateTo(Date messageDateTo) {
        return SearchUtils.getUntilDatePredicate(messageDateTo, noticeLogHistory.createdDate);
    }

    private BooleanExpression getNoticeStatus(String noticeStatus) {
        return noticeLogHistory.status.eq(NoticeStatus.valueOf(noticeStatus));
    }

    private BooleanExpression getNoticeType(String noticeType) {
        return noticeLogHistory.type.eq(NoticeType.valueOf(noticeType));
    }
}
