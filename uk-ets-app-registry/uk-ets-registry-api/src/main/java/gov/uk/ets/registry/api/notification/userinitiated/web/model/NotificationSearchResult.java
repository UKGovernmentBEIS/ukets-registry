package gov.uk.ets.registry.api.notification.userinitiated.web.model;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationStatus;
import gov.uk.ets.registry.api.notification.userinitiated.domain.types.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationSearchResult implements SearchResult {
    private static final long serialVersionUID = -8448525332292925689L;
    private Long notificationId;
    private String shortText;
    private NotificationType type;
    private LocalDateTime scheduledDate;
    private LocalDateTime expirationDate;
    private Integer runEveryXDays;
    private NotificationStatus status;

    @QueryProjection
    public NotificationSearchResult(Long notificationId, String shortText, NotificationType type,
                                    LocalDateTime scheduledDate, LocalDateTime expirationDate, Integer runEveryXDays, NotificationStatus status) {
        this.notificationId = notificationId;
        this.shortText = shortText;
        this.type = type;
        this.scheduledDate = scheduledDate;
        this.expirationDate = expirationDate;
        this.runEveryXDays = runEveryXDays;
        this.status = status;
    }
}
