package uk.gov.ets.signing.api.web.error;

import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class ErrorDetail implements Serializable {
    private String code;
    private String message;
    private String identifier;
    private String urid;
}
