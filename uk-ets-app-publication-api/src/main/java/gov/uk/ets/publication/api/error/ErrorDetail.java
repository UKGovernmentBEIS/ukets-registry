package gov.uk.ets.publication.api.error;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class ErrorDetail implements Serializable {
    private String code;
    private String message;
    private String identifier;
    private String urid;
    private String componentId;
    private Long errorFileId;
    private String errorFilename;
}
