package gov.uk.ets.registry.api.file.upload.wrappers;

import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BulkArTaskDTO {

    private final String accountFullIdentifier;
    private final String userUrid;
    private final RequestStateEnum taskStatus;
}
