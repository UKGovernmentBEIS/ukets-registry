package gov.uk.ets.registry.api.task.searchmetadata.handler;

import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableUploadDetails;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.task.searchmetadata.utils.QueryUtils;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.AbstractEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UploadAllocationTableHandler implements SearchMetadataHandler {

    private final Mapper mapper;

    @Override
    public boolean canHandle(Task task) {
        return RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST == task.getType();
    }

    @Override
    public void handle(Task task, AbstractEvent abstractEvent) {

        if (task.getDifference() == null) {
            return;
        }

        AllocationTableUploadDetails allocationTableUploadDetails =
            mapper.convertToPojo(task.getDifference(), AllocationTableUploadDetails.class);
        AllocationCategory allocationCategory = allocationTableUploadDetails.getAllocationCategory();

        executeUpdate(abstractEvent, QueryUtils.getQuery(false,abstractEvent),
                QueryUtils.createDefaultQueryParams(task.getId(),
                        MetadataName.ALLOCATION_CATEGORY.name(), allocationCategory.name()));
    }

}
