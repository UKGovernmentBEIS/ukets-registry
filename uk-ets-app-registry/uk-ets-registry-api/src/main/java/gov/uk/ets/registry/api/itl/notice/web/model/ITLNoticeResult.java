package gov.uk.ets.registry.api.itl.notice.web.model;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ITLNoticeResult implements SearchResult {

    private static final long serialVersionUID = 1L;

    private long notificationIdentifier;
    private NoticeType type;
    private Date receivedOn;
    private Date lastUpdateOn;
    private NoticeStatus status;

    @QueryProjection
    public ITLNoticeResult(long notificationIdentifier,
                           NoticeType type,
                           Date receivedOn,
                           Date lastUpdateOn,
                           NoticeStatus status) {
        this.notificationIdentifier = notificationIdentifier;
        this.type = type;
        this.receivedOn = receivedOn;
        this.lastUpdateOn = lastUpdateOn;
        this.status = status;
    }
}
