package gov.uk.ets.registry.api.account.web.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * The permit transfer object.
 */
@Getter
@Setter
@EqualsAndHashCode
public class PermitDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -8764097501268653713L;

    /**
     * The id.
     */
    @Size(max = 256, message = "Permit ID name must not exceed 256 characters.")
    String id;

    /**
     * The Permit ID unchanged is set when an installation transfer uses the same permit id
     * with the installation an RA is transferring.
     */
    Boolean permitIdUnchanged;

}
