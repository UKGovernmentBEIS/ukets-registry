package gov.uk.ets.registry.api.ar.web.model;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * The request body for adding an AR or changing his/her account access right.
 */
@Getter
@Setter
public class UpdateARRightsRequest extends UpdateARRequest {
    /**
     * The authorized representative access right
     */
    @NotNull
    private AccountAccessRight accessRight;
}
