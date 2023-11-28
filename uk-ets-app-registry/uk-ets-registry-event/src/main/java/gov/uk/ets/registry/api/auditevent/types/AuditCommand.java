package gov.uk.ets.registry.api.auditevent.types;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AuditCommand implements Serializable {
    private String domainType;
    private String domainId;
    private String metaInformation;
    private String creator;
    private String creatorType;
    private Date creationDate;
}
