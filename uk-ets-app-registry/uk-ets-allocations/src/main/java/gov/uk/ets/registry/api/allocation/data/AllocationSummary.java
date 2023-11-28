package gov.uk.ets.registry.api.allocation.data;

import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import java.io.Serializable;

import gov.uk.ets.registry.api.allocation.util.AllocationUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Transfer object for allocation entries.
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class AllocationSummary implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 183574245566903871L;

    /**
     * Constructor.
     * @param year The year.
     * @param status The status.
     */
    public AllocationSummary(Integer year, AllocationStatusType status) {
        this.year = year;
        this.status = status;
    }

    /**
     * Constructors.
     * @param identifier The installation / aircraft operator identifier.
     * @param year The year.
     * @param entitlement The entitlement.
     */
    public AllocationSummary(Long identifier, Integer year, Long entitlement) {
        this.identifier = identifier;
        this.year = year;
        this.entitlement = entitlement;
    }

    public AllocationSummary(Integer year, Long entitlement, Long allocated, AllocationStatusType status, Boolean excluded) {
        this.year = year;
        this.entitlement = entitlement;
        this.allocated = allocated;
        this.remaining = AllocationUtils.calculateRemainingValue(entitlement, allocated, excluded);
        this.status = status;
        this.excluded = excluded;
    }

    public AllocationSummary(Integer year, Long entitlement, Long allocated, AllocationStatusType status,
                             Boolean excluded, AllocationType type) {
        this.year = year;
        this.entitlement = entitlement;
        this.allocated = allocated;
        this.remaining = AllocationUtils.calculateRemainingValue(entitlement, allocated, excluded);
        this.status = status;
        this.excluded = excluded;
        this.type = type;
    }

    public AllocationSummary(Long compliantEntityId, Long entitlement, Long allocated, AllocationStatusType status,  Boolean excluded) {
        this.compliantEntityId = compliantEntityId;
        this.entitlement = entitlement;
        this.allocated = allocated;
        this.remaining = AllocationUtils.calculateRemainingValue(entitlement, allocated, excluded);
        this.status = status;
    }

    /**
     * The installation / aircraft operator identifier.
     */
    private Long identifier;

    /**
     * The installation / aircraft operator primary key.
     */
    private Long compliantEntityId;

    /**
     * The account full identifier.
     */
    private String accountFullIdentifier;

    /**
     * The year.
     */
    private Integer year;

    /**
     * The planned quantity (entitlement).
     */
    private Long entitlement;

    /**
     * The allocated quantity.
     */
    private Long allocated;

    /**
     * The remaining quantity.
     */
    private Long remaining;

    /**
     * The allocation status.
     */
    private AllocationStatusType status;

    /**
     * The allocation type.
     */
    private AllocationType type;

    private Boolean excluded;
}
