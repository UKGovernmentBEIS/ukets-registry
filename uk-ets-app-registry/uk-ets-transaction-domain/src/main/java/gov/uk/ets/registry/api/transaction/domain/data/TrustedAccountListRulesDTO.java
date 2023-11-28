package gov.uk.ets.registry.api.transaction.domain.data;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * The trusted account list transfer object.
 */
@Getter
@Setter
public class TrustedAccountListRulesDTO implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -4151916179137149763L;

    /**
     * The current rule 1 - Approval of a second AR is required in transfers.
     */
    private Boolean currentRule1;

    /**
     * The current rule 2 - Transfers are allowed for accounts outside the TAL.
     */
    private Boolean currentRule2;

    /**
     * The current rule 3 - Whether a single person approval is required for specific transactions.
     */
    private Boolean currentRule3;

    /**
     * The Rule1 - Approval of a second AR is required in transfers.
     */
    private Boolean rule1;

    /**
     * The Rule2 - Transfers are allowed for accounts outside the TAL.
     */
    private Boolean rule2;

    /**
     * The Rule3 - Whether a single person approval is required for specific transactions.
     */
    private Boolean rule3;

}
