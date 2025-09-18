package gov.uk.ets.registry.api.allocation.domain;

import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the allocation status of an account.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"compliantEntityId", "allocationYear"})
public class AllocationStatus {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "allocation_status_generator", sequenceName = "allocation_status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_status_generator")
    private Long id;

    /**
     * The status.
     */
    @Enumerated(EnumType.STRING)
    private AllocationStatusType status;

    /**
     * The compliant entity (installation / aircraft operator).
     */
    @Column(name = "compliant_entity_id")
    private Long compliantEntityId;

    /**
     * The allocation year.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private AllocationYear allocationYear;

}
