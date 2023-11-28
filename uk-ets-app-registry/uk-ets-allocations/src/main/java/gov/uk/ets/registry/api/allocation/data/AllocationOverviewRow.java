package gov.uk.ets.registry.api.allocation.data;

import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a row in the allocation overview.
 */
@Setter
@Getter
public class AllocationOverviewRow implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 3938088256827726775L;

    /**
     * The allocation type.
     */
    private AllocationType allocationType;

    /**
     * The number of accounts.
     */
    private Integer accounts;

    /**
     * The quantity.
     */
    private Long quantity;

    /**
     * The number of excluded accounts.
     */
    private Integer excludedAccounts;

    /**
     * The number of withheld accounts.
     */
    private Integer withheldAccounts;

    /**
     * The number of closed and fully suspended accounts.
     */
    private Integer closedAndFullySuspendedAccounts;
    
    /**
     * The number of transfer pending accounts.
     */
    private Integer transferPendingAccounts;

}
