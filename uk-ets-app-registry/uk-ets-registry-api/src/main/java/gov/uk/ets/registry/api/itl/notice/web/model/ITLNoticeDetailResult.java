package gov.uk.ets.registry.api.itl.notice.web.model;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.common.search.SearchResult;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeStatus;
import gov.uk.ets.registry.api.itl.notice.domain.type.NoticeType;
import lombok.Builder;
import lombok.Getter;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class ITLNoticeDetailResult implements SearchResult {

    private static final long serialVersionUID = 1L;

    private long id;
    private String content;
    private Date messageDate;
    private NoticeType type;
    private long notificationIdentifier;
    private NoticeStatus status;
    private String projectNumber;
    private Integer unitType;
    private Long targetValue;
    private Date targetDate;
    private long lulucfActivity;
    private Integer commitPeriod;
    private Date actionDueDate;
    private Date createdDate;
    private List<ITLUnitIdentifierSearchResult> unitBlockIdentifiers;

    @QueryProjection
    public ITLNoticeDetailResult(String content,
                                 Date messageDate,
                                 NoticeType type,
                                 long notificationIdentifier,
                                 NoticeStatus status,
                                 String projectNumber,
                                 Integer unitType,
                                 Long targetValue,
                                 Date targetDate,
                                 long lulucfActivity,
                                 Integer commitPeriod,
                                 Date actionDueDate,
                                 Date createdDate,
                                 List<ITLUnitIdentifierSearchResult> unitBlockIdentifiers
    ) {
        this.content = content;
        this.messageDate = messageDate;
        this.type = type;
        this.notificationIdentifier = notificationIdentifier;
        this.status = status;
        this.projectNumber = projectNumber;
        this.unitType = unitType;
        this.targetValue = targetValue;
        this.targetDate = targetDate;
        this.lulucfActivity = lulucfActivity;
        this.commitPeriod = commitPeriod;
        this.actionDueDate = actionDueDate;
        this.createdDate = createdDate;
        this.unitBlockIdentifiers = unitBlockIdentifiers;
    }

    public ITLNoticeDetailResult(long id, String content, Date messageDate, NoticeType type, long notificationIdentifier, NoticeStatus status, String projectNumber, Integer unitType, Long targetValue, Date targetDate, long lulucfActivity, Integer commitPeriod, Date actionDueDate, Date createdDate, List<ITLUnitIdentifierSearchResult> unitBlockIdentifiers) {
        this(content, messageDate, type, notificationIdentifier, status, projectNumber, unitType, targetValue, targetDate, lulucfActivity, commitPeriod, actionDueDate, createdDate, unitBlockIdentifiers);
        this.id = id;
    }
}
