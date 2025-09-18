package gov.uk.ets.registry.api.allocation.service.dto;

import gov.uk.ets.registry.api.common.search.PageableMapper;
import java.util.function.Function;
import org.springframework.data.domain.Sort;

public enum AllocationJobSortFieldParam implements PageableMapper.SortParameter {

    ALLOCATION_JOB_REQUEST_IDENTIFIER("requestIdentifier", direction -> Sort.by(direction, "requestIdentifier")),
    ALLOCATION_JOB_ID("id", direction -> Sort.by(direction, "id")),
    ALLOCATION_JOB_YEAR("year", direction -> Sort.by(direction, "year")),
    ALLOCATION_JOB_CATEGORY("category", direction -> Sort.by(direction, "category")),
    ALLOCATION_JOB_STATUS("status", direction -> Sort.by(direction, "status")),
    ALLOCATION_JOB_EXECUTION_DATE("updated", direction -> Sort.by(direction, "updated"));

    private final String sortField;

    private final Function<Sort.Direction, Sort> getSortFunc;

    AllocationJobSortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
        this.sortField = sortField;
        this.getSortFunc = getSortFunc;
    }

    @Override
    public String getName() {
        return sortField;
    }

    @Override
    public Function<Sort.Direction, Sort> getSortProvider() {
        return getSortFunc;
    }
}
