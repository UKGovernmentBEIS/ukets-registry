package gov.uk.ets.registry.api.allocation.domain;

import gov.uk.ets.registry.api.allocation.type.AllocationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation entry.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"allocationYear", "compliantEntityId", "type"})
public class AllocationEntry {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "allocation_entry_generator", sequenceName = "allocation_entry_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_entry_generator")
    private Long id;

    /**
     * The allocation year.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private AllocationYear allocationYear;

    /**
     * The compliant entity (installation / aircraft operator).
     */
    @Column(name = "compliant_entity_id")
    private Long compliantEntityId;

    /**
     * The allocation type (e.g. NAT).
     */
    @Enumerated(EnumType.STRING)
    private AllocationType type;

    /**
     * The planned quantity (entitlement).
     */
    private Long entitlement;

    /**
     * The allocated quantity.
     */
    private Long allocated;

    /**
     * The quantity returned via return of excess allocations.
     */
    private Long returned;

    /**
     * The quantity returned via allocation reversals.
     */
    private Long reversed;

}
