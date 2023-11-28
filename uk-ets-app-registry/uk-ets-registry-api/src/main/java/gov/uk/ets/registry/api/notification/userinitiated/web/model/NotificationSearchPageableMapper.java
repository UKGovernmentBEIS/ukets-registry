package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import gov.uk.ets.registry.api.common.search.PageableMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class NotificationSearchPageableMapper extends PageableMapper {

    public NotificationSearchPageableMapper() {
        super(NotificationSearchPageableMapper.SortFieldParam.values(), NotificationSearchPageableMapper.SortFieldParam.NOTIFICATION_ID);
    }

    public enum SortFieldParam implements SortParameter {
        NOTIFICATION_ID("notificationId",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_ID)),
        NOTIFICATION_SUBJECT("subject",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_SUBJECT)),
        NOTIFICATION_TYPE("type",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_TYPE)),
        NOTIFICATION_SCHEDULED_DATE("scheduledDate",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_SCHEDULED_DATE)),
        NOTIFICATION_RECUR_DAYS("runEveryXDays",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_RECUR_DAYS)),
        NOTIFICATION_EXPIRATION_DATE("expirationDate",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_EXPIRATION_DATE)),
        NOTIFICATION_STATUS("status",
                direction -> Sort.by(direction, NotificationPropertyPath.NOTIFICATION_STATUS));

        private final String sortField;

        private final Function<Sort.Direction, Sort> getSortFunc;

        SortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
            this.sortField = sortField;
            this.getSortFunc = getSortFunc;
        }

        public String getName() {
            return sortField;
        }

        public Function<Sort.Direction, Sort> getSortProvider() {
            return getSortFunc;
        }
    }
}
