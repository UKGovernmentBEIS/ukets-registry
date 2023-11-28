package gov.uk.ets.registry.api.ar.web.model;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * The request body for restoring, suspending and removing AR.
 */
@Getter
@Setter
public class UpdateARRequest {
    /**
     * The unique business identifier of the authorized representative which state is going to be updated.
     */
    @NotBlank
    private String candidateUrid;

    private String comment;
}
