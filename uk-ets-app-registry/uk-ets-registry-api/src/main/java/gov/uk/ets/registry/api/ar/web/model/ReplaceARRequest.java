package gov.uk.ets.registry.api.ar.web.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request body for replacing an AR with an other one.
 */
@Getter
@Setter
public class ReplaceARRequest extends UpdateARRightsRequest {

    /**
     * The unique business identifier of the user of the AR which is going to be replaced.
     */
    @NotBlank
    private String replaceeUrid;
}
