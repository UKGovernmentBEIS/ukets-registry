package gov.uk.ets.registry.api.note.web.model;

import gov.uk.ets.registry.api.note.domain.NoteDomainType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoteDto {

    private Long id;
    private String userIdentifier;
    private String userName;
    private String description;
    private NoteDomainType domainType;
    private String domainId;
    private Date creationDate;
}
