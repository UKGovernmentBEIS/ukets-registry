package gov.uk.ets.registry.api.itl.notice.web.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ITLNoticeSearchCriteria implements Serializable {

    private Long notificationIdentifier;

    /**
     * The message date (from)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date messageDateFrom;

    /**
     * The message date (until)
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date messageDateTo;

    private String noticeType;

    private String noticeStatus;

}
