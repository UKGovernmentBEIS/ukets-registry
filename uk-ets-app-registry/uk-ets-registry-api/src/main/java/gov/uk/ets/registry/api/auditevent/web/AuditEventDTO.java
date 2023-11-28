package gov.uk.ets.registry.api.auditevent.web;

import java.util.Date;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditEventDTO {

    private String domainId;
    private String domainAction;
    private String description;
    private String creator;
    private String creatorUserIdentifier;
    private String creatorType;
    private Date creationDate;
    
    @QueryProjection
    public AuditEventDTO(String domainId, String domainAction, String description, String creator, String creatorType,Date creationDate) {
        super();
        this.domainId = domainId;
        this.domainAction = domainAction;
        this.description = description;
        this.creator = creator;
        this.creatorType = creatorType;
        this.creationDate = creationDate;
    }

    @QueryProjection
    public AuditEventDTO(String domainId, String domainAction, String description, String creator, String creatorUserIdentifier, String creatorType,Date creationDate) {
        super();
        this.domainId = domainId;
        this.domainAction = domainAction;
        this.description = description;
        this.creator = creator;
        this.creatorUserIdentifier = creatorUserIdentifier;
        this.creatorType = creatorType;
        this.creationDate = creationDate;
    }

    @QueryProjection
    public AuditEventDTO(String domainId, String domainAction, String description, String creatorType,Date creationDate) {
        super();
        this.domainId = domainId;
        this.domainAction = domainAction;
        this.description = description;
        this.creatorType = creatorType;
        this.creationDate = creationDate;
    }
}
