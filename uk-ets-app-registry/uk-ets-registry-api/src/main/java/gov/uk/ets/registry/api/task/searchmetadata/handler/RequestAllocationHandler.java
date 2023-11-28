package gov.uk.ets.registry.api.task.searchmetadata.handler;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;

import java.util.Set;
import java.util.stream.Collectors;

import gov.uk.ets.registry.api.task.searchmetadata.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.AbstractEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RequestAllocationHandler implements SearchMetadataHandler {

    private final Mapper mapper;

    @Override
    public boolean canHandle(Task task) {
        return RequestType.ALLOCATION_REQUEST == task.getType() && task.getDifference() != null;
    }

    @Override
    public void handle(Task task, AbstractEvent abstractEvent) {
        AllocationOverview diff = mapper.convertToPojo(task.getDifference(), AllocationOverview.class);

        executeUpdate(abstractEvent, QueryUtils.getQuery(false,abstractEvent),
                QueryUtils.createDefaultQueryParams(task.getId(),
                        MetadataName.ALLOCATION_YEAR.name(), diff.getYear().toString()));

        Set<AllocationCategory> categories =
            diff.getRows().keySet().stream().map(AllocationType::getCategory).collect(Collectors.toSet());

        if (categories.size() != 1) {
            return;
        }

        categories.forEach(category ->
            executeUpdate(abstractEvent, QueryUtils.getQuery(false,abstractEvent),
                    QueryUtils.createDefaultQueryParams(task.getId(),
                            MetadataName.ALLOCATION_CATEGORY.name(), category.name())));
    }

}
