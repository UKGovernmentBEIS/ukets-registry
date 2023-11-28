package gov.uk.ets.registry.api.allocation;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import lombok.Getter;
import lombok.Setter;

/**
 * This is for testing reasons
 */
@Entity
@Getter
@Setter
public class CompliantEntity {

    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The identifier.
     */
    private Long identifier;

    private Integer startYear;

    private Integer endYear;
    
    /**
     * The allocation classification.
     */
    @Enumerated(EnumType.STRING)
    private AllocationClassification allocationClassification;
}
