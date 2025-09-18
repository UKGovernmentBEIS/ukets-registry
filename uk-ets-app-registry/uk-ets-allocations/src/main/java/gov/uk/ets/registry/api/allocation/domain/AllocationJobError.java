package gov.uk.ets.registry.api.allocation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AllocationJobError {

    @Id
    @SequenceGenerator(name = "allocation_job_error_generator", sequenceName = "allocation_job_error_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allocation_job_error_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_job_id", nullable = false)
    private AllocationJob allocationJob;

    private Integer errorCode;
    private String details;
    private Date dateOccurred;
}
