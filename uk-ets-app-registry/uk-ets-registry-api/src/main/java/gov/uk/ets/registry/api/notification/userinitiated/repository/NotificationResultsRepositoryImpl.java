package gov.uk.ets.registry.api.notification.userinitiated.repository;

import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.common.search.OptionalBooleanBuilder;
import gov.uk.ets.registry.api.common.search.Search;
import gov.uk.ets.registry.api.notification.userinitiated.domain.QNotification;
import gov.uk.ets.registry.api.notification.userinitiated.domain.QNotificationDefinition;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationPropertyPath;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchCriteria;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.NotificationSearchResult;
import gov.uk.ets.registry.api.notification.userinitiated.web.model.QNotificationSearchResult;
import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class NotificationResultsRepositoryImpl implements NotificationResultsRepository {

    @PersistenceContext
    EntityManager entityManager;

    private static final QNotification notification = QNotification.notification;
    private static final QNotificationDefinition definition = QNotificationDefinition.notificationDefinition;

    private final Map<String, BeanPath<?>> sortingMap = Map.of(
        NotificationPropertyPath.NOTIFICATION_ID, notification,
        NotificationPropertyPath.NOTIFICATION_SUBJECT, notification,
        NotificationPropertyPath.NOTIFICATION_TYPE, notification.definition,
        NotificationPropertyPath.NOTIFICATION_SCHEDULED_DATE, notification.schedule,
        NotificationPropertyPath.NOTIFICATION_RECUR_DAYS, notification.schedule,
        NotificationPropertyPath.NOTIFICATION_EXPIRATION_DATE, notification.schedule,
        NotificationPropertyPath.NOTIFICATION_STATUS, notification
    );


    @Override
    public Page<NotificationSearchResult> search(NotificationSearchCriteria criteria, Pageable pageable) {
        return new Search.Builder<NotificationSearchResult>()
            .query(getNotificationResultsQuery(criteria))
            .pageable(pageable)
            .sortingMap(sortingMap)
            .build()
            .getResults();
    }

    public JPAQuery<NotificationSearchResult> getNotificationResultsQuery(NotificationSearchCriteria criteria) {
        return new JPAQuery<>(entityManager).select(
                new QNotificationSearchResult(
                    notification.id,
                    notification.shortText,
                    definition.type,
                    notification.schedule.startDateTime,
                    notification.schedule.endDateTime,
                    notification.schedule.runEveryXDays,
                    notification.status))
            .from(notification)
            .innerJoin(notification.definition, definition) // implicit join here to avoid cross-join
            .where(
                new OptionalBooleanBuilder(notification.isNotNull())
                    .notNullAnd(definition.type::eq, criteria.getType())
                    .build()
            )
            ;
    }
}
