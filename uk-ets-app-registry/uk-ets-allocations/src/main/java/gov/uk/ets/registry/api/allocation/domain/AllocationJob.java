package gov.uk.ets.registry.api.allocation.domain;

import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an allocation job.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"requestIdentifier"})
public class AllocationJob {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "allocation_job_generator", sequenceName = "allocation_job_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_job_generator")
    private Long id;

    /**
     * The allocation year.
     */
    private Integer year;

    /**
     * The allocation category.
     */
    @Enumerated(EnumType.STRING)
    private AllocationCategory category;

    /**
     * The status.
     */
    @Enumerated(EnumType.STRING)
    private AllocationJobStatus status;

    /**
     * The request identifier.
     */
    private Long requestIdentifier;

    /**
     * The date when the job was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    /**
     * The date when it was last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * The allocation job errors.
     */
    @OneToMany(mappedBy = "allocationJob", fetch = FetchType.LAZY)
    private List<AllocationJobError> errors = new ArrayList<>();

}
