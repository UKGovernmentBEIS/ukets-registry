package gov.uk.ets.registry.api.ar.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the AR Update action.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
@EqualsAndHashCode
public class ARUpdateAction implements Serializable {
    /**
     * The type of Authorized Representative Update request
     */
    private ARUpdateActionType type;
    /**
     * The urid of the authorized representative that the update request is going to be applied on
     */
    private String urid;
    /**
     * The urid of the authorized representative that is going to be replaced.
     */
    private String toBeReplacedUrid;
    /**
     * The account access right of the authorized representative that the update request is going to be applied on.
     */
    private AccountAccessRight accountAccessRight;
}
