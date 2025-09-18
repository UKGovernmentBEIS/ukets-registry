package gov.uk.ets.registry.api.allocation.service.dto;

import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class AllocationJobSearchCriteria {

    /**
     * The request identifier
     */
    private Long requestIdentifier;

    /**
     * The allocation job id
     */
    private Long id;

    /**
     * The allocation job status
     */
    private AllocationJobStatus status;

    /**
     * The allocation job execution date (from)
     */
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private Date executionDateFrom;

    /**
     * The allocation job execution date (until)
     */
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private Date executionDateTo;
}
