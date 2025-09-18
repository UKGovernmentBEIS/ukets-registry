package gov.uk.ets.registry.api.allocation;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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
