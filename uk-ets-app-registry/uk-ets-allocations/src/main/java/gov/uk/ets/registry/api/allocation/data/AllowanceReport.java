package gov.uk.ets.registry.api.allocation.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Transfer object for issuance and allocation entries summary for the whole registry.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class AllowanceReport {

    /**
     * The description.
     */
    private String description;

    /**
     * The cap.
     */
    private Long cap = 0L;
    
    /**
     * The planned quantity (entitlement).
     */
    private Long entitlement  = 0L;

    /**
     * The issued quantity.
     */
    private Long issued  = 0L;
    
    /**
     * The allocated quantity.
     */
    private Long allocated  = 0L;

    /**
     * The for auction quantity.
     */
    private Long forAuction  = 0L;
    
    /**
     * The auctioned quantity.
     */
    private Long auctioned  = 0L;
}
