package gov.uk.ets.registry.api.compliance.web.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperatorEmissionsExclusionStatusChangeDTO  implements Serializable {

    private static final long serialVersionUID = 4007821671230577674L;

    /**
     * The year for which the exclusion status should be set.
     */
    @NotNull(message = "The year is mandatory")
    private Long year;
    
    /**
     * Indicates whether the operator is excluded for this year .
     */
    @NotNull(message = "The exclusion status is mandatory")
    private boolean excluded;
    
}
