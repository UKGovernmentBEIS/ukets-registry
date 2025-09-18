package gov.uk.ets.registry.api.allocation.service.dto;

import gov.uk.ets.registry.api.allocation.domain.AllocationJob;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationJobStatus;
import gov.uk.ets.registry.api.common.search.SearchResult;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocationJobSearchResult implements SearchResult {

    public static AllocationJobSearchResult from(AllocationJob allocationJob) {
        AllocationJobSearchResult result = new AllocationJobSearchResult();

        result.setId(allocationJob.getId());
        result.setYear(allocationJob.getYear());
        result.setCategory(allocationJob.getCategory());
        result.setStatus(allocationJob.getStatus());
        result.setRequestIdentifier(allocationJob.getRequestIdentifier());
        result.setExecutionDate(allocationJob.getUpdated());

        return result;
    }

    private Long requestIdentifier;
    private Long id;
    private Integer year;
    private AllocationCategory category;
    private AllocationJobStatus status;
    private Date executionDate;
}
